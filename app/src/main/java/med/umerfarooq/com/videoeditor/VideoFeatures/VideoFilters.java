package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlBoxBlurFilter;
import com.daasuu.mp4compose.filter.GlBulgeDistortionFilter;
import com.daasuu.mp4compose.filter.GlFilter;
import com.daasuu.mp4compose.filter.GlGrayScaleFilter;
import com.daasuu.mp4compose.filter.GlHazeFilter;
import com.daasuu.mp4compose.filter.GlLutFilter;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.MainScreen;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VideoFilters
        extends AppCompatActivity
    {
        public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
        private static final String TAG = "SAMPLE";
        private static final int PERMISSION_REQUEST_CODE = 88888;
        String perviouPath;
        Boolean Pervious = true;
        MediaController videoMediaController;
        String videoPath;
        String path;
        TinyDB tinydb;
        Button skip;
        InterstitialAd mInterstitialAd;
        VideoView videoView;
        String[] filterArray = {"Simple Filter","GlayScaleFilter","MonoChromeFilter","BoxBlurFilter","LutFilter","BulgeDistortionFilter","VignetteFilter","HazeFilter"};
        private AdView mAdView;
        private Mp4Composer mp4Composer;
        private Bitmap bitmap;
        private CheckBox muteCheckBox;
        private Button btnFullscreenAd, btnShowRewardedVideoAd;

        /**
         * ギャラリーにエクスポート
         *
         * @param filePath
         * @return The video MediaStore URI
         */
        public static void exportMp4ToGallery(Context context,String filePath)
        {
            // ビデオのメタデータを作成する
            final ContentValues values = new ContentValues(1);
           values.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4");
            values.put(MediaStore.Video.Media.DATA,filePath);
            // MediaStoreに登録
            context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,values);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://" + filePath)));
        }

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
            setContentView(R.layout.activity_video_filters);
            new MainScreen.TestAsync().execute();
            tinydb = new TinyDB(this);
            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(VideoFilters.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);

            mInterstitialAd = new InterstitialAd(this);

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(others.intersitial);

            AdRequest adRequest = new AdRequest.Builder().build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener()
                {
                    public void onAdLoaded()
                    {

                    }
                });
            tinydb.putString("filter",filterArray[0]);

            ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.fiter_listview,filterArray);

            ListView listView = (ListView) findViewById(R.id.video_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent,View view,int position,long id)
                    {
                        Toast.makeText(VideoFilters.this,"Filter Selected",Toast.LENGTH_SHORT).show();
                        tinydb.putString("filter",filterArray[position]);
                    }
                });

            videoPath = getVideoFilePath();
            videoView = (VideoView) findViewById(R.id.VideoView);

            path = tinydb.getString("path");
            perviouPath = path;
            videoMediaController = new MediaController(this);
            videoView.setVideoPath(path);
            videoMediaController.setMediaPlayer(videoView);
            videoView.setMediaController(videoMediaController);
            videoView.requestFocus();
            videoView.start();


            muteCheckBox = findViewById(R.id.mute_check_box);

            findViewById(R.id.adda).setOnClickListener(v -> {
//                v.setEnabled(false);
                videoView.stopPlayback();
                startCodec();
            });

            findViewById(R.id.cancel_button).setOnClickListener(v -> {
                if (mp4Composer != null) {
                    mp4Composer.cancel();
                }
            });

            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lookup_sample);


            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
                    .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build();


            mAdView.loadAd(adRequest);

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            myToolbar.setTitle("Add Filters");
            myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(myToolbar);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            skip = findViewById(R.id.button);
            skip.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        Intent i = new Intent(VideoFilters.this,VideoText.class);


                        startActivity(i);
                    }
                });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu,menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home) {

                finish();

            }
            switch (item.getItemId()) {
                case R.id.clear:
                    videoView.setVideoPath(perviouPath);
                    videoMediaController.setMediaPlayer(videoView);
                    videoView.setMediaController(videoMediaController);
                    videoView.requestFocus();
                    videoView.start();

                    tinydb.putString("path",perviouPath);
                    File delfile = new File(videoPath);
                    if (delfile.exists()) {

                        delfile.delete();

                    }

                    return true;
                case R.id.cancel:

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(VideoFilters.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(VideoFilters.this);
                    }
                    builder.setTitle("Delete entry")
                            .setMessage("Are you sure you wanna discard all changes and cancel?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDir(new File(Environment.getExternalStorageDirectory() + "/","Videotemp"));
                                    startActivity(new Intent(VideoFilters.this,MainScreen.class));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }

        }

        @Override
        protected void onResume()
        {
            super.onResume();

            if (!videoView.isPlaying()) {

                videoMediaController = new MediaController(this);
                videoView.setVideoPath(path);
                videoMediaController.setMediaPlayer(videoView);
                videoView.setMediaController(videoMediaController);
                videoView.requestFocus();
                videoView.start();

            }
        }

        private void startCodec()
        {


            final ProgressBar progressBar = findViewById(R.id.progress_bar);
            progressBar.setMax(100);

            boolean mute = muteCheckBox.isChecked();

            mp4Composer = null;
            Mp4Composer mp4Composer = new Mp4Composer(path,videoPath);
            // .rotation(Rotation.ROTATION_270)
            //.size(720, 1280)
            mp4Composer.fillMode(FillMode.PRESERVE_ASPECT_FIT);
//            mp4Composer.filter(new GlMonochromeFilter());
            String filtermatch = tinydb.getString("filter");


            if (filtermatch.equals(filterArray[0])) {

                mp4Composer.filter(new GlFilter());

            }

            if (filtermatch.equals(filterArray[1])) {

                mp4Composer.filter(new GlGrayScaleFilter());

            }

            if (filtermatch.equals(filterArray[2])) {

                mp4Composer.filter(new GlMonochromeFilter());

            }

            if (filtermatch.equals(filterArray[3])) {

                mp4Composer.filter(new GlBoxBlurFilter());

            }

            if (filtermatch.equals(filterArray[4])) {

                mp4Composer.filter(new GlLutFilter(bitmap));

            }

            if (filtermatch.equals(filterArray[5])) {

                mp4Composer.filter(new GlBulgeDistortionFilter());

            }

            if (filtermatch.equals(filterArray[6])) {

                mp4Composer.filter(new GlVignetteFilter());

            }

            if (filtermatch.equals(filterArray[7])) {

                mp4Composer.filter(new GlHazeFilter());

            }


            mp4Composer.mute(mute);
            mp4Composer.listener(new Mp4Composer.Listener()
                {
                    @Override
                    public void onProgress(double progress)
                    {
                        Log.d(TAG,"onProgress = " + progress);
                        runOnUiThread(() -> progressBar.setProgress((int) (progress * 100)));
                    }

                    @Override
                    public void onCompleted()
                    {
//                            skip.setText("Next");
                        runOnUiThread(() -> skip.setText("Next"));
                        Log.d(TAG,"onCompleted()");
                      exportMp4ToGallery(getApplicationContext(),videoPath);
                        tinydb.putString("path",videoPath);
                        runOnUiThread(() -> {
                            progressBar.setProgress(100);
                            findViewById(R.id.adda).setEnabled(true);
//                                Toast.makeText(VideoFilters.this, "codec complete path =" + videoPath, Toast.LENGTH_SHORT).show();
                            videoView.setVideoPath(videoPath);

                            videoView.start();
                        });
                    }

                    @Override
                    public void onCanceled()
                    {

                    }

                    @Override
                    public void onFailed(Exception exception)
                    {
                        Log.d(TAG,"onFailed()");
                    }
                });
            mp4Composer.start();


        }

        public File getAndroidMoviesFolder()
        {
            return Environment.getExternalStorageDirectory();
        }

        @SuppressLint("SimpleDateFormat")
        public String getVideoFilePath()
        {
            return getAndroidMoviesFolder().getAbsolutePath() + "/Videotemp/" + new SimpleDateFormat("yyyyMM_dd-HHmmss").format(new Date()) + "filter_apply.mp4";
        }

        private boolean checkPermission()
        {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            // request permission if it has not been grunted.
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                return false;
            }

            return true;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                @NonNull String[] permissions,
                @NonNull int[] grantResults
                                              )
        {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(VideoFilters.this,"permission has been grunted.",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VideoFilters.this,"[WARN] permission is not grunted.",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        private void showInterstitial()
        {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
        public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation,
                File targetLocation
                                                                    ) throws IOException
        {

            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdir();
                }

                String[] children = sourceLocation.list();
                for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                    copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation,children[i]),new File(targetLocation,children[i]));
                }
            } else {

                InputStream in = new FileInputStream(sourceLocation);

                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf,0,len);
                }
                in.close();
                out.close();
            }


        }
        public static boolean deleteDir(File dir)
        {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir,children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

            return dir.delete();
        }
    }
