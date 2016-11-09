package com.livestreamsdk.example;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;

import com.livestreamsdk.log.TLog;
import com.livestreamsdk.streaming.CameraFragment;
import com.livestreamsdk.streaming.HLSQualityPreset;
import com.livestreamsdk.streaming.Nine00SecondsCameraViewListener;

import java.util.concurrent.TimeUnit;

public class CameraStreamingActivity extends FragmentActivity implements Nine00SecondsCameraViewListener {

	private CameraFragment cameraFragment; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_streaming_activity);
		if (savedInstanceState == null) {
			cameraFragment = CameraFragment.getInstance();
			cameraFragment.setQuality(HLSQualityPreset.PRESET_640x480);

//			cameraFragment.setScalePreviewToFit(true);

			int orientation = getScreenOrientation();
			cameraFragment.setStreamingOrientation(orientation);
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.cameraViewContainer, cameraFragment)
			.commit();
			
			cameraFragment.setCameraViewListener(this);
			updateVideoViewWithConfig(getResources().getConfiguration());
		}
	}
	
    /**
     * onClick handler for "record" button.
     */
    public void clickToggleRecording(View unused) {
    	if (cameraFragment.isRecording())
    		cameraFragment.stopRecording();
    	else
    		cameraFragment.startRecording();
    	
        updateControls();
    }
    

    /**
     * Updates the on-screen controls to reflect the current state of the app.
     */
    private void updateControls() {
        ImageButton toggleRelease = (ImageButton) findViewById(R.id.toggleRecording_button);
        int id = cameraFragment.isRecording() ?
                R.drawable.stop_reccord_button : R.drawable.reccord_button;
        toggleRelease.setImageResource(id);
    }

	@Override
	public void onStartRecord(String streamId) {
	}

	@Override
	public void onStopRecord(String streamId) {
	}

	@Override
	public void onRecordingTimeChange(long recordingTime) {
		String ms = String.format("%02d:%02d", 
				TimeUnit.MILLISECONDS.toMinutes(recordingTime) % TimeUnit.HOURS.toMinutes(1),
			    TimeUnit.MILLISECONDS.toSeconds(recordingTime) % TimeUnit.MINUTES.toSeconds(1));
		
		Log.d("900 SECONDS", "Recording time is " + ms);
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateVideoViewWithConfig(newConfig);
    }
    
    private void updateVideoViewWithConfig(final Configuration config){
    	int orientation = getScreenOrientation();

		cameraFragment.setStreamingOrientation(orientation);
    	cameraFragment.setPreviewOrientation(orientation);
		
    }
    
	protected int getScreenOrientation() {
	    int rotation = getWindowManager().getDefaultDisplay().getRotation();
	    DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;
	    int height = dm.heightPixels;
	    int orientation;
	    // if the device's natural orientation is portrait:
	    if ((rotation == Surface.ROTATION_0
	            || rotation == Surface.ROTATION_180) && height > width ||
	        (rotation == Surface.ROTATION_90
	            || rotation == Surface.ROTATION_270) && width > height) {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            default:
	                TLog.e(this, "Unknown screen orientation. Defaulting to " +
	                        "portrait.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;              
	        }
	    }
	    // if the device's natural orientation is landscape or if the device
	    // is square:
	    else {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            default:
	                TLog.e(this, "Unknown screen orientation. Defaulting to " +
	                        "landscape.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;              
	        }
	    }

	    return orientation;
	}
    
}
