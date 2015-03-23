package ru.denivip.nine00secondssdk.example.map;

import ru.denivip.nine00secondssdk.data.StreamData;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

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
