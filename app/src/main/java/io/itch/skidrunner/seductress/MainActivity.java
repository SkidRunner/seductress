package io.itch.skidrunner.seductress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.logging.Logger;
import android.view.Window;
import android.view.WindowManager;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.content.res.Configuration;
import android.provider.Settings;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
       		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setContentView(R.layout.activity_main);
	}

}
