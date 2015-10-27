package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Search extends ActionBarActivity {
    Context context = this;
    private String TAG  = "Search";
    public final static String SEARCH_TERMS = "com.aptmini.jreacs.connexus.SEARCH_TERMS";
    public final static String STREAM_NAME = "com.aptmini.jreacs.connexus.STREAM_NAME";
    public final static String OWNER_EMAIL = "com.aptmini.jreacs.connexus.OWNER_EMAIL";
    public final static String RESULT_NUM = "com.aptmini.jreacs.connexus.RESULT_NUM";
    int resultNum;
    String search_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(DisplayStreams.SEARCH_TERMS);
        search_terms = message;
        System.out.println("WHAT WAS SEARCHED: " + message);

        setContentView(R.layout.activity_search);

        TextView morePicsText= (TextView) findViewById(R.id.view_more_results);
        morePicsText.setVisibility(View.VISIBLE);



        String result_num = intent.getStringExtra(RESULT_NUM);
        System.out.println("Intent result_num: " + result_num);
        try {
            resultNum = Integer.parseInt(result_num);
            System.out.println("RESULTNUM WAS PARSED");
        } catch (NumberFormatException e) {
            System.out.println("NUMBER FORMAT EXCEPTION IN RESULT NUM");
            resultNum = 0;
        }

        final String request_url = "http://apt2015mini.appspot.com/msearch?terms="+message;
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

                    TextView numResults = (TextView) findViewById(R.id.search_results);
                    numResults.setText(displayUrls.length()+" results for "+ search_terms);

                    //Make the More Results button disappear if displayUrls.length < resultNum + Params.maxResults
                    if(displayUrls.length() <= resultNum + Params.maxResults)
                    {
                        System.out.println("NOT ENOUGH RESULTS FOR MORE!!!");
                        TextView myTextView= (TextView) findViewById(R.id.view_more_results);
                        myTextView.setVisibility(View.INVISIBLE);
                    }

                    for(int i=resultNum;i<displayNames.length() && i < Params.maxResults+resultNum;i++) {

                        coverURLs.add(displayUrls.getString(i));
                        streamNames.add(displayNames.getString(i));
                        ownerEmails.add(displayOwner.getString(i));

                        System.out.println(displayNames.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_search);
                    //gridview.setAdapter(new ImageAdapter(context,coverURLs));
                    gridview.setAdapter(new ImageTextAdapter(Search.this, context, coverURLs, streamNames));
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

    //This is a totally new search
    public void search(View view) {
        Intent intent = new Intent(this, Search.class);
        EditText editText = (EditText) findViewById(R.id.search_message);
        String message = editText.getText().toString();
        intent.putExtra(SEARCH_TERMS, message);

        startActivity(intent);

    }

    public void viewMoreResults(View view) {
        Intent intent = new Intent(this, Search.class);

        intent.putExtra(SEARCH_TERMS, search_terms);

        System.out.println("INTENT TO START NEARBY AGAIN, picNum: " + resultNum);
        String toPass = ""+(resultNum+Params.maxResults);
        System.out.println("\tWHAT I'M PASSSINGGG: " + toPass);
        intent.putExtra(RESULT_NUM, toPass);

        startActivity(intent);

    }

    public void viewAllStreams(View view) {
        Intent intent = new Intent(this, DisplayStreams.class);
        startActivity(intent);
    }

}


