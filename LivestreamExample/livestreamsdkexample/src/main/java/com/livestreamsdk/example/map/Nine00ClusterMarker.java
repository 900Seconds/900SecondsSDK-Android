package com.livestreamsdk.example.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.livestreamsdk.data.StreamData;

public class Nine00ClusterMarker implements ClusterItem {

	private StreamData streamData;
	public Nine00ClusterMarker(StreamData streamData) {
		this.streamData = streamData;
	}
	@Override
	public LatLng getPosition() {
		return streamData.getLocation().getLatLng();
	}

	public StreamData getStreamData() {
		return streamData;
	}
}
