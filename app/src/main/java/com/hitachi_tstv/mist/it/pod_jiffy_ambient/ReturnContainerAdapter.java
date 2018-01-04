package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by tunyaporns on 2/14/2017.
 */

public class ReturnContainerAdapter extends BaseAdapter {
    private Context context;
    private String[] containerTypeStrings, imageTextStrings, conIdStrings;
    private ImageView pictureImageView;
    private TextView containerTextView;

    public ReturnContainerAdapter(Context context, String[] containerTypeStrings, String[] imageTextStrings, String[] conIdStrings) {
        this.context = context;
        this.containerTypeStrings = containerTypeStrings;
        this.imageTextStrings = imageTextStrings;
        this.conIdStrings = conIdStrings;
    }

    @Override
    public int getCount() {
        return containerTypeStrings.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d("Tag1", "Layout ==> " + layoutInflater);
        View view1 = layoutInflater.inflate(R.layout.return_cont_list_view, viewGroup, false);

        //bind widget
        pictureImageView = (ImageView) view1.findViewById(R.id.imageView7);
        containerTextView = (TextView) view1.findViewById(R.id.textView23);

        //load & show picture
        SynLoadImage synLoadImage = new SynLoadImage(pictureImageView, context, imageTextStrings[i]);
        synLoadImage.execute();

        //set text
        containerTextView.setText(containerTypeStrings[i]);


        return view1;
    }

    private class SynLoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView view;
        private Context context;
        private String urlString;

        public SynLoadImage(ImageView view, Context context, String urlString) {
            this.view = view;
            this.context = context;
            this.urlString = urlString;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlString).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            view.setImageBitmap(bitmap);
        }
    }
}
