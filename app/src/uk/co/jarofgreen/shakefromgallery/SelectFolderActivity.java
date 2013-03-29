package uk.co.jarofgreen.shakefromgallery;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author James
 * @copyright 2013 JMB Technology Limited
 * @license Open Source; 3-clause BSD 
 */
public class SelectFolderActivity extends Activity {
	
	GridView gridview;
	ImageAdapter gridViewAdaptor;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectgallery);
		
	    gridview = (GridView) findViewById(R.id.gridview);
	    gridViewAdaptor = new ImageAdapter(this);
	    gridview.setAdapter(gridViewAdaptor);
		
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	Gallery data = (Gallery)gridViewAdaptor.getItem(position);
				Intent i = new Intent(SelectFolderActivity.this, MainActivity.class);
				i.putExtra("bucketID",data.id);
				i.putExtra("bucketName", data.name);
				SelectFolderActivity.this.startActivity(i);
	        }
	    });
		
	}
	
	
	protected class Gallery {
		public String id;
		public String name;
		public long imageID;
		public Gallery(String id, String name, String imageID) {
			super();
			this.id = id;
			this.name = name;
			this.imageID = Long.parseLong(imageID);
		}
	}
	
	protected class ImageAdapter extends BaseAdapter {
		protected Context mContext;
		protected LayoutInflater layoutInflater; 

		protected List<Gallery> data = new ArrayList<Gallery>();
		protected List<String> bucketIdList = new ArrayList<String>();

		public ImageAdapter(Context c) {
			mContext = c;
			layoutInflater = (LayoutInflater)c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	        
	        String[] projection = new String[]{
	        		MediaStore.Images.Media._ID,
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
		        String imageId;
		        int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		        int bucketIdColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
		        int imageIDColumn = cur.getColumnIndex(MediaStore.Images.Media._ID);
		        do {
		            bucket = cur.getString(bucketColumn);
		            bucketId = cur.getString(bucketIdColumn);
		            imageId = cur.getString(imageIDColumn);
		            if (!bucketIdList.contains(bucketId)) {
		            	//Log.i("NEWBUCKET"," id="+bucketId+" name="+bucket);
		            	bucketIdList.add(bucketId);
		            	data.add(new Gallery(bucketId, bucket, imageId));
		            }
		        } while (cur.moveToNext());
		    }		
	        
	    }

	    public int getCount() {
	        return data.size();
	    }

	    public Object getItem(int position) {
	        return data.get(position);
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View view;
	    	if (convertView != null) {
	    		view = convertView;
	    	} else {
	    		view = layoutInflater.inflate( R.layout.selectgallery_item, parent, false );
	    	}
	    	
	    	Gallery ourData = data.get(position);
	    	
	        TextView textView = (TextView)view.findViewById(R.id.title);
	        textView.setText(ourData.name);
	        
	        ImageView imageView = (ImageView)view.findViewById(R.id.image);
	        
	        Bitmap bit = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),ourData.imageID, 
	        		MediaStore.Images.Thumbnails.MINI_KIND, null);
	        imageView.setImageBitmap(bit);
	        
	        return view;
	    }

	}
	

}