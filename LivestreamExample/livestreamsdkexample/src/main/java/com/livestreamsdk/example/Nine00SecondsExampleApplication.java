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
		TLog.addTarget(new LogCatTarget("LIVESTREAM_EXAMPLE"));
		
		Nine00SecondsSDK.registerAppIDWithSecret(getBaseContext(),
				"__test_app_id", 
				"Roophohro2kei2shiMe7");
		
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
