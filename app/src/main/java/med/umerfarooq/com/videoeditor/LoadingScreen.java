package med.umerfarooq.com.videoeditor;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import med.umerfarooq.com.videoeditor.Other.others;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoadingScreen
        extends AppCompatActivity
    {

     public static   InterstitialAd mInterstitialAd;
        int SPLASH_TIME_OUT = 8000;
        int i = 0;
        Boolean addload = false;
        private String TAG = LoadingScreen.class.getSimpleName();

        @Override
        protected void attachBaseContext(Context newBase)
        {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build());
            setContentView(R.layout.activity_loading_screen);

            new TestAsync().execute();
            mInterstitialAd = new InterstitialAd(this);
            File dir = new File(Environment.getExternalStorageDirectory(),"/Videotemp");
            try {
                if (dir.mkdir()) {
                    System.out.println("Directory created");
                } else {
                    System.out.println("Directory is not created");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // set the ad unit ID
            mInterstitialAd.setAdUnitId(others.intersitial);

            AdRequest adRequest = new AdRequest.Builder().build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener()


                {
                    public void onAdLoaded()
                    {
                        showInterstitial();
                    }

                    @Override
                    public void onAdFailedToLoad(int i)
                    {
                        super.onAdFailedToLoad(i);

                        startActivity(new Intent(LoadingScreen.this,MainScreen.class));
                    }

                    @Override
                    public void onAdClosed()
                    {
                        addload = true;
                        startActivity(new Intent(LoadingScreen.this,MainScreen.class));
                    }
                });

            loading();

//

        }

        private void loading()
        {
//



            new Handler().postDelayed(new Runnable()
                {



                    @Override
                    public void run()
                    {


                        if(!addload){
                        startActivity(new Intent(LoadingScreen.this,MainScreen.class));
                    }

//                          showInterstitial();




                        // close this activity

                    }
                },SPLASH_TIME_OUT);

        }

        private void showInterstitial()
        {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        private boolean isNetworkConnected()
        {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null;
        }

        private void copyAssets()
        {
            AssetManager assetManager = getAssets();
            String[] files = null;
            try {
                files = assetManager.list("");
            } catch (IOException e) {
                Log.e("tag","Failed to get asset file list.",e);
            }
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open("Roboto-Medium.ttf");

                    String outDir = Environment.getExternalStorageDirectory().getAbsolutePath();

                    File outFile = new File(outDir,"Roboto-Medium.ttf");

                    out = new FileOutputStream(outFile);
                    copyFile(in,out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    Log.e("tag","Failed to copy asset file: " + filename,e);
                }
//                Toast.makeText(this,"done",Toast.LENGTH_SHORT).show();
            }
        }

        private void copyFile(InputStream in,OutputStream out) throws IOException
        {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer,0,read);
            }
        }

        class TestAsync
                extends AsyncTask<Void, Integer, String>
            {
                String TAG = getClass().getSimpleName();

                protected void onPreExecute()
                {
                    super.onPreExecute();
                    Log.d(TAG + " PreExceute","On pre Exceute......");
                }

                protected String doInBackground(Void... arg0)
                {
                    Log.d(TAG + " DoINBackGround","On doInBackground...");
                    copyAssets();

                    return "You are at PostExecute";
                }

                protected void onProgressUpdate(Integer... a)
                {
                    super.onProgressUpdate(a);
                    Log.d(TAG + " onProgressUpdate","You are in progress update ... " + a[0]);
                }

                protected void onPostExecute(String result)
                {
                    super.onPostExecute(result);
                    Log.d(TAG + " onPostExecute","" + result);
                }
            }
    }