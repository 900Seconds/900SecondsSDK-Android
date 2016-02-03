package com.livestreamsdk.example;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.livestreamsdk.Nine00SecondsSDK;

public class PlayStreamActivity extends Activity
{
    public static final String	INTENT_STREAM_ID	= "streamId";
	private VideoView myVideoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
 
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String streamId = getIntent().getStringExtra(INTENT_STREAM_ID);
        String path = Nine00SecondsSDK.getStreamURL(streamId);
 
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        // set the main layout of the activity
        setContentView(R.layout.play_stream_activity);
 
        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(PlayStreamActivity.this);
        }
 
        //initialize the VideoView
        myVideoView = (VideoView) findViewById(R.id.video_view);
 
        // create a progress bar while the video file is loading
        progressDialog = new ProgressDialog(PlayStreamActivity.this);
        // set a title for the progress bar
        progressDialog.setTitle(getString(R.string.play_stream_preload));
        // set a message for the progress bar
        progressDialog.setMessage(getString(R.string.loading));
        //set the progress bar not cancelable on users' touch
        progressDialog.setCancelable(false);
        // show the progress bar
        progressDialog.show();
 
        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);
 
            //set the uri of the video to be played
            myVideoView.setVideoPath(path);
 
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            progressDialog.dismiss();
        }
 
        myVideoView.requestFocus();
        
        Nine00SecondsSDK.notifyStreamWatched(null, streamId);
        
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
         
            public void onPrepared(MediaPlayer mediaPlayer) {
                // close the progress bar and play the video
                progressDialog.dismiss();
                //if we have a position on savedInstanceState, the video playback should start from here
                myVideoView.seekTo(position);
                if (position == 0) {
                    myVideoView.start();
                } else {
                    //if we come from a resumed activity, video playback will be paused
                    myVideoView.pause();
                }
            }
        });
        myVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				progressDialog.dismiss();
				return false;
			}
		});
 
    }
 
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we use onSaveInstanceState in order to store the video playback position for orientation change
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        myVideoView.pause();
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //we use onRestoreInstanceState in order to play the video playback from the stored position 
        position = savedInstanceState.getInt("Position");
        myVideoView.seekTo(position);
    }
}
