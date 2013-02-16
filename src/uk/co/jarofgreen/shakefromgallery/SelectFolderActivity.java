package uk.co.jarofgreen.shakefromgallery;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author James
 * @copyright 2013 JMB Technology Limited
 * @license Open Source; 3-clause BSD 
 */
public class SelectFolderActivity extends Activity {

	ListView listView;
	List<String> bucketIdList = new ArrayList<String>();
	List<String> bucketNameList = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectgallery);

		listView = (ListView)findViewById(R.id.list);


		String[] projection = new String[]{
				MediaStore.Images.Media.BUCKET_ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME, 
		};
		Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Cursor cur = managedQuery(images,
				projection, // Which columns to return
						"",         // Which rows to return (all rows)
						null,       // Selection arguments (none)
						""          // Ordering
				);

		Log.i("ListingImages"," query count="+cur.getCount());

		if (cur.moveToFirst()) {
			String bucket;
			String bucketId;
			int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
			int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);

			do {
				bucket = cur.getString(bucketColumn);
				bucketId = cur.getString(bucketIdColumn);

				if (!bucketIdList.contains(bucketId)) {
					//Log.i("NEWBUCKET"," id="+bucketId+" name="+bucket);
					bucketIdList.add(bucketId);
					bucketNameList.add(bucket);
				}
			} while (cur.moveToNext());

			ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, bucketNameList);
			listView.setAdapter(directoryList); 

		}		

		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("BUCKET",bucketNameList.get(position));
				Intent i = new Intent(SelectFolderActivity.this, MainActivity.class);
				i.putExtra("bucketID",bucketIdList.get(position));
				i.putExtra("bucketName",bucketNameList.get(position));
				SelectFolderActivity.this.startActivity(i);
			}
		});

	}

}
