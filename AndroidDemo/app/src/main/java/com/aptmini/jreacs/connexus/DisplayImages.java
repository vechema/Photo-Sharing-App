package com.aptmini.jreacs.connexus;
import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayImages extends ActionBarActivity {
    Context context = this;
    private String TAG  = "Display Images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        final String request_url = "http://apt2015mini.appspot.com/mviewAllPhotos";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                final ArrayList<String> imageCaps = new ArrayList<String>();
                final ArrayList<String> imageLats = new ArrayList<String>();
                final ArrayList<String> imageLngs = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray displayImages = jObject.getJSONArray("displayImages");
                    JSONArray displayCaption = jObject.getJSONArray("imageCaptionList");
                    JSONArray displayLats = jObject.getJSONArray("latitudeList");
                    JSONArray displayLngs = jObject.getJSONArray("longitudeList");

                    for(int i=0;i<displayImages.length();i++) {

                        imageURLs.add(displayImages.getString(i));
                        imageCaps.add(displayCaption.getString(i));
                        imageLats.add(displayLats.getString(i));
                        imageLngs.add(displayLngs.getString(i));

                        System.out.println(displayImages.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(context,imageURLs));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            //Toast.makeText(context, imageLats.get(position), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, imageCaps.get(position), Toast.LENGTH_SHORT).show();

                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(imageURLs.get(position)).into(image);

                            imageDialog.show();
                        }
                    });
                }
                catch(JSONException j){
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
