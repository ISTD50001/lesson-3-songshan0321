package com.example.norman_lee.comicapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editTextComicNo;
    Button buttonGetComic;
    TextView textViewTitle;
    ImageView imageViewComic;

    String comicNo;
    public static final String TAG = "Logcat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 6.1 Ensure that Android Manifest has permissions for internet and has orientation fixed
        // 6.2 Get references to widgets

        editTextComicNo = findViewById(R.id.editTextComicNo);
        buttonGetComic = findViewById(R.id.buttonGetComic);
        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewComic = findViewById(R.id.imageViewComic);

        // 6.3 - 6.5 and 6.14 **********************************
        // 6.3 Set up setOnClickListener for the button
        // 6.4 Retrieve the user input from the EditText
        // 6.5 Set up the xkcd url by completing buildURL (see below)
        //TODO 6.14 If network is active, instantiate your AsyncTask class and call the execute method

        buttonGetComic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editTextComicNo.getText().toString();

                if (userInput.isEmpty()){
                    Log.i(TAG, getString(R.string.edit_text_warning_message));
                    Toast.makeText(MainActivity.this,
                            R.string.edit_text_warning_message,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    URL url = buildURL(userInput);
                    if (Utils.isNetworkAvailable(MainActivity.this)) {
                        GetComic getComic = new GetComic();
                        getComic.execute(url);
                    } else {
                        Toast.makeText(MainActivity.this, "Network not available", Toast.LENGTH_SHORT);
                    }
                }

            }
        });


        //TODO 6.6 - 6.13 Modify GetComic Below *************

    }

    //TODO 6.5 Set up the xkcd url by completing buildURL (see below)
    //TODO you are reminded that this is not part of onCreate
    private URL buildURL(String comicNo){

        String scheme = "https";
        final String authority = "xkcd.com";
        final String back = "info.0.json";
        Uri.Builder builder;
        URL url = null;


        builder = new Uri.Builder();

        if (comicNo.equals("")) {
            builder.scheme(scheme)
                    .authority(authority)
                    .appendPath(back);
        } else {
            builder.scheme(scheme)
                    .authority(authority)
                    .appendPath(comicNo)
                    .appendPath(back);
        }

        Uri uri = builder.build();

        try{
            url = new URL(uri.toString());
            Log.i(TAG,"URL OK: " + url.toString());
        }catch(MalformedURLException e) {
            Log.i(TAG, "malformed URL: " + url.toString());
        }

        return url;

    }

    //TODO 6.6 - 6.13 ****************
    //TODO you are reminded that this is an inner class
    //TODO 6.6 In publish progress, write code to update textViewTitle with a string
    //TODO 6.6 In doInBackground, get the JSON Response
    //TODO 6.7 If the JSON response is null, then call publishProgress with an appropriate message and return null
    //TODO 6.8 Create a new JSONObject wtih a try/catch block
    //TODO 6.9 Using getString, Extract the value with key "safe_title" and display it on textViewTitle
    //TODO 6.10 Extract the image url with key "img"
    //TODO 6.11 Create a URL object and put another catch block
    //TODO 6.12 Get the image with the url
    //TODO 6.13 Complete onPostExecute to assign the Bitmap downloaded to imageView
    class GetComic extends AsyncTask<URL,String,Bitmap> {

        @Override
        protected Bitmap doInBackground (URL... urls) {
            URL myURL = urls[0];
            String JSONstring = Utils.getJson(myURL);
            Bitmap bitmap = null;
            try {
                JSONObject jsonObject = new JSONObject(JSONstring);
                String safe_title = jsonObject.getString("safe_title");
                publishProgress(safe_title);
                String imgURLString = jsonObject.getString("img");
                URL imgURL = new URL(imgURLString);
                bitmap = Utils.getBitmap(imgURL);

            } catch (JSONException ex) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "JSON File is fautly", Toast.LENGTH_SHORT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Img URL is fautly", Toast.LENGTH_SHORT);
            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate (String... values) {
            super.onProgressUpdate(values);
            textViewTitle.setText(values[0]);
        }

        @Override
        protected void onPostExecute (Bitmap bitmap) {
            super .onPostExecute(bitmap);
            if (bitmap != null) {
                imageViewComic.setImageBitmap(bitmap);
            } else {
                Toast.makeText(MainActivity.this, "No bitmap", Toast.LENGTH_SHORT);
            }
        }
    }

}
