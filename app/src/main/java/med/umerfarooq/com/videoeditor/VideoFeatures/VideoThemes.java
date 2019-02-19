package med.umerfarooq.com.videoeditor.VideoFeatures;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import med.umerfarooq.com.videoeditor.Database.TinyDB;
import med.umerfarooq.com.videoeditor.FFmpeg.DaggerDependencyModuleThmes;
import med.umerfarooq.com.videoeditor.MainScreen;
import med.umerfarooq.com.videoeditor.Other.others;
import med.umerfarooq.com.videoeditor.R;
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.CustomAdapterStickers;
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.DataModelStickers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VideoThemes
        extends AppCompatActivity
        implements OnClickListener, View.OnDragListener, View.OnTouchListener
    {


        private static final String TAG = VideoThemes.class.getSimpleName();
        private static CustomAdapterStickers adapter;
        String perviouPath;
        Boolean Pervious = true;
        MediaController videoMediaController;
        InterstitialAd mInterstitialAd;
        ImageView tvimgToAdd;
        @Inject
        FFmpeg ffmpeg;
        TextView VideoThemes;
        Button skip;
        String[] command;
        Switch switch1;
        @InjectView(R.id.run_command)
        Button runButton;
        TinyDB tinydb;
        String path;
        EditText size;
        ArrayList<DataModelStickers> dataModels;
        ListView listView;
//        @InjectView(R.id.command)
        VideoView videoView;
        Bitmap bm;
        private ArrayList<Integer> stickersarray = new ArrayList<>();
        private ArrayList<Integer> stickersarraygrid = new ArrayList<>();
        private String xPos = "(w-tw)/2";
        private int xResolution;
        private float xTextView;
        private float xVideoView;
        private String yPos = "(h/PHI)+th";
        private int yResolution;
        private float yTextView;
        private float yVideoView;
        private ProgressDialog progressDialog;
        private AdView mAdView;
        private RelativeLayout rlTop;
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
            setContentView(R.layout.activity_video_stickers);


            stickersarray.add(R.drawable.birthday);
            stickersarraygrid.add(R.drawable.birthdayparty);
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
            View adContainer = findViewById(R.id.adView);
            AdView mAdView = new AdView(VideoThemes.this);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            ((FrameLayout) adContainer).addView(mAdView);
            mAdView.setAdUnitId(others.BANNER);
            tvimgToAdd = findViewById(R.id.tvimgToAdd);
            tvimgToAdd.setOnTouchListener(this);
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
            size = findViewById(R.id.size);
            ButterKnife.inject(this);
            ObjectGraph.create(new DaggerDependencyModuleThmes(this)).inject(this);

            loadFFMpegBinary();
            initUI();

            listView = (ListView) findViewById(R.id.list);

            dataModels = new ArrayList<>();

            bm = BitmapFactory.decodeResource(getResources(),R.drawable.boom);
            adapter = new CustomAdapterStickers(stickersarray,getApplicationContext());



            GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(stickersarraygrid,this));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    public void onItemClick(AdapterView<?> parent,View v,int position,long id)
                    {
                        tvimgToAdd.setVisibility(View.GONE);
                        Toast.makeText(VideoThemes.this,"Selected",Toast.LENGTH_SHORT).show();
                        bm = BitmapFactory.decodeResource(getResources(),stickersarray.get(position));

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
            myToolbar.setTitle("Add Themes");
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


                        String sound = tinydb.getString("sound");

//

                        startActivity(new Intent(VideoThemes.this,ShareVideo.class));


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
                    File delfile = new File(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sticker.mp4");
                    if (delfile.exists()) {

                        delfile.delete();

                    }

                    return true;
                case R.id.cancel:

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(VideoThemes.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(VideoThemes.this);
                    }
                    builder.setTitle("Delete entry")
                            .setMessage("Are you sure you wanna discard all changes and cancel?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDir(new File(Environment.getExternalStorageDirectory() + "/","Videotemp"));
                                    startActivity(new Intent(VideoThemes.this,MainScreen.class));
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
                            tvimgToAdd.setVisibility(View.GONE);

                            Log.d(TAG,"Started command : ffmpeg " + command);
                            progressDialog.setMessage("Processing...");
                            progressDialog.show();
                        }

                        @Override
                        public void onFinish()
                        {
                            skip.setText("Next");
                            tvimgToAdd.setVisibility(View.GONE);
                            videoView.setVideoPath(Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sticker.mp4");
                            tinydb.putString("path",Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sticker.mp4");
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
            TextView textView = new TextView(VideoThemes.this);
            textView.setText(text);

        }

        private void showUnsupportedExceptionDialog()
        {
            new AlertDialog.Builder(VideoThemes.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
//                            VideoThemes.finish();
                    }
                }).create().show();

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

        @Override
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.run_command:
                    prepareScale();
                    videoView.stopPlayback();

                    File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + "image.png");
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG,100,outStream);
                        outStream.flush();
                        outStream.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Uri imguri = Uri.fromFile(file);

                    String[] command = new String[]{"-y","-i",path,"-i",String.valueOf(imguri),"-filter_complex","overlay=" + -50 + ":" + -80,"-c:v","libx264","-preset","ultrafast",Environment.getExternalStorageDirectory().getPath() + "/Videotemp/" + "sticker.mp4"};


                    if (command.length != 0) {
                        execFFmpegBinary(command);
                    } else {
                        Toast.makeText(VideoThemes.this,getString(R.string.empty_command_toast),Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

        private void prepareScale()
        {
            xVideoView = (float) videoView.getRight();
            yVideoView = (float) videoView.getBottom();
            Log.d("myLog","Video: " + xVideoView + ":" + yVideoView);
            float afterScaleY = (yTextView / yVideoView) * ((float) yResolution);
            xPos = Float.toString((xTextView / xVideoView) * ((float) xResolution) - tvimgToAdd.getWidth() * 2);
            yPos = Float.toString(afterScaleY - tvimgToAdd.getHeight());
//            Toast.makeText(this,"x:    "+tvimgToAdd.getWidth()+"     y:"+tvimgToAdd.getHeight(),Toast.LENGTH_SHORT).show();
        }

        public boolean onDrag(View v,DragEvent event)
        {
            switch (event.getAction()) {
                case 3:
                    float X = event.getX();
                    float Y = event.getY();
                    Log.d("myLog","TextView Measure: " + tvimgToAdd.getMeasuredHeight() + ":" + tvimgToAdd.getMeasuredWidth());
                    xTextView = X - ((float) (tvimgToAdd.getWidth() / 4));
                    yTextView = Y - ((float) (tvimgToAdd.getHeight() / 4));
                    Log.d("myLog","TextView X: " + ((int) X) + " Y: " + ((int) Y) + " TEST: " + tvimgToAdd.getHeight() + ":" + tvimgToAdd.getWidth());
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
//
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

        public class ImageAdapter
                extends BaseAdapter
            {

                Context mContext;
                private ArrayList<Integer> dataSet;

                public ImageAdapter(ArrayList<Integer> data,Context c)
                {
                    mContext = c;
                    dataSet = data;
                }

                public int getCount()
                {
                    return dataSet.size();
                }

                public Object getItem(int position)
                {
                    return null;
                }

                public long getItemId(int position)
                {
                    return 0;
                }

                // create a new ImageView for each item referenced by the Adapter
                public View getView(int position,View convertView,ViewGroup parent)
                {
                    ImageView imageView;
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View rowView = inflater.inflate(R.layout.listviewstickers,parent,false);

                    imageView = (ImageView) rowView.findViewById(R.id.item_info);
                    imageView.setImageResource(dataSet.get(position));


                    return rowView;
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


