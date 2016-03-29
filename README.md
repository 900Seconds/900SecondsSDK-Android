# LiveStreamingSDK
Live Streaming Video SDK for Android

LiveStreaming SDK is a video streaming library made for easily add geolocated live streaming into your mobile app. It encapsulates the whole stack of video streaming-related tech involved in live streaming process such as handling device camera, processing video with filters and encoding video chunks with ffmpeg, uploading them to your file storage. Also it has a backend that handles app authorization, stores streams objects and can be requested for fetching available streams.

Some of the features the SDK handles:
- shooting the video with device camera
- applying video effects (blur, distrtions, bitmap overlaying over video, etc) on the fly
- compress video chunks and upload them to a file storage
- fetching available streams around the geolocation
- video streams playback

## Quick start guide
1. [Intallation](https://github.com/900Seconds/900SecondsSDK-Android#installation-and-dependencies)
2. [Basic usage](https://github.com/900Seconds/900SecondsSDK-Android#basic-usage)
3. [Example application](https://github.com/900Seconds/900SecondsSDK-Android#example-application)
4. [Other Requirements](https://github.com/900Seconds/900SecondsSDK-Android#other-requirements)

### Installation and dependencies
For LiveStreaming SDK using needs following:
- jcenter repository in `build.gradle`
```Gradle
repositories {
   jcenter()
}
```
- include the livestream-sdk dependency:
```Gradle
dependencies {
    // ... other dependencies here.     
    compile 'com.livestream:livestream-sdk:0.1.1'
}
```

### Basic usage
Overview of all basic library features with comments.

#### Autorizing the app
First of all, you need to register your app with 900Seconds web interface. Registration will require your application ID. Also in order for library to be able to stream video you have to provide it with an access to your file storage. Currently only AWS S3 is supported. You need to register on AWS, get credentials for file storage and use them while registering your app for 900Seconds SDK. In return 900Seconds will give you secret key which you can use for authorizing the app with SDK.

Authorizing itself is very easy and require just one call to _Nine00SecondsSDK_:
```java
Nine00SecondsSDK.registerAppID(getBaseContext(),
		"yourAppId", 
		"yourSecretKey");
```
This call should be made on startup. Presumably on your application class but it's really your choice, just make it before your start using the SDK.

For author autorization you should use:
```java
Nine00SecondsSDK.loginWithAuthorId(userId);
```

`userId` is unique string. You can generate it randomly or use userId from other API like facebook, twitter etc.


#### Recording video
LiveStreaming SDK has all camera-related and ffmpeg encoding logic inside already so you don't need to set anything at all. To start preview video feed from the camera just add _CameraFragment_ to your view hierarchy. your_camera_activity_layout.xml may look like:

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
    
    <FrameLayout
        android:clickable="false"
        android:id="@+id/cameraViewContainer"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!--
		You can add your design features here like start streaming button etc.
	-->

</RelativeLayout>
```

and in YourCameraStreamingActivity add:

```java
public class YourCameraStreamingActivity extends Activity {

	private CameraFragment cameraFragment; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.your_camera_streaming_activity);
		if (savedInstanceState == null) {
			cameraFragment = CameraFragment.getInstance();
			cameraFragment.setQuality(HLSQualityPreset.PRESET_640x480); //set your quality

			cameraFragment.setScalePreviewToFit(true); // Set to true if needs scale preview to fit inside camera fragment
			
			cameraFragment.setStreamingOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // set needed streaming orientation
			
			getFragmentManager().beginTransaction()
			.replace(R.id.cameraViewContainer, cameraFragment)
			.commit();
			
			cameraFragment.setCameraViewListener(this); // optional. start/stop/recording time change events
		}
	}
```

You can change camera for recording with
```java
cameraFragment.requestCamera(cameraNum);
```

Or set live filter to be applied over stream (both broadcast and preview)
```java
cameraFragment.applyVideoFilter(Filters.FILTER_BLACK_WHITE); // or other filter from  com.livestreamsdk.streaming.Filters class
```

To start streaming you need just another one call:
```java
cameraFragment.startRecording();
```

This call will create stream object on the backend and start writing and uploading video chunks to the file storage. Also it will start observing your location as every stream in our SDK obliged to have a location. In order to keep user's privacy coordinates are taken with precision up to only 100 meters.

To stop streaming and preview just call:
```java
cameraFragment.stopRecording();
```

Most of preview and broadcasting calls are asynchronous. Add _Nine00SecondsCameraViewListener_ to know exactly when start/stop methods have finished:
```java
			
cameraFragment.setCameraViewListener(new Nine00SecondsCameraViewListener() {
	
	@Override
	public void onStartRecord(String streamId) {
	}
	
	@Override
	public void onStopRecord(String streamId) {
		.....
		finish(); // for example
	}
	
	@Override
	public void onRecordingTimeChange(long recordingTime) {
		String ms = String.format("%02d:%02d", 
				TimeUnit.MILLISECONDS.toMinutes(recordingTime) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(recordingTime) % TimeUnit.MINUTES.toSeconds(1));
		
		if (lastShowedTime.equals(ms))
			return;
		
		lastShowedTime = ms;
		Log.i("TEST_APP", "Recording time is:" + ms);
	}
});
```

#### Fetching live streams
All the streams made by your application with the SDK can be fetched from backend as an object _StreamsListData_ that contains array of _StreamData_ objects. _StreamData_ is a model object which contains information about stream such as it's ID, author ID, when it was started or stopped, how much it was watched through it's lifetime and so on.

There are two main options for fetching streams.
The first is to fetch streams with radius around some location point. To perform this fetch you should call
```java
Nine00SecondsSDK.fetchStreamsNearCoordinate(new Nine00SecondsSDK.RequestStreamsListCallback() {

	@Override
	public void onFault(RequestErrorType error) {
	}

	@Override
	public void onComplete(final StreamsListData streamsListData) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateStreamsList(streamsListData.getStreams()); // for example
				totalBrodcasts.setText(String.valueOf(streamsListData.getTotal())); // for example
			}
		});
	}
}, currentMapLatLNG, -1, -1, currentFilterType);
```

Fetch will request the backend for list of streams satisfying the parameters. When backend responds the completion will be called. You can set _radius_ to -1 or _since_ to -1 to ignore those parameters, it will return all the streams made by this application.

The second fetch option is to fetch last 30 streams made by specific author.
```java
Nine00SecondsSDK.fetchRecentStreams(new Nine00SecondsSDK.RequestStreamsListCallback() {
	
	@Override
	public void onFault(RequestErrorType error) {
	}
	
	@Override
	public void onComplete(StreamsListData streamsListData) {
		updateStreamsList(streamsListData.getStreams()); // for example
	}
}, userId);
```

By default any _StreamData_ object has authorID property set to some unique string generated on first app launch. So every stream has an identifier of an application on a particular device it was made with. Passing this identifier to this method will specify which user streams you want to fetch.
To fetch streams made by current user you have to pass current application's authorID to this method.



#### Stream playback
To play any stream you can use any player you want. You can play this URL with _VideoView_ or any other player.
LiveStream SDK can evaluate the popularity of each stream. To make this happen the application must to notify the LiveStream backend that someone started watching the stream. 
```java
Nine00SecondsSDK.notifyStreamWatched(null, streamId);
```

For playing stream you must know url for hls stream. You can get this with
```java
String streamUrl = Nine00SecondsSDK.getStreamURL(streamId);
videoView.setVideoPath(path); // for example
```


## Example application
You can get an example application from [git repository](https://github.com/900Seconds/900SecondsSDK-Android)
It contains code example for camera streaming, stream playing, streams list retrieving and other.


## Other Requirements
Your app have to require minSdkVersion 18:
```Gradle
android {
....
    defaultConfig {
		.....
        minSdkVersion 18
    }
}
```
