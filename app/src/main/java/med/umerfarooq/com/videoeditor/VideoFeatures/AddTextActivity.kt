//package med.umerfarooq.com.videoeditor.VideoFeatures;
//
//import android.app.Activity;
//import android.app.AlertDialog.Builder;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.AssetManager;
//import android.graphics.Typeface;
//import android.media.MediaMetadataRetriever;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.DragEvent;
//import android.view.KeyEvent;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.DragShadowBuilder;
//import android.view.View.OnClickListener;
//import android.view.View.OnDragListener;
//import android.view.View.OnTouchListener;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.MediaController;
//import android.widget.RelativeLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.TextView.OnEditorActionListener;
//import android.widget.Toast;
//import android.widget.VideoView;
//import com.addtexttovideos.textonvideos.R;
//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
//import com.gpaddy.hungdh.texttovideo.controller.ColorSpinnerAdapter;
//import com.gpaddy.hungdh.texttovideo.controller.FontSpinnerAdapter;
//import com.gpaddy.hungdh.texttovideo.controller.utils.FileUtils;
//import com.gpaddy.hungdh.texttovideo.controller.utils.VideoControl;
//import com.gpaddy.hungdh.texttovideo.model.ItemSpinnerColor;
//import com.gpaddy.hungdh.texttovideo.model.ItemSpinnerFont;
//import com.mopub.mobileads.MoPubView;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//
//public class AddTextActivity extends AppCompatActivity implements OnTouchListener, OnDragListener {
//    private ColorSpinnerAdapter adapterColor;
//    private FontSpinnerAdapter adapterFont;
//    private Button btnAddText;
//    private ImageButton btnLess;
//    private ImageButton btnMore;
//    private CheckBox cbBorder;
//    private String color;
//    private long durationInMs;
//    private EditText etTextToAdd;
//    private FFmpeg ffmpeg;
//    private FileUtils fileUtils;
//    private String font;
//    private String fontFolder;
//    private boolean haveBorder = true;
//    private ArrayList<ItemSpinnerColor> listColor;
//    private ArrayList<ItemSpinnerFont> listFont;
//    private MoPubView moPubView;
//    private String outputFile;
//    private String pathVideo;
//    private RelativeLayout rlTop;
//    private Spinner spColor;
//    private Spinner spFont;
//    private int textSize = 50;
//    private String textToAdd;
//    private int tvSize = 10;
//    private TextView tvTextToAdd;
//    private Uri videoUri;
//    private VideoView vvAddText;
//    private String xPos = "(w-tw)/2";
//    private int xResolution;
//    private float xTextView;
//    private float xVideoView;
//    private String yPos = "(h/PHI)+th";
//    private int yResolution;
//    private float yTextView;
//    private float yVideoView;
//
//    class C04661 implements OnClickListener {
//        C04661() {
//        }
//
//        public void onClick(View v) {
//            AddTextActivity.this.addText();
//        }
//    }
//
//    class C04672 implements TextWatcher {
//        C04672() {
//        }
//
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            AddTextActivity.this.tvTextToAdd.setText(s.toString().trim());
//            if (s.toString().trim().length() > 0) {
//                AddTextActivity.this.enableAddText();
//            } else {
//                AddTextActivity.this.disableAddText();
//            }
//        }
//
//        public void afterTextChanged(Editable s) {
//        }
//    }
//
//    class C04683 implements OnEditorActionListener {
//        C04683() {
//        }
//
//        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//            if ((keyEvent != null && keyEvent.getKeyCode() == 66) || i == 6) {
//                AddTextActivity.this.hideSoftKeyboard(AddTextActivity.this, AddTextActivity.this.etTextToAdd);
//            }
//            return false;
//        }
//    }
//
//    class C04694 implements OnClickListener {
//        C04694() {
//        }
//
//        public void onClick(View view) {
//            AddTextActivity.this.textSize = AddTextActivity.this.textSize - 10;
//            if (AddTextActivity.this.textSize < 50) {
//                AddTextActivity.this.textSize = 50;
//                AddTextActivity.this.tvSize = 10;
//            } else {
//                AddTextActivity.this.tvSize = AddTextActivity.this.tvSize - 2;
//            }
//            AddTextActivity.this.tvTextToAdd.setTextSize((float) AddTextActivity.this.tvSize);
//        }
//    }
//
//    class C04705 implements OnClickListener {
//        C04705() {
//        }
//
//        public void onClick(View view) {
//            AddTextActivity.this.textSize = AddTextActivity.this.textSize + 10;
//            AddTextActivity.this.tvSize = AddTextActivity.this.tvSize + 2;
//            AddTextActivity.this.tvTextToAdd.setTextSize((float) AddTextActivity.this.tvSize);
//        }
//    }
//
//    class C04716 implements OnCheckedChangeListener {
//        C04716() {
//        }
//
//        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//            if (b) {
//                AddTextActivity.this.haveBorder = true;
//                AddTextActivity.this.tvTextToAdd.setBackgroundResource(R.color.background_text_view);
//                return;
//            }
//            AddTextActivity.this.haveBorder = false;
//            AddTextActivity.this.tvTextToAdd.setBackgroundResource(R.color.transparent);
//        }
//    }
//
//    class C04727 implements OnItemSelectedListener {
//        C04727() {
//        }
//
//        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//            AddTextActivity.this.color = ((ItemSpinnerColor) AddTextActivity.this.listColor.get(position)).getName();
//            AddTextActivity.this.tvTextToAdd.setTextColor(AddTextActivity.this.getResources().getColor(((ItemSpinnerColor) AddTextActivity.this.listColor.get(position)).getIdColor()));
//        }
//
//        public void onNothingSelected(AdapterView<?> adapterView) {
//        }
//    }
//
//    class C04738 implements OnItemSelectedListener {
//        C04738() {
//        }
//
//        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//            AddTextActivity.this.font = ((ItemSpinnerFont) AddTextActivity.this.listFont.get(position)).getPath();
//            AddTextActivity.this.tvTextToAdd.setTypeface(Typeface.createFromFile(AddTextActivity.this.font));
//        }
//
//        public void onNothingSelected(AdapterView<?> adapterView) {
//        }
//    }
//
//    class C09659 extends ExecuteBinaryResponseHandler {
//        private ProgressDialog progressDialog = new ProgressDialog(AddTextActivity.this);
//
//        C09659() {
//        }
//
//        public void onFailure(String s) {
//            Toast.makeText(AddTextActivity.this.getApplicationContext(), "Add text failure", 1).show();
//            FileUtils.deleteFile(AddTextActivity.this.outputFile);
//        }
//
//        public void onSuccess(String s) {
//            Toast.makeText(AddTextActivity.this.getApplicationContext(), "Add text success", 1).show();
//            AddTextActivity.this.onSuccessAddText();
//        }
//
//        public void onProgress(String s) {
//            if (s.contains("time=")) {
//                this.progressDialog.setMessage("Processing " + ((int) ((((double) VideoControl.progressDurationInMs(s.substring(s.lastIndexOf("time=") + 5, s.lastIndexOf("time=") + 16))) / ((double) AddTextActivity.this.durationInMs)) * 100.0d)) + "%");
//            }
//        }
//
//        public void onStart() {
//            this.progressDialog.setCancelable(false);
//            this.progressDialog.setMessage("Processing...");
//            this.progressDialog.show();
//        }
//
//        public void onFinish() {
//            this.progressDialog.dismiss();
//        }
//    }
//
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView((int) R.layout.activity_add_text);
//        copyFontFromAsset();
//        initActionBar();
//        getDataFromIntent();
//        init();
//        this.ffmpeg = FFmpeg.getInstance(this);
//        loadFFMpegBinary();
//        initBanner();
//    }
//
//    private void copyFontFromAsset() {
//        this.fileUtils = FileUtils.getInstance(this);
//        this.fontFolder = this.fileUtils.getAppDirPath() + "fonts";
//        File fontsF = new File(this.fontFolder);
//        if (fontsF.exists() || fontsF.mkdir()) {
//            if (!new File(this.fontFolder + File.separator + "Roboto-Light.ttf").exists()) {
//                copyAssets();
//            }
//            initFont();
//        }
//    }
//
//    private void copyAssets() {
//        AssetManager assetManager = getAssets();
//        String[] files = null;
//        try {
//            files = assetManager.list("");
//        } catch (IOException e) {
//            IOException e2;
//            Log.e("tag", "Failed to get asset file list.", e2);
//        }
//        if (files != null) {
//            for (String filename : files) {
//                try {
//                    InputStream in = assetManager.open(filename);
//                    OutputStream out = new FileOutputStream(this.fontFolder + File.separator + filename);
//                    try {
//                        copyFile(in, out);
//                        in.close();
//                        out.flush();
//                        out.close();
//                    } catch (IOException e3) {
//                        e2 = e3;
//                        OutputStream outputStream = out;
//                        Log.e("tag", "Failed to copy asset file: " + filename, e2);
//                    }
//                } catch (IOException e4) {
//                    e2 = e4;
//                    Log.e("tag", "Failed to copy asset file: " + filename, e2);
//                }
//            }
//        }
//    }
//
//    private void copyFile(InputStream in, OutputStream out) throws IOException {
//        byte[] buffer = new byte[1024];
//        while (true) {
//            int read = in.read(buffer);
//            if (read != -1) {
//                out.write(buffer, 0, read);
//            } else {
//                return;
//            }
//        }
//    }
//
//    private void initActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(getResources().getString(R.string.add_text));
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case 16908332:
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void getDataFromIntent() {
//        this.videoUri = Uri.parse(getIntent().getStringExtra(MainActivity.KEY_PUT_VIDEO_PATH));
//        try {
//            this.pathVideo = FileUtils.getFilePath(this, this.videoUri);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        this.durationInMs = VideoControl.getDuration(this, this.videoUri);
//        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
//        try {
//            metaRetriever.setDataSource(this.pathVideo);
//            String height = metaRetriever.extractMetadata(19);
//            String width = metaRetriever.extractMetadata(18);
//            String rotation = metaRetriever.extractMetadata(24);
//            Log.d("myLog", "Resolution: " + height + " : " + width + " :Rotation: " + rotation);
//            if (rotation.equals("90") || rotation.equals("270")) {
//                this.xResolution = Integer.parseInt(height);
//                this.yResolution = Integer.parseInt(width);
//                return;
//            }
//            this.xResolution = Integer.parseInt(width);
//            this.yResolution = Integer.parseInt(height);
//        } catch (Exception e2) {
//        }
//    }
//
//    private void init() {
//        this.vvAddText = (VideoView) findViewById(R.id.vv_add_text);
//        MediaController videoMediaController = new MediaController(this);
//        this.vvAddText.setVideoPath(this.pathVideo);
//        videoMediaController.setMediaPlayer(this.vvAddText);
//        this.vvAddText.setMediaController(videoMediaController);
//        this.vvAddText.requestFocus();
//        this.vvAddText.start();
//        this.btnAddText = (Button) findViewById(R.id.btn_add_text);
//        disableAddText();
//        this.btnAddText.setOnClickListener(new C04661());
//        this.etTextToAdd = (EditText) findViewById(R.id.et_add_text);
//        this.etTextToAdd.addTextChangedListener(new C04672());
//        this.etTextToAdd.setOnEditorActionListener(new C04683());
//        this.tvTextToAdd = (TextView) findViewById(R.id.tv_add_text);
//        this.tvTextToAdd.setTextSize((float) this.tvSize);
//        this.rlTop = (RelativeLayout) findViewById(R.id.root);
//        this.rlTop.setOnDragListener(this);
//        this.tvTextToAdd.setOnTouchListener(this);
//        initColor();
//        this.btnLess = (ImageButton) findViewById(R.id.btn_less);
//        this.btnLess.setOnClickListener(new C04694());
//        this.btnMore = (ImageButton) findViewById(R.id.btn_more);
//        this.btnMore.setOnClickListener(new C04705());
//        this.cbBorder = (CheckBox) findViewById(R.id.cbBorder);
//        this.cbBorder.setOnCheckedChangeListener(new C04716());
//    }
//
//    private void hideSoftKeyboard(Activity activity, View view) {
//        ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
//    }
//
//    private void initBanner() {
//        this.moPubView = (MoPubView) findViewById(R.id.adview);
//        this.moPubView.setAdUnitId(getResources().getString(R.string.ad_banner_id));
//        this.moPubView.loadAd();
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        this.moPubView.destroy();
//    }
//
//    public boolean onDrag(View v, DragEvent event) {
//        switch (event.getAction()) {
//            case 3:
//                float X = event.getX();
//                float Y = event.getY();
//                Log.d("myLog", "TextView Measure: " + this.tvTextToAdd.getMeasuredHeight() + ":" + this.tvTextToAdd.getMeasuredWidth());
//                this.xTextView = X - ((float) (this.tvTextToAdd.getWidth() / 2));
//                this.yTextView = Y - ((float) (this.tvTextToAdd.getHeight() / 2));
//                Log.d("myLog", "TextView X: " + ((int) X) + " Y: " + ((int) Y) + " TEST: " + this.tvTextToAdd.getHeight() + ":" + this.tvTextToAdd.getWidth());
//                View view = (View) event.getLocalState();
//                view.setX(X - ((float) (view.getWidth() / 2)));
//                view.setY(Y - ((float) (view.getHeight() / 2)));
//                view.setVisibility(0);
//                break;
//        }
//        return true;
//    }
//
//    public boolean onTouch(View view, MotionEvent event) {
//        if (event.getAction() != 0) {
//            return false;
//        }
//        view.startDrag(null, new DragShadowBuilder(view), view, 0);
//        view.setVisibility(4);
//        return true;
//    }
//
//    private void initColor() {
//        this.spColor = (Spinner) findViewById(R.id.sp_color);
//        this.listColor = new ArrayList();
//        this.listColor.add(new ItemSpinnerColor("white", R.color.white));
//        this.listColor.add(new ItemSpinnerColor("black", R.color.black));
//        this.listColor.add(new ItemSpinnerColor("red", R.color.red));
//        this.listColor.add(new ItemSpinnerColor("green", R.color.green));
//        this.listColor.add(new ItemSpinnerColor("blue", R.color.blue));
//        this.listColor.add(new ItemSpinnerColor("yellow", R.color.yellow));
//        this.listColor.add(new ItemSpinnerColor("orange", R.color.orange));
//        this.adapterColor = new ColorSpinnerAdapter(this, this.listColor);
//        this.spColor.setAdapter(this.adapterColor);
//        this.color = ((ItemSpinnerColor) this.listColor.get(0)).getName();
//        this.spColor.setOnItemSelectedListener(new C04727());
//    }
//
//    private void initFont() {
//        this.spFont = (Spinner) findViewById(R.id.sp_font);
//        this.listFont = new ArrayList();
//        for (File file : new File(this.fontFolder).listFiles()) {
//            if (FileUtils.getFileType(file.getName()).toLowerCase().equals("ttf")) {
//                this.listFont.add(new ItemSpinnerFont(file.getName(), file.getPath()));
//            }
//        }
//        this.adapterFont = new FontSpinnerAdapter(this, this.listFont);
//        this.spFont.setAdapter(this.adapterFont);
//        this.font = ((ItemSpinnerFont) this.listFont.get(0)).getPath();
//        this.spFont.setOnItemSelectedListener(new C04738());
//    }
//
//    private void addText() {
//        prepareScale();
//        try {
//            this.outputFile = this.fileUtils.createTempFile("VID", ".mp4").getPath();
//            this.textToAdd = this.etTextToAdd.getText().toString().trim();
//            if (this.haveBorder) {
//                execFFmpegBinary(new String[]{"-y", "-i", this.pathVideo, "-vf", "drawtext=fontfile=" + this.font + ":text='" + this.textToAdd + "'" + ":fontcolor=" + this.color + ":fontsize=" + this.textSize + ":x=" + this.xPos + ":y=" + this.yPos + ":box=1" + ":boxcolor=black@0.2" + ":boxborderw=5", "-y", this.outputFile});
//                return;
//            }
//            execFFmpegBinary(new String[]{"-y", "-i", this.pathVideo, "-vf", "drawtext=fontfile=" + this.font + ":text='" + this.textToAdd + "'" + ":fontcolor=" + this.color + ":fontsize=" + this.textSize + ":x=" + this.xPos + ":y=" + this.yPos, "-y", this.outputFile});
//        } catch (IOException e) {
//            e.printStackTrace();A
//        }
//    }
//
//    private void prepareScale() {
//        this.xVideoView = (float) this.vvAddText.getRight();
//        this.yVideoView = (float) this.vvAddText.getBottom();
//        Log.d("myLog", "Video: " + this.xVideoView + ":" + this.yVideoView);
//        float afterScaleY = (this.yTextView / this.yVideoView) * ((float) this.yResolution);
//        this.xPos = Float.toString((this.xTextView / this.xVideoView) * ((float) this.xResolution));
//        this.yPos = Float.toString(afterScaleY);
//    }
//
//    private void onSuccessAddText() {
//        FileUtils.scanMedia(this, this.outputFile);
//        Intent intent = new Intent(this, VideoResultActivity.class);
//        intent.putExtra(MainActivity.KEY_RESULT_VIDEO_PATH, this.outputFile);
//        startActivity(intent);
//    }
//
//    private void execFFmpegBinary(String[] command) {
//        try {
//            this.ffmpeg.execute(command, new C09659());
//        } catch (FFmpegCommandAlreadyRunningException e) {
//        }
//    }
//
//    private void loadFFMpegBinary() {
//        try {
//            this.ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                public void onFailure() {
//                    AddTextActivity.this.showUnsupportedExceptionDialog();
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            showUnsupportedExceptionDialog();
//        }
//    }
//
//    private void showUnsupportedExceptionDialog() {
//        new Builder(this).setIcon(17301543).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                AddTextActivity.this.finish();
//            }
//        }).create().show();
//    }
//
//    private void enableAddText() {
//        this.btnAddText.setBackgroundResource(R.drawable.bg_orange);
//        this.btnAddText.setEnabled(true);
//    }
//
//    private void disableAddText() {
//        this.btnAddText.setBackgroundResource(R.drawable.bg_gray);
//        this.btnAddText.setEnabled(false);
//    }
//}
