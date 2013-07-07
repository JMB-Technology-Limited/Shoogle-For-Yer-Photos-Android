package uk.co.jarofgreen.shakefromgallery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.jarofgreen.lib.ShakeDetectActivity;
import uk.co.jarofgreen.lib.ShakeDetectActivityListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 
 * @author James
 * @copyright 2013 JMB Technology Limited
 * @license Open Source; 3-clause BSD 
 */
public class MainActivity extends Activity {

	ImageView mainImageView;
	Random randomGenerator;
	TextView mainTextView;
	Bitmap bit;
	LoadImageTask loadImageTask;
	
	
	ShakeDetectActivity shakeDetectActivity;

	List<String> images = new ArrayList<String>();

	/**
	 * We store the last few images show to the user, so they don't see the same image 
	 * repeated to close together. 
	 */
	protected List<Integer> lastTriggersSelected = new ArrayList<Integer>();


	/**
	 * This is how many values we store in lastTriggersSelected; it is calculated later 
	 * based on the size of the gallery.
	 */
	int remmeberLastImages = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		shakeDetectActivity = new ShakeDetectActivity(this); 
		shakeDetectActivity.addListener(new ShakeDetectActivityListener() {
			@Override
			public void shakeDetected() {
				MainActivity.this.triggerShakeDetected();
				
			}
		});
		
		mainImageView = (ImageView)findViewById(R.id.imageView);
		mainTextView = (TextView)findViewById(R.id.mainText);

		randomGenerator = new Random();
		randomGenerator.setSeed(System.currentTimeMillis());

		Bundle extras = getIntent().getExtras();

		if (extras == null) {
			return; // TODO die
		}
		String bucketID = extras.getString("bucketID");
		String bucketName = extras.getString("bucketName");

		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("lastViewedBucketID", bucketID);
		editor.putString("lastViewedBucketName", bucketName);
		editor.commit();

		String[] projection = new String[]{
				MediaStore.Images.Media.DATA 
		};
		Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String[] args = { bucketID };
		Cursor cur = managedQuery(imagesUri,
				projection, // Which columns to return
				MediaStore.Images.Media.BUCKET_ID+"=?",         // Which rows to return (all rows)
				args,       // Selection arguments (none)
				""          // Ordering
				);

		if (cur.getCount() == 0) {
			return; // TODO die
		}

		if (cur.moveToFirst()) {
			String data;
			int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
			do {
				data = cur.getString(dataColumn);
				images.add(data);
				//Log.i("IMAGE",data);
			} while (cur.moveToNext());
		}		

		remmeberLastImages = Math.min(10, images.size()/2);
	}

	@Override
	protected void onResume() {
		super.onResume();
		shakeDetectActivity.onResume();
		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onPause() {
		getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		shakeDetectActivity.onPause();
		super.onPause();
	}

	public void triggerShakeDetected() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(300);
		nextPicture();
	}

	public void nextPicture() {
		mainTextView.setVisibility(View.GONE);

		// are we already loading?
		if (loadImageTask != null && loadImageTask.isRunning()) {
			return;
		}
		
		// get next index
		int next;
		do {
			next = randomGenerator.nextInt(images.size());
			//Log.i("POSSIBLENEXT",Integer.toString(next));
		} while (lastTriggersSelected.contains(Integer.valueOf(next)));
		lastTriggersSelected.add(Integer.valueOf(next));
		if (lastTriggersSelected.size() > remmeberLastImages) lastTriggersSelected.remove(0);

		// start loading
		mainImageView.setImageResource(R.drawable.loading);
		loadImageTask = new LoadImageTask();
		loadImageTask.execute(images.get(next));
	

	}

	protected class LoadImageTask extends AsyncTask<String, Integer, Boolean> {

		protected boolean running = false;
		
		@Override
		protected Boolean doInBackground(String... params) {
			running = true;
			if (bit != null) {
				bit.recycle();
			}
			bit = BitmapFactory.decodeFile(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mainImageView.setImageBitmap(bit);
			running = false;
		}

		public boolean isRunning() {
			return running;
		}
		
		
		
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.next:
			nextPicture();
			return true;
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;	            
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}