package uk.co.jarofgreen.shakefromgallery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * 
 * @author James
 * @copyright 2013 JMB Technology Limited
 * @license Open Source; 3-clause BSD 
 */	 
public class SplashActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		addLastViewedButtonIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		addLastViewedButtonIfNeeded();
	}

	protected void addLastViewedButtonIfNeeded() {

		// We have to redo this every time we RESUME the activity.
		// While we were in the background the id & name of the last viewed gallery may have changed 
		//   and so we have to update the button.

		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		String lastViewedBucketID = settings.getString("lastViewedBucketID", null);
		String lastViewedBucketName = settings.getString("lastViewedBucketName", null);

		if (lastViewedBucketID != null) {
			Button button = (Button)findViewById(R.id.go_last_viewed_button);
			button.setVisibility(View.VISIBLE);
			button.setText(getString(R.string.splash_button_go_last_viewed_button_prefix)+
					" "+lastViewedBucketName+" "+
					getString(R.string.splash_button_go_last_viewed_button_postfix));
		}
	}


	public void goLastViewedButtonClick(View v) {
		Intent i = new Intent(this, MainActivity.class);
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		i.putExtra("bucketID",settings.getString("lastViewedBucketID", null));
		i.putExtra("bucketName",settings.getString("lastViewedBucketName", null));
		startActivity(i);
	}

	public void goButtonClick(View v) {
		startActivity(new Intent(this, SelectFolderActivity.class));
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.splash, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;	            
		default:
			return super.onOptionsItemSelected(item);
		}
	}   
}
