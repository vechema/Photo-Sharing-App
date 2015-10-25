package com.aptmini.jreacs.connexus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jo on 10/24/2015.
 */
public class ImageTextAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> imageURLs;
    private ArrayList<String> texts;

    public ImageTextAdapter(Activity a, Context c, ArrayList<String> imageURLs, ArrayList<String> texts) {
        mActivity = a;
        mContext = c;
        this.imageURLs = imageURLs;
        this.texts = texts;
    }

    public int getCount() {
        return imageURLs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        public ImageView imgViewPic;
        public TextView txtViewTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder view;
        LayoutInflater inflator = mActivity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_image_text, null);

            view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);
            view.imgViewPic = (ImageView) convertView.findViewById(R.id.imageView1);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.txtViewTitle.setText(texts.get(position));
        //view.imgViewPic.setImageResource(imageURLs.get(position));
        Picasso.with(mContext).load(imageURLs.get(position)).into(view.imgViewPic);

        return convertView;
    }
}
