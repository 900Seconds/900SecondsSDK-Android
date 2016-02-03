package com.livestreamsdk.example;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.livestreamsdk.Nine00SecondsSDK;
import com.livestreamsdk.Nine00SecondsSDK.FilterType;
import com.livestreamsdk.Nine00SecondsSDK.RequestErrorType;
import com.livestreamsdk.data.StreamData;
import com.livestreamsdk.data.StreamsListData;
import com.livestreamsdk.example.map.Nine00ClusterMarker;
import com.livestreamsdk.example.map.Nine00MarkerRenderer;
import com.livestreamsdk.location.LocatorCallback;

public class MapActivity extends Activity implements OnMapReadyCallback, OnClusterClickListener<Nine00ClusterMarker>, OnClusterItemClickListener<Nine00ClusterMarker> {

	private ArrayList<StreamData> streams = new ArrayList<StreamData>();
	private GoogleMap map;
	private LatLng currentMapLatLNG;
	private ClusterManager<Nine00ClusterMarker> mClusterManager;
	private Date maxCreatedDate;
	private Date minCreatedDate;
	private int minPopularity = Integer.MAX_VALUE;
	private int maxPopularity = Integer.MIN_VALUE;
	private boolean needToCenterOnCurrentLocation = true;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		maxCreatedDate = new Date(Long.MIN_VALUE);
		minCreatedDate = new Date(Long.MAX_VALUE);
		getActionBar().hide();
		setContentView(R.layout.map_activity);
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	protected void requestStreams() {
		Nine00SecondsSDK.fetchStreamsNearCoordinate(new Nine00SecondsSDK.RequestStreamsListCallback() {
			
			@Override
			public void onFault(RequestErrorType error) {
			}
			
			@Override
			public void onComplete(final StreamsListData streams) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateStreamsList(streams.getStreams());
					}
				});
			}
		}, currentMapLatLNG, -1, -1, FilterType.ALL);
	}
	
	protected void updateStreamsList(ArrayList<StreamData> streams) {
		mClusterManager.clearItems();
		for (int i = 0; i < streams.size(); i++) {
			StreamData streamData = streams.get(i); 
			// calculating min and max dates and popularity
			if (streamData.getCreatedAt().compareTo(maxCreatedDate) > 0)
				maxCreatedDate = streamData.getCreatedAt();

			if (streamData.getCreatedAt().compareTo(minCreatedDate) < 0)
				minCreatedDate = streamData.getCreatedAt();
			
			
			maxPopularity = Math.max(maxPopularity, streamData.getPopularity());
			minPopularity = Math.min(minPopularity, streamData.getPopularity());
			
			mClusterManager.addItem(new Nine00ClusterMarker(streamData));
		}
		for (StreamData streamData : streams) {
			streamData.setRelativePopularity((int) ((streamData.getPopularity() - minPopularity)*100f/(maxPopularity - minPopularity)));
			streamData.setRelativeCreated((int) ((streamData.getCreatedAt().getTime() - minCreatedDate.getTime())*100f/(maxCreatedDate.getTime() - minCreatedDate.getTime())));
		}
		mClusterManager.cluster();
	}

	protected void updatePins() {
		if (map == null || streams.size() == 0)
			return;
		mClusterManager.cluster();
	}

	@Override
	public void onMapReady(GoogleMap gMap) {
		this.map = gMap;
		
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setMapToolbarEnabled(false);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		
		mClusterManager = new ClusterManager<Nine00ClusterMarker>(this, map);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
		
		mClusterManager.setRenderer(new Nine00MarkerRenderer(this, gMap, mClusterManager));
		
		currentMapLatLNG = map.getCameraPosition().target;
		
		map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				Log.i("MapActivity", "camera position changed");
				mClusterManager.onCameraChange(cameraPosition);
				currentMapLatLNG = map.getCameraPosition().target;
				requestStreams();
			}
		});
		map.setOnMarkerClickListener(mClusterManager);
	}
    @Override
    public boolean onClusterClick(Cluster<Nine00ClusterMarker> cluster) {
    	LatLngBounds.Builder builder = new LatLngBounds.Builder();
    	for (Nine00ClusterMarker marker : cluster.getItems()) {
    	    builder.include(marker.getPosition());
    	}
    	LatLngBounds bounds = builder.build();
    	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
    	map.animateCamera(cameraUpdate);
        return true;
    }

    @Override
    public boolean onClusterItemClick(Nine00ClusterMarker item) {
        Intent intent = new Intent(MapActivity.this, PlayStreamActivity.class);
        intent.putExtra(PlayStreamActivity.INTENT_STREAM_ID, item.getStreamData().getStreamId());
        MapActivity.this.startActivity(intent);
        return true;
    }
    
	@Override
    protected void onResume() {
    	super.onResume();
    	Nine00SecondsSDK.getLocator().startUpdates(new LocatorCallback() {
			
			@Override
			public void onLocationChanged(Location location, Location preciseLocation) {
				if (needToCenterOnCurrentLocation){
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(Nine00SecondsSDK.getLocator().getLatLng(), 5);
					map.moveCamera(cameraUpdate);
				}
				needToCenterOnCurrentLocation = false;
			}
		});
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Nine00SecondsSDK.getLocator().stopUpdates();
    }
}
