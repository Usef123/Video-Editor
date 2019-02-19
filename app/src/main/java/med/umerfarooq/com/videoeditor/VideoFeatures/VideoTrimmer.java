package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;
import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.MainScreen;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VideoTrimmer
        extends AppCompatActivity
        implements OnTrimVideoListener, OnK4LVideoListener
    {
        InterstitialAd mInterstitialAd;
        TinyDB tinyDB;
        private String TAG = VideoTrimmer.class.getSimpleName();
        private AdView mAdView;
        private Button btnFullscreenAd, btnShowRewardedVideoAd;
        private K4LVideoTrimmer mVideoTrimmer;
        private ProgressDialog mProgressDialog;

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
            setContentView(R.layout.activity_video_trimmer);
            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(VideoTrimmer.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);
            Intent extraIntent = getIntent();
            String path = "";

            tinyDB = new TinyDB(this);

            if (extraIntent != null) {
                path = extraIntent.getStringExtra(MainScreen.EXTRA_VIDEO_PATH);

                tinyDB.putString("path",path);
            }

            //setting progressbar
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.trimming_progress));
//            mInterstitialAd = new InterstitialAd(this);

            // set the ad unit ID
//            mInterstitialAd.setAdUnitId(others.intersitial);
            AdRequest adRequest = new AdRequest.Builder().build();


//            mInterstitialAd.loadAd(adRequest);
            MainScreen.mInterstitialAd.setAdListener(new AdListener()
                {
                    public void onAdLoaded()
                    {

                    }

                    @Override
                    public void onAdFailedToLoad(int i)
                    {
                        super.onAdFailedToLoad(i);
                        if(isNetworkAvailable()) {
                            startActivity(new Intent(VideoTrimmer.this,VideoFilters.class));
                        }
                    }

                    @Override
                    public void onAdClosed()
                    {

                        startActivity(new Intent(VideoTrimmer.this,VideoFilters.class));
                    }
                });

            mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
            if (mVideoTrimmer != null) {
                mVideoTrimmer.setMaxDuration(10);
                mVideoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "trimVideo.mp4");
                mVideoTrimmer.setOnTrimVideoListener(VideoTrimmer.this);
                mVideoTrimmer.setOnK4LVideoListener(VideoTrimmer.this);

                mVideoTrimmer.setVideoURI(Uri.parse(path));
                mVideoTrimmer.setVideoInformationVisibility(true);
            }
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)

                    .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build();

            mAdView.setAdListener(new AdListener()
                {
                    @Override
                    public void onAdLoaded()
                    {
                    }

                    @Override
                    public void onAdClosed()
                    {
//
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode)
                    {
//
                    }

                    @Override
                    public void onAdLeftApplication()
                    {
//
                    }

                    @Override
                    public void onAdOpened()
                    {
                        super.onAdOpened();
                    }
                });

            mAdView.loadAd(adRequest);

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            myToolbar.setTitle("Cut Video");
            myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(myToolbar);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Button button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        if(isNetworkAvailable()) {
                            showInterstitial();
                        }else{
                            startActivity(new Intent(VideoTrimmer.this,VideoFilters.class));
                        }

                    }
                });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();


            if (id == android.R.id.home) {
                finish();

            }
            return super.onOptionsItemSelected(item);
        }
        @Override
        protected void onResume()
        {
            super.onResume();
            MainScreen v = new MainScreen();
            new MainScreen.TestAsync().execute();
        }

        @Override
        public void onTrimStarted()
        {
            mProgressDialog.show();
        }

        @Override
        public void getResult(final Uri uri)
        {
            mProgressDialog.cancel();
            tinyDB.putString("path",uri.getPath());
            runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(VideoTrimmer.this,getString(R.string.video_saved_at,uri.getPath()),Toast.LENGTH_SHORT).show();
                        if(isNetworkAvailable()) {
                            showInterstitial();
                        }else{
                            startActivity(new Intent(VideoTrimmer.this,VideoFilters.class));
                        }
                    }
                });

            finish();
        }

        @Override
        public void cancelAction()
        {
            mProgressDialog.cancel();
            mVideoTrimmer.destroy();
            finish();
        }

        @Override
        public void onError(final String message)
        {
            mProgressDialog.cancel();

            runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(VideoTrimmer.this,message,Toast.LENGTH_SHORT).show();
                    }
                });
        }

        @Override
        public void onVideoPrepared()
        {
            runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
//
                    }
                });
        }

        private void showInterstitial()
        {
            if (MainScreen.mInterstitialAd.isLoaded()) {
                MainScreen.mInterstitialAd.show();
            }else{
                startActivity(new Intent(VideoTrimmer.this,VideoFilters.class));
            }
        }

        private boolean isNetworkAvailable()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
