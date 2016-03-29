package com.livestreamsdk.example;

import android.app.Application;
import android.util.Log;

import com.livestreamsdk.Nine00SecondsSDK;
import com.livestreamsdk.amazon.AmazonSender.ChunkData;
import com.livestreamsdk.events.Event;
import com.livestreamsdk.events.IEventListener;
import com.livestreamsdk.events.StreamEvent;
import com.livestreamsdk.log.LogCatTarget;
import com.livestreamsdk.log.TLog;

public class Nine00SecondsExampleApplication extends Application implements IEventListener {

	@Override
	public void onCreate() {
		super.onCreate();
		TLog.addTarget(new LogCatTarget("EYEYE"));
		
		Nine00SecondsSDK.registerAppID(getBaseContext(),
				"__test_app_id", 
				"fb1c9fe790674fdc9adcc8c6e6ae7866%2448ee433685c976b1a839d09a96fc9db61b3773774ab5f1be39b5aa4d0b194f46a667bcdc7b1d04b0e352c9d1d2c4d5b0b007986bbd345f2e21ad0f53fcb62cd5");
		
		Nine00SecondsSDK.getDispatcher().addEventListener(StreamEvent.CHUNK_SENDED, this);
		Nine00SecondsSDK.getDispatcher().addEventListener(StreamEvent.BROADCAST_COMPLETED, this);
	}

	@Override
	public void onEvent(Event event) {
		StreamEvent streamEvent = (StreamEvent) event;
		if (event.getType().equals(StreamEvent.BROADCAST_CREATED)){
			Log.d("Livestream SDK", "Broadcast created");
		} else if (event.getType().equals(StreamEvent.CHUNK_SENDED)){
			ChunkData chunkData = streamEvent.getChunkData();
			Log.d("Livestream SDK", "Chunk " + chunkData.number + " uploaded: " + streamEvent.getStreamId());
		} else if (event.getType().equals(StreamEvent.BROADCAST_COMPLETED)){
			Log.d("Livestream SDK", "Broadcast upload complete: " + streamEvent.getStreamId());
		}
	}
}
