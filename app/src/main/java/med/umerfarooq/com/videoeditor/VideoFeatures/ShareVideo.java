package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.MainScreen;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShareVideo
        extends AppCompatActivity
    {
        private static final String APP_DIR = "VideoAPPEditor";
        public static int random;
        public static File file;
        TinyDB tinydb;
        ImageButton share, save;
        String path;
        int min = 100;
        int max = 500;
        InterstitialAd mInterstitialAd;
        Button finish;
        private String TAG = ShareVideo.class.getSimpleName();

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
//            copyDirectoryOneLocationToAnotherLocation(file,new File(Environment.getExternalStorageDirectory() + "/","Finalvideo" + random + ".mp4"));

        }

        public static void exportMp4ToGallery(Context context,String filePath)
        {
            // ビデオのメタデータを作成する
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4");
            values.put(MediaStore.Video.Media.DATA,filePath);
            // MediaStoreに登録
            context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,values);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://" + filePath)));
        }

        private static String getLocalPath()
        {
            return Environment.getExternalStorageDirectory() + "/";
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
            setContentView(R.layout.activity_share_video);
            tinydb = new TinyDB(this);
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

                    @Override
                    public void onAdFailedToLoad(int i)
                    {
                        super.onAdFailedToLoad(i);
                        Toast.makeText(ShareVideo.this,"Video saved Please check your Gallery.." + "Finalvideo" + random + ".mp4",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAdClosed()
                    {
                        Toast.makeText(ShareVideo.this,"Video saved Please check your Gallery.." + "Finalvideo" + random + ".mp4",Toast.LENGTH_LONG).show();

                    }
                });
            finish = findViewById(R.id.finish);
            finish.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent i = new Intent(ShareVideo.this,MainScreen.class);
                        startActivity(i);

                    }
                });
            Random r = new Random();
            random = r.nextInt(max - min + 1) + min;
            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            myToolbar.setTitle("Share and Save Video ");
            myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(myToolbar);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            share = findViewById(R.id.share);
            save = findViewById(R.id.save);
            path = tinydb.getString("path");
            file = new File(path);
            share.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        shareVideo("Video",tinydb.getString("path"));
                    }
                });
            showInterstitial();
            save.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {


                        new TestAsync().execute();
                    }


                });

        }

        public String getGalleryPath()
        {
            File folder = Environment.getExternalStoragePublicDirectory("VideoEditorApp");

            if (!folder.exists()) {
                folder.mkdir();
            }

            return folder.getAbsolutePath();
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

        public void shareVideo(final String title,String path)
        {

            MediaScannerConnection.scanFile(ShareVideo.this,new String[]{path},

                                            null,new MediaScannerConnection.OnScanCompletedListener()
                        {
                            public void onScanCompleted(String path,Uri uri)
                            {
                                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                                shareIntent.setType("video/*");
                                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,title);
                                shareIntent.putExtra(android.content.Intent.EXTRA_TITLE,title);
                                shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                startActivity(Intent.createChooser(shareIntent,"Share Video"));

                            }
                        }
                                           );
        }

        private void showInterstitial()
        {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        public File createTempFile(String prefix,String extension) throws IOException
        {
            File file = new File(getAppDirPath() + "/" + prefix + System.currentTimeMillis() + extension);
            file.createNewFile();
            return file;
        }

        public String getAppDirPath()
        {
            if (getLocalPath() != null) {
                return getLocalPath() + APP_DIR + "/";
            }
            return null;
        }

        private boolean isNetworkAvailable()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        class TestAsync
                extends AsyncTask<Void, Integer, String>
            {
                String TAG = getClass().getSimpleName();

                protected void onPreExecute()
                {
                    super.onPreExecute();
                    if (isNetworkAvailable()) {

                        showInterstitial();
                    } else {
                        Toast.makeText(ShareVideo.this,"Please wait...",Toast.LENGTH_SHORT).show();
                    }

                }

                protected String doInBackground(Void... arg0)
                {
                    Log.d(TAG + " DoINBackGround","On doInBackground...");

                    try {
                        copyDirectoryOneLocationToAnotherLocation(file,new File(Environment.getExternalStorageDirectory() + "/" + "Finalvideo" + random + ".mp4"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MediaScannerConnection.scanFile(ShareVideo.this,new String[]{Environment.getExternalStorageDirectory() + "/" + "Finalvideo" + random + ".mp4"},null,(path,uri) -> {

                        Log.i("ExternalStorage","Scanned " + path + ":");
                        Log.i("ExternalStorage","-> uri=" + uri);
                    });

                    try {
                        copyDirectoryOneLocationToAnotherLocation(file,new File(getGalleryPath() + "/" + "Finalvideo" + random + ".mp4"));
//                            exportMp4ToGallery(ShareVideo.this,path)
                        createTempFile("Finalvideo" + random,".mp4");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    deleteDir(new File(Environment.getExternalStorageDirectory() + "/","Videotemp"));
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
                    return null;
                }

                protected void onProgressUpdate(Integer... a)
                {
                    super.onProgressUpdate(a);
                    Log.d(TAG + " onProgressUpdate","You are in progress update ... " + a[0]);
                }

                protected void onPostExecute(String result)
                {
                    super.onPostExecute(result);
                    if (isNetworkAvailable()) {

                    } else {
                        Toast.makeText(ShareVideo.this,"Video saved Please check your Gallery.." + "Finalvideo" + random + ".mp4",Toast.LENGTH_LONG).show();

                    }

                }
            }
    }
