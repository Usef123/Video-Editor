package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.FFmpeg.DaggerDependencyModuleSound;
import med.umerfarooq.com.videoeditor.MainScreen;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.R;
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.CustomAdapterStickers;
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.DataModelStickers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VideoSound
        extends AppCompatActivity
        implements OnClickListener
    {
        private static final String TAG = VideoSound.class.getSimpleName();
        private static CustomAdapterStickers adapter;
        public final int AUDIO_REQUEST = 101;
        @Inject
        FFmpeg ffmpeg;
        TextView VideoSound;
        //        @InjectView(R.id.command)
        String seconds = "0";
        Button skip;
        boolean extractbol = false;
        String perviouPath;
        Boolean Pervious = true;
        MediaController videoMediaController;
        String[] command;
        Switch switch1;
        @InjectView(R.id.run_command)

        Button runButton;
        @InjectView(R.id.extract)
        Button extract;
        TinyDB tinydb;
        String path;
        EditText size;
        Boolean merge = false;
        ArrayList<DataModelStickers> dataModels;
        ListView listView;
        EditText getse;
        EditText getsec;
        VideoView videoView;
        Uri imguri;

        InterstitialAd mInterstitialAd;
        Bitmap bm;
        private String[] soundarray = {"Rockstar","Akcent","Dance Floor","Dark Thril","Despicto","Good Morning",
                "Let me love you"};

        private int[] soundlist = {R.raw.rockstar,R.raw.akcent,R.raw.dancefloor,R.raw.darkthril,R.raw.despicto,R.raw.goodmorning,R.raw.letmeloveyou};
        private ProgressDialog progressDialog;
        private AdView mAdView;
        private Button btnFullscreenAd, btnShowRewardedVideoAd;

        @Override
        protected void attachBaseContext(Context newBase)
        {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build());
            setContentView(R.layout.activity_video_sound);

            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(VideoSound.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);
            getsec = findViewById(R.id.seconds);
            seconds = getsec.getText().toString();

            videoView = findViewById(R.id.VideoVieww);
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
//                    showInterstitial();
                    }

                    @Override
                    public void onAdClosed()
                    {

                    }
                });
            path = tinydb.getString("path");
            perviouPath = path;
            Log.d("path",path);
//            Toast.makeText(this,""+path,Toast.LENGTH_SHORT).show();
            videoMediaController = new MediaController(this);
            videoView.setVideoPath(path);
            videoMediaController.setMediaPlayer(videoView);
            videoView.setMediaController(videoMediaController);
            videoView.requestFocus();
            videoView.start();


            switch1 = findViewById(R.id.switch1);
            size = findViewById(R.id.size);
            ButterKnife.inject(this);
            ObjectGraph.create(new DaggerDependencyModuleSound(this)).inject(this);

            loadFFMpegBinary();
            initUI();

            getsec.setOnEditorActionListener(new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
                    {
                        if ((event != null && event.getKeyCode() == 66) || actionId == 6) {
                            hideSoftKeyboard(VideoSound.this,getsec);
                        }
                        return false;
                    }
                });
            ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.fiter_listview,soundarray);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
            imguri = Uri.fromFile(copyFiletoExternalStorage(soundlist[0],"song.mp3"));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent,View view,int position,long id)
                    {

                        Toast.makeText(VideoSound.this,"Sound Selected",Toast.LENGTH_SHORT).show();
                        imguri = Uri.fromFile(copyFiletoExternalStorage(soundlist[position],"song.mp3"));
                    }
                });
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device ID
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
            myToolbar.setTitle("Add Sound");
            myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
            myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(myToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            skip = findViewById(R.id.button);
            skip.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startActivity(new Intent(VideoSound.this,VideoThemes.class));
                    }
                });
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
        @SuppressLint("WrongConstant")
        private void hideSoftKeyboard(Activity activity,View view)
        {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
        }
        @Override
        protected void onPause()
        {
            super.onPause();
            videoView.stopPlayback();
        }

        private String getAudioPath(Uri uri)
        {
            String[] data = {MediaStore.Audio.Media.DATA};
            CursorLoader loader = new CursorLoader(getApplicationContext(),uri,data,null,null,null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.sound_menu,menu);
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
                    File delfile = new File(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sound.mp4");
                    if (delfile.exists()) {

                        delfile.delete();

                    }

                    return true;
                case R.id.chooseaudio:
                    videoView.stopPlayback();
                    Intent videoIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(videoIntent,"Select Audio"),AUDIO_REQUEST);
                    return true;
                case R.id.cancel:

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(VideoSound.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(VideoSound.this);
                    }
                    builder.setTitle("Delete entry")
                            .setMessage("Are you sure you wanna discard all changes and cancel?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDir(new File(Environment.getExternalStorageDirectory() + "/","Videotemp"));
                                    startActivity(new Intent(VideoSound.this,MainScreen.class));
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
        protected void onActivityResult(int requestCode,int resultCode,Intent data)
        {
            if (requestCode == AUDIO_REQUEST && null != data) {
                if (requestCode == AUDIO_REQUEST) {


                    videoView.setVideoPath(path);
//                    tinydb.putString("path",Environment.getExternalStorageDirectory().getPath() +  "/Videotemp/" + "sound.mp4");
                    videoView.start();

                    Uri uri = data.getData();
                    try {
                        String uriString = uri.toString();
                        File myFile = new File(uriString);
                        //    String path = myFile.getAbsolutePath();
                        String displayName = null;
                        String path2 = getAudioPath(uri);
                        File f = new File(path2);
                        long fileSizeInBytes = f.length();
                        long fileSizeInKB = fileSizeInBytes / 1024;
                        long fileSizeInMB = fileSizeInKB / 1024;
                        if (fileSizeInMB > 8) {
//                            customAlterDialog("Can't Upload ", "sorry file size is large");
                        } else {
                            imguri = Uri.parse(path2);

                        }
                    } catch (Exception e) {
                        //handle exception
//                        Toast.makeText(GroupDetailsActivity.this, "Unable to process,try again", Toast.LENGTH_SHORT).show();
                    }
                    //   String path1 = uri.getPath();

                }
            }

        }

        private void initUI()
        {
            runButton.setOnClickListener(this);
            extract.setOnClickListener(this);
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(null);
        }

        private void loadFFMpegBinary()
        {
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler()
                    {
                        @Override
                        public void onFailure()
                        {
                            showUnsupportedExceptionDialog();
                        }
                    });
            } catch (FFmpegNotSupportedException e) {
                showUnsupportedExceptionDialog();
            }
        }

        private void execFFmpegBinary(final String[] command)
        {
            try {
                ffmpeg.execute(command,new ExecuteBinaryResponseHandler()
                    {
                        @Override
                        public void onFailure(String s)
                        {
                            addTextViewToLayout("FAILED with output : " + s);
                        }

                        @Override
                        public void onSuccess(String s)
                        {
                            addTextViewToLayout("SUCCESS with output : " + s);
                        }

                        @Override
                        public void onProgress(String s)
                        {
                            Log.d(TAG,"Started command : ffmpeg " + command);
                            addTextViewToLayout("progress : " + s);
                            progressDialog.setMessage("Processing\n" + s);
                        }

                        @Override
                        public void onStart()
                        {


                            Log.d(TAG,"Started command : ffmpeg " + command);
                            progressDialog.setMessage("Processing...");
                            progressDialog.show();
                        }

                        @Override
                        public void onFinish()
                        {
                            skip.setText("Next");

                            if (extractbol) {

                                runButton.setText("Merge");
                                merge = true;
                            }
                            videoView.setVideoPath(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sound.mp4");
                            tinydb.putString("path",Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sound.mp4");
                            videoView.start();

                            Log.d(TAG,"Finished command : ffmpeg " + command);
                            progressDialog.dismiss();
                        }
                    });
            } catch (FFmpegCommandAlreadyRunningException e) {
                // do nothing for now
            }
        }

        private void addTextViewToLayout(String text)
        {
            TextView textView = new TextView(VideoSound.this);
            textView.setText(text);

        }

        private void showUnsupportedExceptionDialog()
        {
            new AlertDialog.Builder(VideoSound.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
//                            VideoSound.finish();
                    }
                }).create().show();

        }

        @Override
        public void onClick(View v)
        {
            String[] command;
            switch (v.getId()) {
                case R.id.run_command:
//
                    videoView.stopPlayback();

                    command = new String[]{"-y","-i",path,"-itsoffset","00:" + getsec.getText().toString(),"-i",String.valueOf(imguri),"-map","0:0","-map","1:0","-c:v","copy","-shortest","-async","1",Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sound.mp4"};

//
                    if (command.length != 0) {
                        execFFmpegBinary(command);
                    } else {
                        Toast.makeText(VideoSound.this,getString(R.string.empty_command_toast),Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.extract:

                    extractbol = true;
                    Toast.makeText(this,"" + seconds,Toast.LENGTH_SHORT).show();
                    videoView.stopPlayback();
//
                    command = new String[]{"-y","-i",path,"-i",String.valueOf(imguri),"-q:a","0","-map","a",Environment.getExternalStorageDirectory().getPath() + "/" + "Extract.mp3"};

                    if (command.length != 0) {
                        execFFmpegBinary(command);
                    } else {
                        Toast.makeText(VideoSound.this,getString(R.string.empty_command_toast),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

        private File copyFiletoExternalStorage(int resourceId,String resourceName)
        {
            String pathSDCard = Environment.getExternalStorageDirectory() + "/Android/data/";
            File file = new File(pathSDCard,resourceName);
            try {
                InputStream in = getResources().openRawResource(resourceId);
                FileOutputStream out = null;
                out = new FileOutputStream(file);
                byte[] buff = new byte[1024];
                int read = 0;
                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff,0,read);
                    }
                } finally {
                    in.close();
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }

        private void showInterstitial()
        {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
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


