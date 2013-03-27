package com.karthik;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	/** Called when the activity is first created. */
	protected String _path;
	boolean _taken;
	Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button helpButton = (Button) findViewById(R.id.helpButton);
		Button aboutButton = (Button) findViewById(R.id.aboutButton);
		Button clickButton = (Button) findViewById(R.id.clickButton);
		_path = Environment.getExternalStorageDirectory() + "/images";
		
		clickButton.setOnClickListener(new ButtonClickHandler());
		
		helpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this,
						HelpActivity.class);
						startActivity(intent);
				
				
			}
		});
		
		aboutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this,
						AboutActivity.class);
						startActivity(intent);
				
			}
		});
		
		

		File directory = new File("_path");
		File mediaStorageDir = new File(directory, "MyCameraApp.jpeg");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");

			}

		}
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			startCameraActivity();
		}
	}

	protected void startCameraActivity() {
	    File file = new File( _path );
	    Uri outputFileUri = Uri.fromFile( file );

	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
	    intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

	    startActivityForResult( intent, 0 );
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("CameraApp", "resultCode: " + resultCode);
		switch (resultCode) {
		case 0:
			Log.i("CameraApp", "User cancelled");
			break;

		case -1:
			onPhotoTaken();
			break;
		}

	}

	protected void onPhotoTaken() {
		_taken = true;
		Intent intent = new Intent(MainMenuActivity.this, PostCamera.class);

		intent.putExtra("string", _path);
		/*ByteArrayOutputStream bs = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
		intent.putExtra("byteArray", bs.toByteArray());*/
		System.out.println("Activity begins");

		startActivity(intent);

	}

}
