package med.umerfarooq.com.videoeditor;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;

import life.knowledge4.videotrimmer.utils.FileUtils;
import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.VideoFeatures.VideoTrimmer;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainScreen
        extends AppCompatActivity
    {
        public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
        private static final int REQUEST_VIDEO_TRIMMER = 0x01;
        private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
        TinyDB tinyDB;
       public static AdRequest adRequest;
        public static InterstitialAd mInterstitialAd;
        private AdView mAdView;
        private Button btnFullscreenAd, btnShowRewardedVideoAd;

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
            setContentView(R.layout.activity_main_screen);
            new TestAsync().execute();
            mInterstitialAd = new InterstitialAd(MainScreen.this);
            mInterstitialAd.setAdUnitId(others.intersitial);
            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(MainScreen.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);
            ImageView rate = findViewById(R.id.rate);
            rate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
//                        Toast.makeText(MainScreen.this,"Rate",Toast.LENGTH_SHORT).show();
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW,uri);
                        try {
                            startActivity(myAppLinkToMarket);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(MainScreen.this," unable to find market app",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            ImageView share = findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
//                        Toast.makeText(MainScreen.this,"Share",Toast.LENGTH_SHORT).show();
                        try {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT,"My application name");
                            String sAux = "\nLet me recommend you this application\n\n";
                            sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";
                            i.putExtra(Intent.EXTRA_TEXT,sAux);
                            startActivity(Intent.createChooser(i,"choose one"));
                        } catch (Exception e) {
                            //e.toString();
                        }
                    }
                });

            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
                    .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build();
            tinyDB = new TinyDB(this);
            mAdView.setAdListener(new AdListener()
                {
                    @Override
                    public void onAdLoaded()
                    {
                    }

                    @Override
                    public void onAdClosed()
                    {
//                    Toast.makeText(getApplicationContext(),"Ad is closed!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode)
                    {
//                    Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLeftApplication()
                    {
//                    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdOpened()
                    {
                        super.onAdOpened();
                    }
                });

            mAdView.loadAd(adRequest);

//            Toolbar myToolbar = findViewById(R.id.my_toolbar);
//            myToolbar.setTitle("Home Screen");
//            myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
//            myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
//            setSupportActionBar(myToolbar);
//            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ImageButton galleryButton = findViewById(R.id.fromstorage);
            if (galleryButton != null) {
                galleryButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            pickFromGallery();
                        }
                    });
            }
            ImageButton recordButton = findViewById(R.id.record);
            if (recordButton != null) {
                recordButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            openVideoCapture();
                        }
                    });
            }
        }

        private void openVideoCapture()
        {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(videoCapture,REQUEST_VIDEO_TRIMMER);
        }

        private void pickFromGallery()
        {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,getString(R.string.permission_read_storage_rationale),REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            } else {
                Intent intent = new Intent();
                intent.setTypeAndNormalize("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,getString(R.string.label_select_video)),REQUEST_VIDEO_TRIMMER);
            }
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
        public void onBackPressed()
        {
            super.onBackPressed();
            startActivity(new Intent(MainScreen.this,Suggestions.class));
        }

        @Override
        public void onActivityResult(int requestCode,int resultCode,Intent data)
        {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_VIDEO_TRIMMER) {
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        tinyDB.putString("path",FileUtils.getPath(this,selectedUri));
                        startTrimActivity(selectedUri);
                    } else {
                        Toast.makeText(MainScreen.this,R.string.toast_cannot_retrieve_selected_video,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        private void startTrimActivity(@NonNull Uri uri)
        {

            tinyDB.putString("path",FileUtils.getPath(this,uri));
            Intent intent = new Intent(this,VideoTrimmer.class);
            intent.putExtra(EXTRA_VIDEO_PATH,FileUtils.getPath(this,uri));
            startActivity(intent);
        }

        /**
         * Requests given permission.
         * If the permission has been denied previously, a Dialog will prompt the user to grant the
         * permission, otherwise it is requested directly.
         */
        private void requestPermission(final String permission,
                String rationale,
                final int requestCode
                                      )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.permission_title_rationale));
                builder.setMessage(rationale);
                builder.setPositiveButton(getString(R.string.label_ok),new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,int which)
                        {
                            ActivityCompat.requestPermissions(MainScreen.this,new String[]{permission},requestCode);
                        }
                    });
                builder.setNegativeButton(getString(R.string.label_cancel),null);
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
            }
        }

        /**
         * Callback received when a permissions request has been completed.
         */
        @Override
        public void onRequestPermissionsResult(int requestCode,
                @NonNull String[] permissions,
                @NonNull int[] grantResults
                                              )
        {
            switch (requestCode) {
                case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        pickFromGallery();
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode,permissions,grantResults);
            }
        }

        // UPDATED!
        public String getPath(Uri uri)
        {
            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
            if (cursor != null) {
                // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
                // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else return null;
        }

       public static class TestAsync extends AsyncTask<Void, Integer, String>
            {
                String TAG = getClass().getSimpleName();

                protected void onPreExecute (){
                    super.onPreExecute();
                    Log.d(TAG + " PreExceute","On pre Exceute......");
                }

                protected String doInBackground(Void...arg0) {
                    Log.d(TAG + " DoINBackGround","On doInBackground...");



                    // set the ad unit ID

                     adRequest = new AdRequest.Builder().build();



                    return "You are at PostExecute";
                }

                protected void onProgressUpdate(Integer...a){
                    super.onProgressUpdate(a);
                    Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
                }

                protected void onPostExecute(String result) {
                    mInterstitialAd.loadAd(adRequest);
                    super.onPostExecute(result);
                    Log.d(TAG + " onPostExecute", "" + result);
                }
            }
    }
