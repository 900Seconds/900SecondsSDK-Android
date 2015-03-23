package ru.denivip.nine00secondssdk.example;

import ru.denivip.nine00secondssdk.hlsstreaming.Nine00SecondsCameraView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class CameraStreamingActivity extends Activity {

	private Nine00SecondsCameraView streamingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_streaming_activity);
		streamingView = (Nine00SecondsCameraView) findViewById(R.id.streamingView);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		streamingView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		streamingView.onPause();
	}

    /**
     * onClick handler for "record" button.
     */
    public void clickToggleRecording(View unused) {
    	if (streamingView.isRecording())
    		streamingView.stopRecording();
    	else
    		streamingView.startRecording();
    	
        updateControls();
    }
    

    /**
     * Updates the on-screen controls to reflect the current state of the app.
     */
    private void updateControls() {
        ImageButton toggleRelease = (ImageButton) findViewById(R.id.toggleRecording_button);
        int id = streamingView.isRecording() ?
                R.drawable.stop_reccord_button : R.drawable.reccord_button;
        toggleRelease.setImageResource(id);
    }
    
}
