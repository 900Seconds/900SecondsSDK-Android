package com.livestreamsdk.example;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			List<String> permissionsNeeded = new ArrayList<String>();

			final List<String> permissionsList = new ArrayList<String>();
			if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
				permissionsNeeded.add("GPS");
			if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
				permissionsNeeded.add("Write to storage");
			if (!addPermission(permissionsList, Manifest.permission.CAMERA))
				permissionsNeeded.add("Camera");
			if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
				permissionsNeeded.add("Audio");
			if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
				permissionsNeeded.add("Location");

			if (permissionsList.size() > 0) {
				if (permissionsNeeded.size() > 0) {
					requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
							REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
				}
			}
		}
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!shouldShowRequestPermissionRationale(permission))
				return false;
		}
		return true;
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
