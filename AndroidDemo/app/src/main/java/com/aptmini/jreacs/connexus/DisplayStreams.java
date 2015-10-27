package com.aptmini.jreacs.connexus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayStreams extends ActionBarActivity {
    Context context = this;
    private String TAG  = "Display All Streams";
    public final static String STREAM_NAME = "com.aptmini.jreacs.connexus.STREAM_NAME";
    public final static String OWNER_EMAIL = "com.aptmini.jreacs.connexus.OWNER_EMAIL";
    public final static String SEARCH_TERMS = "com.aptmini.jreacs.connexus.SEARCH_TERMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_streams);

        TextView morePicsText= (TextView) findViewById(R.id.subscribed);

        if (Homepage.email != null) {
            morePicsText.setVisibility(View.VISIBLE);
        } else {
            morePicsText.setVisibility(View.GONE);
        }

        final String request_url = "http://apt2015mini.appspot.com/mviewAllStreams";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> coverURLs = new ArrayList<String>();
                final ArrayList<String> streamNames = new ArrayList<String>();
                final ArrayList<String> ownerEmails = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    JSONArray displayUrls = jObject.getJSONArray("coverURLs");
                    JSONArray displayNames = jObject.getJSONArray("streamNames");
                    JSONArray displayOwner = jObject.getJSONArray("ownerEmails");

                    for(int i=0;i<displayNames.length() && i < Params.maxStreams;i++) {

                        coverURLs.add(displayUrls.getString(i));
                        streamNames.add(displayNames.getString(i));
                        ownerEmails.add(displayOwner.getString(i));

                        System.out.println(displayNames.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_streams);
                    //gridview.setAdapter(new ImageAdapter(context,coverURLs));
                    gridview.setAdapter(new ImageTextAdapter(DisplayStreams.this, context, coverURLs, streamNames));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            //When clicked - open up a new activity - view a single stream
                            Intent intent = new Intent(context, ViewAStream.class);
                            String stream_name = streamNames.get(position);
                            String owner_email = ownerEmails.get(position);
                            System.out.println("DisplayStreams, stream name: " + stream_name);
                            intent.putExtra(STREAM_NAME, stream_name);
                            intent.putExtra(OWNER_EMAIL, owner_email);
                            startActivity(intent);

                            /*Toast.makeText(context, streamNames.get(position), Toast.LENGTH_SHORT).show();

                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(coverURLs.get(position)).into(image);

                            imageDialog.show();*/
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
    public void onResume(){
        super.onResume();

        //Setting the location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null){
            Params.longitude = 0;
            Params.latitude = 0;

        } else {
            Params.longitude = location.getLongitude();
            Params.latitude = location.getLatitude();
        }

        System.out.println("********************");
        System.out.println("Lng: " + Params.longitude);
        System.out.println("Lat: " + Params.latitude);
        System.out.println("********************");
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

    public void viewNearbyPics(View view) {
        Intent intent = new Intent(this, ViewNearbyPics.class);
        startActivity(intent);
    }

    public void viewSubscribed(View view) {
        Intent intent = new Intent(this, ViewSubscribed.class);
        startActivity(intent);
    }

    public void search(View view) {
        Intent intent = new Intent(this, Search.class);
        EditText editText = (EditText) findViewById(R.id.search_message);
        String message = editText.getText().toString();
        intent.putExtra(SEARCH_TERMS, message);
        startActivity(intent);

    }

}
