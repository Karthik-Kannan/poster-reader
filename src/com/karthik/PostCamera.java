package com.karthik;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

public class PostCamera extends Activity {

	protected String path, venue, contact;
	protected Button mainMenuButton, saveEventButton;
	protected ImageView _image;
	protected Date startDate, endDate;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_camera);
		System.out.println("PostCamera");

		mainMenuButton = (Button) findViewById(R.id.mainMenubutton);
		saveEventButton = (Button) findViewById(R.id.eventButton);
		_image = (ImageView) findViewById(R.id.imageView);

		
		Bundle extras = getIntent().getExtras();
		path = extras.getString("string");
		if (path == null) {
			Log.e("post camera", "STRING EMPTY");
		} else
			System.out.println(path);

		EditText textget = (EditText) findViewById(R.id.editText);

		//String recogtext = "Sample Poster\nSample Event Description, Do Attend!!\n17 September 2011\nVenue- Anna Auditorium\n5:00pm to 7:00pm\nFor Further details contact: 9940094857";
		 String recogtext = readImage();
		textget.setText(recogtext);
		parseString(recogtext);
		
		mainMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PostCamera.this,
						MainMenuActivity.class);
				startActivity(intent);

			}
		});

		saveEventButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent l_intent = new Intent(Intent.ACTION_EDIT);
				l_intent.setType("vnd.android.cursor.item/event");
				// l_intent.putExtra("calendar_id", m_selectedCalendarId); //this
				// doesn't work
				l_intent.putExtra("title", "Sample Poster");
				l_intent.putExtra("description",
						"Contact Number Given: " +contact);
				l_intent.putExtra("eventLocation", venue);
				l_intent.putExtra("beginTime", startDate.getTime());
				l_intent.putExtra("endTime", endDate.getTime());
				l_intent.putExtra("allDay", 0);
				// status: 0~ tentative; 1~ confirmed; 2~ canceled
				l_intent.putExtra("eventStatus", 1);
				// 0~ default; 1~ confidential; 2~ private; 3~ public
				l_intent.putExtra("visibility", 1);
				// 0~ opaque, no timing conflict is allowed; 1~ transparency, allow
				// overlap of scheduling
				l_intent.putExtra("transparency", 0);
				// 0~ false; 1~ true
				l_intent.putExtra("hasAlarm", 1);// overlap of scheduling
				l_intent.putExtra("transparency", 0);
				// 0~ false; 1~ true
				l_intent.putExtra("hasAlarm", 1);
				try {
					startActivity(l_intent);
				} catch (Exception e) {
					Toast.makeText(PostCamera.this.getApplicationContext(),
							"Sorry, no compatible calendar is found!",
							Toast.LENGTH_LONG).show();
				}

			}

		});

	}

	private void parseString(String recogtext) {

		SimpleDateFormat dateformat = new SimpleDateFormat("dd MMMM yyyy hh:mm");

		String stimestr = null, etimestr = null, datestr = null;

		Pattern date = Pattern
				.compile("(\\d)(\\d)(\\s)(\\w)*(\\s)(\\d)(\\d)(\\d)(\\d)");
		Matcher datematcher = date.matcher(recogtext);
		if (datematcher.find()) {
			datestr = datematcher.group();
		}

		Pattern time = Pattern.compile("(\\d)(:)(\\d)(\\d)");
		Matcher timematcher = time.matcher(recogtext);

		if (timematcher.find()) {
			stimestr = timematcher.group();

		}
		if (timematcher.find()) {
			etimestr = timematcher.group();

		}
		String startdatestr, enddatestr;
		startdatestr= datestr + " " + stimestr;
		enddatestr= datestr+ " " + etimestr;
		System.out.println(startdatestr);
		System.out.println(enddatestr);

		try {
			startDate = dateformat.parse(datestr + " " + stimestr);
			
			endDate = dateformat.parse(datestr + " " + etimestr);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Pattern venuepat= Pattern.compile("(\\w)*(-)(\\w)*(\\s)(\\w)*(\\s)(\\w)*");
        Matcher venuematcher= venuepat.matcher(recogtext);
        String venuestr; 
        
        if(venuematcher.find())
        {
        	venuestr = venuematcher.group();
        	venue= venuestr.replaceAll("Venue- ", "");
        }
        
        
        Pattern contactpat= Pattern.compile("(\\d){10}");
        Matcher contactmatcher= contactpat.matcher(recogtext);
        if(contactmatcher.find())
        {
        	contact = contactmatcher.group();
        	
        }
        
        
        

	}

	protected String readImage() {

		System.out.println("read img");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = -4;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int exifOrientation = exif
				.getAttributeInt(ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

		int rotate = 0;

		switch (exifOrientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			rotate = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			rotate = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			rotate = 270;
			break;
		}

		if (rotate != 0) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			// Setting pre rotate
			Matrix mtx = new Matrix();
			mtx.preRotate(rotate);

			// Rotating Bitmap & convert to ARGB_8888, required by tess
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);

		}

		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		TessBaseAPI baseApi = new TessBaseAPI();
		// DATA_PATH = Path to the storage
		// lang for which the language data exists, usually "eng"
		baseApi.init(Environment.getExternalStorageDirectory() + "/tesseract",
				"eng");
		Pix pix = ReadFile.readBitmap(bitmap);
		pix = Binarize.otsuAdaptiveThreshold(pix, 300, 300, 30, 30, (float) 0.1);
		baseApi.setImage(pix);
		String recognizedText = baseApi.getUTF8Text();
		bitmap = WriteFile.writeBitmap(pix);
		_image.setImageBitmap(bitmap);
		baseApi.end();
		// Toast.makeText(CameratestActivity.this, recognizedText,
		// Toast.LENGTH_LONG).show();

		System.out.println(recognizedText);
		return recognizedText;
	}

}
