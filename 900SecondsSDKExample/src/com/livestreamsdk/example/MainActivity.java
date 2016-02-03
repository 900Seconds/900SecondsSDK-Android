package com.livestreamsdk.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onMapClick(View v){
      Intent intent = new Intent(MainActivity.this, MapActivity.class);
      startActivity(intent);
	}
	
	public void onStreamsListClick(View v){
		Intent intent = new Intent(MainActivity.this, StreamsListActivity.class);
		startActivity(intent);
	}
	
	public void onStreamingClick(View v){
		Intent intent = new Intent(MainActivity.this, CameraStreamingActivity.class);
		startActivity(intent);
	}

}
