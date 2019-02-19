package med.umerfarooq.com.videoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.Json.HttpHandler;
import med.umerfarooq.com.videoeditor.Json.JSONParser;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen
        extends AppCompatActivity
    {
        private static int SPLASH_TIME_OUT = 5000;
        String Title = null;
        String Description = null;
        String iconUrl = null;
        JSONParser jParser = new JSONParser();
        String review = null;
        TinyDB tinydb;
        String title;
        String description;
        String icon_url;
        String target_url;
        String sound=null;
        ArrayList titlel;
        ArrayList descriptionl;
        ArrayList icon_urll;
        ArrayList target_urll;
        String jsonStr;

        @Override
        protected void attachBaseContext(Context newBase)
        {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf").setFontAttrId(R.attr.fontPath).build());
            setContentView(R.layout.activity_splash_screen);


            if (isNetworkAvailable()) {
                new LoadImageData().execute();
            } else {
                int SPLASH_TIME_OUT = 1000;
                new Handler().postDelayed(new Runnable()
                    {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                        @Override
                        public void run()
                        {


                            startActivity(new Intent(SplashScreen.this,Suggestions.class));


                            // close this activity

                        }
                    },SPLASH_TIME_OUT);


            }
            tinydb = new TinyDB(SplashScreen.this);


        }

        private boolean isNetworkAvailable()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        private class LoadImageData
                extends AsyncTask<String, String, String>
            {


                @Override
                protected void onPreExecute()
                {
                    super.onPreExecute();

//
                    titlel = new ArrayList();
                    descriptionl = new ArrayList();
                    icon_urll = new ArrayList();
                    target_urll = new ArrayList();

                }

                protected String doInBackground(String... args)
                {


                    HttpHandler sh = new HttpHandler();
                    // Making a request to url and getting response
                    String url ="https://api.myjson.com/bins/pysrr";

                     jsonStr = sh.makeServiceCall(url);


                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsonStr);
                            sound = jsonObj.getString("sounds_name");

                            // Getting JSON Array node
                            JSONArray contacts = jsonObj.getJSONArray("apps_data");

                            // looping through All Contacts
                            for (int i = 0; i < contacts.length(); i++) {
                                JSONObject c = contacts.getJSONObject(i);

                                title = c.getString("title");
                                description = c.getString("description");
                                icon_url = c.getString("icon_url");
                                target_url = c.getString("target_url");


                                titlel.add(title);
                                descriptionl.add(description);
                                icon_urll.add(icon_url);
                                target_urll.add(target_url);


                            }
                        } catch (final JSONException e) {

                            runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
//                                        Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });

                        }

                    } else {

                        runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
//                                    Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!",Toast.LENGTH_LONG).show();
                                }
                            });
                    }


                    return null;


                }


                @SuppressLint("SetTextI18n")
                protected void onPostExecute(String file_url)
                {


//
                    if (jsonStr != null) {
                        if(title!=null){
                            tinydb.putListString("title",titlel);
                        }
                        if(descriptionl!=null) {
                            tinydb.putListString("description",descriptionl);
                        }
                        if(icon_urll!=null) {
                            tinydb.putListString("icon_url",icon_urll);
                        }
                        if(target_urll!=null) {
                            tinydb.putListString("target_url",target_urll);
                        }
                        if(sound!=null) {
                            tinydb.putString("sound",sound);
                        }


                        startActivity(new Intent(SplashScreen.this,Suggestions.class));

                    }else{
                        tinydb.putString("sound","enable");

                        startActivity(new Intent(SplashScreen.this,Suggestions.class));
                    }
                }
            }
    }







