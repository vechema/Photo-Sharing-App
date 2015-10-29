package com.aptmini.jreacs.connexus;

/**
 * Created by Andrew on 10/28/2015.
 */

public class OfflinePhoto{
    byte[] encodedImage;
    String photoCaption;
    double lat;
    double lng;
    String stream_name;

    OfflinePhoto(byte[] encodedImage, String photoCaption, double lat, double lng, String stream_name){
        this.encodedImage = encodedImage;
        this.photoCaption = photoCaption;
        this.lat = lat;
        this.lng = lng;
        this.stream_name = stream_name;
    }


}

