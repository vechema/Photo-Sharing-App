package com.aptmini.jreacs.connexus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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

public class ViewAStream extends ActionBarActivity {

    public final static String PIC_NUM = "com.aptmini.jreacs.connexus.PIC_NUM";
    public final static String OWNER_EMAIL = "com.aptmini.jreacs.connexus.OWNER_EMAIL";
    public final static String STREAM_NAME = "com.aptmini.jreacs.connexus.STREAM_NAME";
    Context context = this;
    private String TAG  = "Display A Stream";
    String stream_name;
    boolean isOwner;
    String owners_email;
    int picNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_astream);

        TextView morePicsText= (TextView) findViewById(R.id.view_more_pics_astream);
        morePicsText.setVisibility(View.VISIBLE);

        TextView uploadPicText= (TextView) findViewById(R.id.upload_photo);
        uploadPicText.setVisibility(View.VISIBLE);

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

        //Owner stuff
        String owner_email = intent.getStringExtra(DisplayStreams.OWNER_EMAIL);
        owners_email = owner_email;

        String home_email = null;
        String format_home_email = null;
        if (Homepage.email != null) {
            home_email = formatEmail(Homepage.email);
            format_home_email = home_email.substring(0,home_email.indexOf('@'));
        }


        if(owner_email.equals(home_email))
        {
            System.out.println("I AM THE OWNER!!!!");
            isOwner = true;
        } else {
            isOwner = false;
            uploadPicText.setVisibility(View.GONE);
        }

        stream_name = intent.getStringExtra(DisplayStreams.STREAM_NAME);
        System.out.println("ViewAStream stream name: " + stream_name);

        TextView myTextView= (TextView) findViewById(R.id.stream_name_view);
        myTextView.setText("View a Stream: " + stream_name);

        System.out.println("CURRENT EMAIL: " + Homepage.email);
        System.out.println("Am I the owner?: " + isOwner);

        final String request_url = "http://apt2015mini.appspot.com/mview?stream=" + stream_name + "&user=" + format_home_email;
        System.out.println(request_url);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> picUrls = new ArrayList<String>();
                final ArrayList<String> picCaps = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    JSONArray displayUrls = jObject.getJSONArray("picUrls");
                    JSONArray displayCaps = jObject.getJSONArray("picCaps");

                    //Make the More Pics button disappear if displayUrls.length < picNum + Params.maxPictures
                    if(displayUrls.length() <= picNum + Params.maxPictures)
                    {
                        System.out.println("NOT ENOUGH PICTURES FOR MORE!!!");
                        TextView myTextView= (TextView) findViewById(R.id.view_more_pics_astream);
                        myTextView.setVisibility(View.INVISIBLE);
                    }

                    for(int i=picNum;i<displayUrls.length() && i < Params.maxPictures+picNum;i++) {

                        picUrls.add(displayUrls.getString(i));
                        picCaps.add(displayCaps.getString(i));

                        System.out.println(displayUrls.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_astream);
                    gridview.setAdapter(new ImageAdapter(context,picUrls));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Toast.makeText(context, picCaps.get(position), Toast.LENGTH_SHORT).show();

                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(picUrls.get(position)).into(image);

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

    public static String formatEmail(String email) {
        email = email.toLowerCase();
        //index = email.index('@')
        int index = email.indexOf('@');
        //email_front = email[:index]
        return email.substring(0,index).replace(".","") + email.substring(index);
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

    public void viewAllStreams(View view) {
        Intent intent = new Intent(this, DisplayStreams.class);
        startActivity(intent);
    }

    public void viewMorePics(View view) {
        Intent intent = new Intent(this, ViewAStream.class);
        System.out.println("INTENT TO START NEARBY AGAIN, picNum: " + picNum);
        String toPass = ""+(picNum+Params.maxPictures);
        System.out.println("\tWHAT I'M PASSSINGGG: " + toPass);
        intent.putExtra(PIC_NUM, toPass);
        intent.putExtra(OWNER_EMAIL, owners_email);
        intent.putExtra(STREAM_NAME, stream_name);
        startActivity(intent);
    }

    public void uploadPic(View view) {
        Intent intent = new Intent(this, ImageUpload.class);
        intent.putExtra(STREAM_NAME, stream_name);
        startActivity(intent);
//        startActivityForResult(intent, 1);
    }

}
