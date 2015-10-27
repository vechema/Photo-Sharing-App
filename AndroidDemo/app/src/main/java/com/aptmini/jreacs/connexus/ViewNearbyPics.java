package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

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
    public final static String PIC_NUM = "com.aptmini.jreacs.connexus.PIC_NUM";
    Context context = this;
    private String TAG  = "Display Nearby Pics";
    int picNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nearby_pics);

        TextView myTextView= (TextView) findViewById(R.id.view_more_pics_nearby);
        myTextView.setVisibility(View.VISIBLE);

        //Get what page we're on
        Intent intent = getIntent();
        String pic_num = intent.getStringExtra(ViewNearbyPics.PIC_NUM);
        System.out.println("Intent pic_num: " + pic_num);
        try {
            picNum = Integer.parseInt(pic_num);
            System.out.println("PICNUM WAS PARSED");
        } catch (NumberFormatException e) {
            System.out.println("NUMBER FORMAT EXCEPTION");
            picNum = 0;
        }

        System.out.println("**Picture number we're on: " + picNum);

        final String request_url = "http://apt2015mini.appspot.com/mviewNearby?latitude=" + Params.latitude + "&longitude=" +Params.longitude;
        System.out.println("Request url: " + request_url);

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

                    //Make the More Pics button disappear if displayUrls.length < picNum + Params.maxPictures
                    if(displayUrls.length() <= picNum + Params.maxPictures)
                    {
                        System.out.println("NOT ENOUGH PICTURES FOR MORE!!!");
                        TextView myTextView= (TextView) findViewById(R.id.view_more_pics_nearby);
                        myTextView.setVisibility(View.INVISIBLE);
                    }

                    for (int i = picNum; i < displayNames.length() && i < Params.maxPictures+picNum; i++) {

                        picURLs.add(displayUrls.getString(i));
                        streamNames.add(displayNames.getString(i));
                        ownerEmails.add(displayOwner.getString(i));
                        distances.add(displayDists.getString(i).substring(0,displayDists.getString(i).indexOf('.')) + " km");

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

    public void viewMorePics(View view) {
        Intent intent = new Intent(this, ViewNearbyPics.class);
        System.out.println("INTENT TO START NEARBY AGAIN, picNum: " + picNum);
        String toPass = ""+(picNum+Params.maxPictures);
        System.out.println("\tWHAT I'M PASSSINGGG: " + toPass);
        intent.putExtra(PIC_NUM, toPass);
        startActivity(intent);
    }

}
