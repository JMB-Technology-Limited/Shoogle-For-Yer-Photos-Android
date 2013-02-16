package uk.co.jarofgreen.shakefromgallery;

import uk.co.jarofgreen.shakefromgallery.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;


/**
 * 
 * @author James
 * @copyright 2013 JMB Technology Limited
 * @license Open Source; 3-clause BSD 
 */
public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		TextView tv = (TextView) findViewById(R.id.about);
		Linkify.addLinks(tv, Linkify.WEB_URLS);
	}

}