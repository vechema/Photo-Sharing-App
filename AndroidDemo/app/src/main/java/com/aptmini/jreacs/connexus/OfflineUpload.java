package com.aptmini.jreacs.connexus;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class OfflineUpload extends ActionBarActivity {
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_IMAGE = 2;
    Context context = this;
    static String imageFilePath;
    public static final String EXTRA_OFFLINE = "offline_photo";
    public static List<OfflinePhoto> offlinephotos = new ArrayList<OfflinePhoto>();


    String streamName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("this is the file path: " + imageFilePath);
        setContentView(R.layout.activity_offline_upload);

        if (imageFilePath != null){
            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            uploadButton.setClickable(true);

            uploadButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Get photo caption

                            EditText text = (EditText) findViewById(R.id.upload_message);
                            String photoCaption = text.getText().toString();
                            EditText sn = (EditText) findViewById(R.id.stream_name);
                            String streamName = sn.getText().toString();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
                            byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                            String encodedImageStr = encodedImage.toString();

                            double lat = Params.latitude;
                            double lng = Params.longitude;
                            System.out.println("**Sent a taken photo: ");
                            System.out.println("\tlat: " + lat);
                            System.out.println("\tlng: " + lng);
                            System.out.println("\tname: " + streamName);

                            imageFilePath = null;
                            getUploadURL(b, photoCaption, lat, lng, streamName);
                        }
                    }
            );
        }

        TextView myTextView= (TextView) findViewById(R.id.stream_name_upload);

        // Choose image from library
        Button chooseFromLibraryButton = (Button) findViewById(R.id.choose_from_library);
        chooseFromLibraryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // To do this, go to AndroidManifest.xml to add permission
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, PICK_IMAGE);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_upload, menu);
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

    private static int randInt(int min, int max) {
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private static float[] randLatLng()
    {
        float[] result = new float[2];
        int lat = randInt(-90, 90);
        int lng = randInt(-180, 180);
        result[0] = lat;
        result[1] = lng;
        return result;
    }

    private float[] getLatLong(String file)
    {
        float[] result = new float[2];
        try {
            ExifInterface exifI = new ExifInterface(file);
            System.out.println("***EXIFI: "+exifI + "***");
            String length = exifI.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            String width = exifI.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            System.out.println("Width: " + width + " + Length: " + length);

            if(exifI.getLatLong(result))
            {
                System.out.println("LAT: "+result[0]);
                System.out.println("LNG: "+result[1]);
            } else {
                result = randLatLng();
                System.out.println("RANDOM FROM ELSE");
            }


        } catch (IOException e) {
            //The file couldn't be found
            System.out.println("Something went wrong with finding the picture");
        } catch (NullPointerException e) {
            result = randLatLng();
            System.out.println("~~Something was null, went random!!");
        }

        return result;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            System.out.println(selectedImage);

            // User had pick an image.

            String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            // Link to the image

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imageFilePath = cursor.getString(columnIndex);
            cursor.close();

            // Bitmap imaged created and show thumbnail

            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            System.out.println(imageFilePath);
            System.out.println("DEBUGGING");
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded

            Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            uploadButton.setClickable(true);

            uploadButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Get photo caption

                            EditText text = (EditText) findViewById(R.id.upload_message);
                            String photoCaption = text.getText().toString();
                            EditText sn = (EditText) findViewById(R.id.stream_name);
                            String streamName = sn.getText().toString();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
                            byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                            String encodedImageStr = encodedImage.toString();

                            float[] latlng = getLatLong(imageFilePath);
                            float lat = latlng[0];
                            float lng = latlng[1];
                            imageFilePath = null;

                            getUploadURL(b, photoCaption, lat, lng, streamName);

                        }
                    }
            );
        }
        if (requestCode == TAKE_IMAGE && data != null) {
            System.out.println("TAKE IMAGE");
            imageFilePath = data.getStringExtra(TakePhoto.EXTRA_FILE);
            System.out.println(imageFilePath);
            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            System.out.println(imageFilePath);
            System.out.println("DEBUGGING");
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded

            Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            uploadButton.setClickable(true);

            uploadButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Get photo caption

                            EditText text = (EditText) findViewById(R.id.upload_message);
                            String photoCaption = text.getText().toString();
                            EditText sn = (EditText) findViewById(R.id.stream_name);
                            String streamName = sn.getText().toString();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
                            byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                            String encodedImageStr = encodedImage.toString();

                            double lat = Params.latitude;
                            double lng = Params.longitude;
                            System.out.println("**Sent a taken photo: ");
                            System.out.println("\tlat: " + lat);
                            System.out.println("\tlng: " + lng);
                            System.out.println("\tname: " + streamName);

                            imageFilePath = null;
                            getUploadURL(b, photoCaption, lat, lng, streamName);
                        }
                    }
            );
        }
    }

    private void getUploadURL(byte[] encodedImage, String photoCaption, double lat, double lng, String stream_name) {
        OfflinePhoto myPhoto = new OfflinePhoto(encodedImage, photoCaption, lat, lng, stream_name);
        offlinephotos.add(myPhoto);
        finish();
    }


    public void takePicture(View view){
        Intent intent= new Intent(this, TakePhoto.class);
        startActivityForResult(intent, TAKE_IMAGE);
    }
}
