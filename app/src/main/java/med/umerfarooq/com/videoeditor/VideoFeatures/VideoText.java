package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
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

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.FFmpeg.DaggerDependencyModule;
import med.umerfarooq.com.videoeditor.MainScreen;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VideoText
        extends AppCompatActivity
        implements OnClickListener, OnDragListener, OnTouchListener
    {

        private static final String TAG = VideoText.class.getSimpleName();

        @Inject
        FFmpeg ffmpeg;
        TextView videotext;
        @InjectView(R.id.command)
        EditText commandEditText;
        String[] command;
        Switch switch1;
        @InjectView(R.id.run_command)
        Button runButton;
        TinyDB tinydb;
        String path;
        String perviouPath;
        Boolean Pervious = true;
        MediaController videoMediaController;
        Button skip;
        VideoView videoView;
        TextView tvTextToAdd;
        int size = 50;
        Button positive, neg;
        InterstitialAd mInterstitialAd;
        private long durationInMs;
        private int tvSize = 10;
        private RelativeLayout rlTop;
        private ProgressDialog progressDialog;
        private AdView mAdView;
        private String xPos = "(w-tw)/2";
        private int xResolution;
        private float xTextView;
        private float xVideoView;
        private String yPos = "(h/PHI)+th";
        private int yResolution;
        private float yTextView;
        private float yVideoView;
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
            setContentView(R.layout.activity_video_text);
            tvTextToAdd = findViewById(R.id.tvTextToAdd);
            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(VideoText.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);
            positive = findViewById(R.id.plus);
            neg = findViewById(R.id.neg);
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
//                   showInterstitial();
                    }

                    @Override
                    public void onAdClosed()
                    {

                    }


                });
            tvTextToAdd.setOnTouchListener(this);
            tinydb = new TinyDB(this);
            path = tinydb.getString("path");

            perviouPath = path;
            getDataFromIntent();
            videoView = (VideoView) findViewById(R.id.VideoVieww);
            this.rlTop = (RelativeLayout) findViewById(R.id.root);
            this.rlTop.setOnDragListener(this);
//            videotext=findViewById(R.id.videotext);
            videoMediaController = new MediaController(this);
            videoView.setVideoPath(path);
            videoMediaController.setMediaPlayer(videoView);
            videoView.setMediaController(videoMediaController);
            videoView.requestFocus();
            videoView.start();
            switch1 = findViewById(R.id.switch1);

            ButterKnife.inject(this);
            ObjectGraph.create(new DaggerDependencyModule(this)).inject(this);

            loadFFMpegBinary();
            initUI();
            positive.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        size = size + 13;
                        tvSize = tvSize + 2;
                        tvTextToAdd.setTextSize((float) tvSize);
                    }
                });
            neg.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        size = size - 13;
                        if (size < 50) {
                            size = 50;
                            tvSize = 10;
                        } else {
                            tvSize = tvSize - 2;
                        }
                        tvTextToAdd.setTextSize(tvSize);
                    }
                });
            commandEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
                    {
                        if ((event != null && event.getKeyCode() == 66) || actionId == 6) {
                            hideSoftKeyboard(VideoText.this,commandEditText);
                        }
                        return false;
                    }
                });
            commandEditText.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void onTextChanged(CharSequence s,int start,int before,int count)
                    {
                        tvTextToAdd.setVisibility(View.VISIBLE);
                        tvTextToAdd.setText(s.toString().trim());
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s,int start,int count,int after)
                    {

                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {

                        // TODO Auto-generated method stub
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

            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            myToolbar.setTitle("Add Text");
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
                        Intent i = new Intent(VideoText.this,VideoStickers.class);


                        startActivity(i);
                    }
                });
        }

        @SuppressLint("WrongConstant")
        private void hideSoftKeyboard(Activity activity,View view)
        {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
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
                    File delfile = new File(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "textVideo.mp4");
                    if (delfile.exists()) {

                        delfile.delete();

                    }

                    return true;

                case R.id.cancel:

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(VideoText.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(VideoText.this);
                    }
                    builder.setTitle("Delete entry")
                            .setMessage("Are you sure you wanna discard all changes and cancel?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDir(new File(Environment.getExternalStorageDirectory() + "/","Videotemp"));
                                    startActivity(new Intent(VideoText.this,MainScreen.class));
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

        private void initUI()
        {
            runButton.setOnClickListener(this);

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

                            tvTextToAdd.setVisibility(View.GONE);
                            Log.d(TAG,"Started command : ffmpeg " + command);
                            progressDialog.setMessage("Processing...");
                            progressDialog.show();
                        }

                        @Override
                        public void onFinish()
                        {
                            videoView.setVideoPath(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "textVideo.mp4");
                            tvTextToAdd.setVisibility(View.GONE);
                            videoView.start();
                            tinydb.putString("path",Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "textVideo.mp4");
                            skip.setText("Next");
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
            TextView textView = new TextView(VideoText.this);
            textView.setText(text);

        }

        private void showUnsupportedExceptionDialog()
        {
            new AlertDialog.Builder(VideoText.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
//                            VideoText.finish();
                    }
                }).create().show();

        }

        @Override
        public void onClick(View v)
        {

            switch (v.getId()) {
                case R.id.run_command:
                    prepareScale();
                    videoView.stopPlayback();
                    String cmd = commandEditText.getText().toString();

                    String[] command = new String[]{"-y","-i",path,"-vf","drawtext=fontfile=" + Environment.getExternalStorageDirectory().getPath() + "/" + "Roboto-Medium.ttf" + ":text='" + cmd + "'" + ":fontcolor=white" + ":fontsize=" + this.size + ":x=" + xPos + ":y=" + yPos,"-y","-c:v","libx264","-preset","ultrafast",Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "textVideo.mp4"};



                    if (command.length != 0) {
                        execFFmpegBinary(command);
                    } else {
                        Toast.makeText(VideoText.this,getString(R.string.empty_command_toast),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

        @Override
        protected void onPause()
        {
            super.onPause();
            videoView.stopPlayback();
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


        private void prepareScale()
        {

//            xPos = Float.toString((float) ((xTextView / xVideoView) * ((float) xResolution) - (tvTextToAdd.getWidth()+tvTextToAdd.getWidth()/2.4)));
            xVideoView = (float) videoView.getRight();
            yVideoView = (float) videoView.getBottom();
            Log.d("myLog","Video: " + xVideoView + ":" + yVideoView);
            float afterScaleY = (yTextView / yVideoView) * ((float) yResolution);

            if(tvTextToAdd.length()<8){

                xPos = Float.toString((float) ((xTextView / xVideoView) * ((float) xResolution) - (tvTextToAdd.getWidth()+tvTextToAdd.getWidth()/2.4)));

            }else if(tvTextToAdd.length()>7 &&tvTextToAdd.length()<12 ){

                xPos = Float.toString((float) ((xTextView / xVideoView) * ((float) xResolution) - (tvTextToAdd.getWidth()+tvTextToAdd.getWidth()/10.0)));

            }else if(tvTextToAdd.length()>11){
                xPos = Float.toString((float) ((xTextView / xVideoView) * ((float) xResolution) - (tvTextToAdd.getWidth()+tvTextToAdd.getWidth()/100.0)));

            }

            yPos = Float.toString(afterScaleY);

        }

        public boolean onDrag(View v,DragEvent event)
        {
            switch (event.getAction()) {
                case 3:
                    float X = event.getX();
                    float Y = event.getY();
                    Log.d("myLog","TextView Measure: " + tvTextToAdd.getMeasuredHeight() + ":" + tvTextToAdd.getMeasuredWidth());
                    xTextView = X - ((float) (tvTextToAdd.getWidth() / 4));
                    yTextView = Y - ((float) (tvTextToAdd.getHeight() / 4));
                    Log.d("myLog","TextView X: " + ((int) X) + " Y: " + ((int) Y) + " TEST: " + tvTextToAdd.getHeight() + ":" + tvTextToAdd.getWidth());
                    View view = (View) event.getLocalState();
                    view.setX(X - ((float) (view.getWidth() / 2)));
                    view.setY(Y - ((float) (view.getHeight() / 2)));
                    view.setVisibility(0);
                    break;
            }
            return true;
        }

        public boolean onTouch(View view,MotionEvent event)
        {
            if (event.getAction() != 0) {
                return false;
            }
            view.startDrag(null,new View.DragShadowBuilder(view),view,0);
            view.setVisibility(4);
            return true;
        }

        private void getDataFromIntent()
        {

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            try {
                metaRetriever.setDataSource(path);
                String height = metaRetriever.extractMetadata(19);
                String width = metaRetriever.extractMetadata(18);
                String rotation = metaRetriever.extractMetadata(24);
                Log.d("myLog","Resolution: " + height + " : " + width + " :Rotation: " + rotation);
                if (rotation.equals("90") || rotation.equals("270")) {
                    this.xResolution = Integer.parseInt(height);
                    this.yResolution = Integer.parseInt(width);
                    return;
                }
                this.xResolution = Integer.parseInt(width);
                this.yResolution = Integer.parseInt(height);
            } catch (Exception e2) {
            }
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
