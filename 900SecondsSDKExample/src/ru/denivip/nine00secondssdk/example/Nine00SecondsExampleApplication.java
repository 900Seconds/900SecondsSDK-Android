package ru.denivip.nine00secondssdk.example;

import ru.denivip.nine00secondssdk.Nine00SecondsSDK;
import ru.denivip.nine00secondssdk.Nine00SecondsSDK.StorageType;
import android.app.Application;

public class Nine00SecondsExampleApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		Nine00SecondsSDK.init(getBaseContext(), getPackageName(), StorageType.AMAZON_S3);
	}
}
