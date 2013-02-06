
package com.edgeorge.graphtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;


public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((Button) findViewById(R.id.btn_simple)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGraphActivity(SimpleGraph.class);
			}
		});
		
		((Button) findViewById(R.id.btn_realtime)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGraphActivity(RealtimeGraph.class);
			}
		});
		((Button) findViewById(R.id.btn_adv)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGraphActivity(AdvancedGraph.class);
			}
		});
	}

	private void startGraphActivity(Class<? extends Activity> activity) {
		Intent intent = new Intent(MainActivity.this, activity);
		if (((CompoundButton) findViewById(R.id.check_box)).isChecked()){
			intent.putExtra("fixed", "true");
		}else{
			intent.putExtra("fixed", "false");
		}
		if (((RadioButton) findViewById(R.id.radio_bar)).isChecked()) {
			intent.putExtra("type", "bar");
		} else {
			intent.putExtra("type", "line");
		}
		if(((RadioButton) findViewById(R.id.radio_quick)).isChecked()){
			intent.putExtra("speed", "fast");
		}else{
			intent.putExtra("speed", "slow");
		}
		startActivity(intent);
	}
}