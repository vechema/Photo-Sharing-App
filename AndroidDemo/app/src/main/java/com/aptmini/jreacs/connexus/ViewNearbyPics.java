package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewNearbyPics extends ActionBarActivity {

    public final static String STREAM_NAME = "com.aptmini.jreacs.connexus.STREAM_NAME";
    public final static String OWNER_EMAIL = "com.aptmini.jreacs.connexus.OWNER_EMAIL";
    Context context = this;
    private String TAG  = "Display Nearby Pics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nearby_pics);

        final String request_url = "http://apt2015mini.appspot.com/mviewNearby?latitude=" + Params.latitude + "&longitude=" +Params.longitude;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> picURLs = new ArrayList<String>();
                final ArrayList<String> streamNames = new ArrayList<String>();
                final ArrayList<String> ownerEmails = new ArrayList<String>();
                final ArrayList<String> distances = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    JSONArray displayUrls = jObject.getJSONArray("picUrls");
                    JSONArray displayNames = jObject.getJSONArray("streamNames");
                    JSONArray displayOwner = jObject.getJSONArray("ownerEmails");
                    JSONArray displayDists = jObject.getJSONArray("distances");

                    for (int i = 0; i < displayNames.length() && i < Params.maxPictures; i++) {

                        picURLs.add(displayUrls.getString(i));
                        streamNames.add(displayNames.getString(i));
                        ownerEmails.add(displayOwner.getString(i));
                        distances.add(displayDists.getString(i));

                        System.out.println(displayNames.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_nearby);
                    //gridview.setAdapter(new ImageAdapter(context, picURLs));
                    gridview.setAdapter(new ImageTextAdapter(ViewNearbyPics.this, context, picURLs, distances));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            //When clicked - open up a new activity - view a single stream
                            Intent intent = new Intent(context, ViewAStream.class);
                            String stream_name = streamNames.get(position);
                            String owner_email = ownerEmails.get(position);
                            System.out.println("NearbyStreams, stream name: " + stream_name);
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
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    public void viewAllStreams(View view) {
        Intent intent = new Intent(this, DisplayStreams.class);
        startActivity(intent);
    }

}
