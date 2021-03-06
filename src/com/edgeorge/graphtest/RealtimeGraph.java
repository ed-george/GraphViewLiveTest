package com.edgeorge.graphtest;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
//import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class RealtimeGraph extends Activity {
	private final Handler mHandler = new Handler();
	private Runnable mTimer1;
	private GraphView graphView;
	private GraphViewSeries exampleSeries1;
    private SigListener sig; 
    private TelephonyManager tm;
    private double lastXValue = 1.0d;
    private int duration = 2000; //default
    private SensorManager mSensorManager;
    private ShakeListener mSensorListener;
    
	private double getSIG() {
		if(sig.isGSMPhone()){
			double s = (double) sig.getGSMSig(); 
			/*if(showToastMsg){
			Toast.makeText(RealtimeGraph.this, "Sig: " + Double.toString(s), Toast.LENGTH_SHORT).show();
			}*/
			
			  TextView t = (TextView)findViewById(R.id.sig_text); 
			  t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			   t.setText(Double.toString(s));
			   
			   //TODO: 'Colour Algorithm'
			   int g = (int) (s*10);
			   int r = (int) (198 - s);
			   t.setTextColor(Color.rgb(r,g,0));
               //------------------------
			   
			return s;
			
		}
		
		return 0.0;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphs);
        sig = new SigListener(); 
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(sig,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
		// init example series data
		exampleSeries1 = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(1, 0.0d) //start at 0,0 -  change to getSIG()??
				
		});

		// graph with dynamically genereated horizontal and vertical labels
		if (getIntent().getStringExtra("type").equals("bar")) {
			graphView = new BarGraphView(
					this // context
					, "Mobile Signal Analysis" // heading
			);
		} else {
			graphView = new LineGraphView(
					this // context
					, "Mobile Signal Analysis" // heading
			);
			((LineGraphView) graphView).setDrawBackground(true);
		}
		graphView.addSeries(exampleSeries1); // data

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
//		graphView.setVerticalLabels(new String[] {"99", "0"});
		if (getIntent().getStringExtra("fixed").equals("true")){
		graphView.setManualYAxisBounds(99.0, 0.0);
		}
		
		if (getIntent().getStringExtra("speed").equals("fast")){
			duration = 500;
			//showToastMsg = false;
		}else{
			duration = 2000;
			//showToastMsg = true;
		}
		graphView.setHorizontalLabels(new String[] {"Time"});
		graphView.setViewPort(0,50);
		//graphView.setLegendAlign(LegendAlign.BOTTOM);  
		//graphView.setShowLegend(true);
		//graphView.setCameraDistance(distance)
		graphView.setHorizontalLabels(new String[] {""});
		graphView.setScrollable(true);
		layout.addView(graphView);

		
		 mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    mSensorListener = new ShakeListener();   

		    mSensorListener.setOnShakeListener(new ShakeListener.OnShakeListener() {

		      public void onShake() {
		       
		    	  Handler handler = new Handler(); 
		    	    handler.postDelayed(new Runnable() { 
		    	         public void run() { 
		    	        	 exampleSeries1.resetData(new GraphViewData[] {
		    	     				new GraphViewData(1, getSIG())});
		    	     		      
		    	     		       Toast.makeText(RealtimeGraph.this, "Data Reset", Toast.LENGTH_LONG).show();
		    	         } 
		    	    }, 2000);
		      
		       
		      }});
		
		// ----------
//		exampleSeries2 = new GraphViewSeries(new GraphViewData[] {
//				new GraphViewData(1, 2.0d)
//				, new GraphViewData(2, 1.5d)
//				, new GraphViewData(2.5, 3.0d) // another frequency
//				, new GraphViewData(3, 2.5d)
//				, new GraphViewData(4, 1.0d)
//				, new GraphViewData(5, 3.0d)
//		});
//
//		// graph with custom labels and drawBackground
//		if (getIntent().getStringExtra("type").equals("bar")) {
//			graphView = new BarGraphView(
//					this
//					, "GraphViewDemo"
//			);
//		} else {
//			graphView = new LineGraphView(
//					this
//					, "GraphViewDemo"
//			);
//			((LineGraphView) graphView).setDrawBackground(true);
//		}
//		graphView.addSeries(exampleSeries2); // data
//		graphView.setViewPort(0, 4);
//		graphView.setScalable(true);
		

//		layout = (LinearLayout) findViewById(R.id.graph2);
//		layout.addView(graphView);
	}

	@Override
	protected void onPause() {
		mHandler.removeCallbacks(mTimer1);
		tm.listen(sig,PhoneStateListener.LISTEN_NONE);
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		tm.listen(sig,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
		mSensorManager.registerListener(mSensorListener,
		        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_UI);
		
		mTimer1 = new Runnable() {
			@Override
			public void run() {
				lastXValue += 1d;
				exampleSeries1.appendData(new GraphViewData(lastXValue, getSIG()), true);
				graphView.setHorizontalLabels(new String[] {""});
				//graphView.redrawAll();
				mHandler.postDelayed(this, duration);
			}
		};
		mHandler.postDelayed(mTimer1, duration); //was 2000

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	}
	
	
	
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		tm.listen(sig,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//		mTimer1 = new Runnable(){
//			@Override
//			public void run() {
//				lastXValue += 1d;
//				exampleSeries1.appendData(new GraphViewData(lastXValue, getSIG()), true);
//				mHandler.postDelayed(this, 1000);
//			}
//		};
//		mHandler.postDelayed(mTimer1, 2000);
//	
//	}
	

//		mTimer2 = new Runnable() {
//			@Override
//			public void run() {
//				graph2LastXValue += 1d;
//				exampleSeries2.appendData(new GraphViewData(graph2LastXValue, getRandom()), true);
//				mHandler.postDelayed(this, 1000);
//			}
//		}
//		mHandler.postDelayed(mTimer2, 1000);
	
}
