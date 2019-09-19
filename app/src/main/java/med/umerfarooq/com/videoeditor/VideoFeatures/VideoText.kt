package med.umerfarooq.com.videoeditor.VideoFeatures

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.DragEvent
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnDragListener
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

import java.io.File

import javax.inject.Inject

import butterknife.ButterKnife
import butterknife.InjectView
import dagger.ObjectGraph
import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.FFmpeg.DaggerDependencyModule
import med.umerfarooq.com.videoeditor.MainScreen
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class VideoText : AppCompatActivity(), OnClickListener, OnDragListener, OnTouchListener {

    @Inject
    internal var ffmpeg: FFmpeg? = null
    internal var videotext: TextView? = null
    @InjectView(R.id.command)
    internal var commandEditText: EditText? = null
    internal var command: Array<String>? = null
    internal lateinit var switch1: Switch
    @InjectView(R.id.run_command)
    internal var runButton: Button? = null
    internal lateinit var tinydb: TinyDB
    internal lateinit var path: String
    internal lateinit var perviouPath: String
    internal var Pervious: Boolean? = true
    internal lateinit var videoMediaController: MediaController
    internal lateinit var skip: Button
    internal lateinit var videoView: VideoView
    internal lateinit var tvTextToAdd: TextView
    internal var size = 50
    internal lateinit var positive: Button
    internal lateinit var neg: Button
    internal lateinit var mInterstitialAd: InterstitialAd
    private val durationInMs: Long = 0
    private var tvSize = 10
    private var rlTop: RelativeLayout? = null
    private var progressDialog: ProgressDialog? = null
    private val mAdView: AdView? = null
    private var xPos = "(w-tw)/2"
    private var xResolution: Int = 0
    private var xTextView: Float = 0.toFloat()
    private var xVideoView: Float = 0.toFloat()
    private var yPos = "(h/PHI)+th"
    private var yResolution: Int = 0
    private var yTextView: Float = 0.toFloat()
    private var yVideoView: Float = 0.toFloat()
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_video_text)
        tvTextToAdd = findViewById(R.id.tvTextToAdd)
        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@VideoText)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER
        positive = findViewById(R.id.plus)
        neg = findViewById(R.id.neg)
        mInterstitialAd = InterstitialAd(this)

        // set the ad unit ID
        mInterstitialAd.adUnitId = others.intersitial

        var adRequest = AdRequest.Builder().build()

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest)
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                //                   showInterstitial();
            }

            override fun onAdClosed() {

            }


        }
        tvTextToAdd.setOnTouchListener(this)
        tinydb = TinyDB(this)
        path = tinydb.getString("path")

        perviouPath = path
        getDataFromIntent()
        videoView = findViewById<View>(R.id.VideoVieww) as VideoView
        this.rlTop = findViewById<View>(R.id.root) as RelativeLayout
        this.rlTop!!.setOnDragListener(this)
        //            videotext=findViewById(R.id.videotext);
        videoMediaController = MediaController(this)
        videoView.setVideoPath(path)
        videoMediaController.setMediaPlayer(videoView)
        videoView.setMediaController(videoMediaController)
        videoView.requestFocus()
        videoView.start()
        switch1 = findViewById(R.id.switch1)

        ButterKnife.inject(this)
        ObjectGraph.create(DaggerDependencyModule(this)).inject(this)

        loadFFMpegBinary()
        initUI()
        positive.setOnClickListener {
            size = size + 13
            tvSize = tvSize + 2
            tvTextToAdd.textSize = tvSize.toFloat()
        }
        neg.setOnClickListener {
            size = size - 13
            if (size < 50) {
                size = 50
                tvSize = 10
            } else {
                tvSize = tvSize - 2
            }
            tvTextToAdd.textSize = tvSize.toFloat()
        }
        commandEditText!!.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == 66 || actionId == 6) {
                hideSoftKeyboard(this@VideoText, commandEditText!!)
            }
            false
        }
        commandEditText!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tvTextToAdd.visibility = View.VISIBLE
                tvTextToAdd.text = s.toString().trim { it <= ' ' }
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {

                // TODO Auto-generated method stub
            }
        })
        adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build()

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {}

            override fun onAdClosed() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {
                //                    Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            override fun onAdLeftApplication() {
                //                    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }
        }

        mAdView.loadAd(adRequest)

        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        myToolbar.title = "Add Text"
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        myToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        skip = findViewById(R.id.button)
        skip.setOnClickListener {
            val i = Intent(this@VideoText, VideoStickers::class.java)


            startActivity(i)
        }
    }

    @SuppressLint("WrongConstant")
    private fun hideSoftKeyboard(activity: Activity, view: View) {
        (activity.getSystemService("input_method") as InputMethodManager).hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {

            finish()

        }
        when (item.itemId) {
            R.id.clear -> {
                videoView.setVideoPath(perviouPath)
                videoMediaController.setMediaPlayer(videoView)
                videoView.setMediaController(videoMediaController)
                videoView.requestFocus()
                videoView.start()

                tinydb.putString("path", perviouPath)
                val delfile = File(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "textVideo.mp4")
                if (delfile.exists()) {

                    delfile.delete()

                }

                return true
            }

            R.id.cancel -> {

                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(this@VideoText, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    builder = AlertDialog.Builder(this@VideoText)
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you wanna discard all changes and cancel?")
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            deleteDir(File(Environment.getExternalStorageDirectory().toString() + "/", "Videotemp"))
                            startActivity(Intent(this@VideoText, MainScreen::class.java))
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which -> dialog.cancel() }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun initUI() {
        runButton!!.setOnClickListener(this)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setTitle(null)
    }

    private fun loadFFMpegBinary() {
        try {
            ffmpeg!!.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onFailure() {
                    showUnsupportedExceptionDialog()
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            showUnsupportedExceptionDialog()
        }

    }

    private fun execFFmpegBinary(command: Array<String>) {
        try {
            ffmpeg!!.execute(command, object : ExecuteBinaryResponseHandler() {
                override fun onFailure(s: String?) {
                    addTextViewToLayout("FAILED with output : " + s!!)
                }

                override fun onSuccess(s: String?) {
                    addTextViewToLayout("SUCCESS with output : " + s!!)
                }

                override fun onProgress(s: String?) {
                    Log.d(TAG, "Started command : ffmpeg $command")
                    addTextViewToLayout("progress : " + s!!)
                    progressDialog!!.setMessage("Processing\n$s")
                }

                override fun onStart() {

                    tvTextToAdd.visibility = View.GONE
                    Log.d(TAG, "Started command : ffmpeg $command")
                    progressDialog!!.setMessage("Processing...")
                    progressDialog!!.show()
                }

                override fun onFinish() {
                    videoView.setVideoPath(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "textVideo.mp4")
                    tvTextToAdd.visibility = View.GONE
                    videoView.start()
                    tinydb.putString("path", Environment.getExternalStorageDirectory().path + "/Videotemp/" + "textVideo.mp4")
                    skip.text = "Next"
                    Log.d(TAG, "Finished command : ffmpeg $command")
                    progressDialog!!.dismiss()
                }
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            // do nothing for now
        }

    }

    private fun addTextViewToLayout(text: String) {
        val textView = TextView(this@VideoText)
        textView.text = text

    }

    private fun showUnsupportedExceptionDialog() {
        AlertDialog.Builder(this@VideoText).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(android.R.string.ok) { dialog, which ->
            //                            VideoText.finish();
        }.create().show()

    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.run_command -> {
                prepareScale()
                videoView.stopPlayback()
                val cmd = commandEditText!!.text.toString()

                val command = arrayOf("-y", "-i", path, "-vf", "drawtext=fontfile=" + Environment.getExternalStorageDirectory().path + "/" + "Roboto-Medium.ttf" + ":text='" + cmd + "'" + ":fontcolor=white" + ":fontsize=" + this.size + ":x=" + xPos + ":y=" + yPos, "-y", "-c:v", "libx264", "-preset", "ultrafast", Environment.getExternalStorageDirectory().path + "/Videotemp/" + "textVideo.mp4")



                if (command.size != 0) {
                    execFFmpegBinary(command)
                } else {
                    Toast.makeText(this@VideoText, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoView.stopPlayback()
    }


    override fun onResume() {
        super.onResume()

        if (!videoView.isPlaying) {

            videoMediaController = MediaController(this)
            videoView.setVideoPath(path)
            videoMediaController.setMediaPlayer(videoView)
            videoView.setMediaController(videoMediaController)
            videoView.requestFocus()
            videoView.start()

        }
    }


    private fun prepareScale() {

        //            xPos = Float.toString((float) ((xTextView / xVideoView) * ((float) xResolution) - (tvTextToAdd.getWidth()+tvTextToAdd.getWidth()/2.4)));
        xVideoView = videoView.right.toFloat()
        yVideoView = videoView.bottom.toFloat()
        Log.d("myLog", "Video: $xVideoView:$yVideoView")
        val afterScaleY = yTextView / yVideoView * yResolution.toFloat()

        if (tvTextToAdd.length() < 8) {

            xPos = java.lang.Float.toString((xTextView / xVideoView * xResolution.toFloat() - (tvTextToAdd.width + tvTextToAdd.width / 2.4)).toFloat())

        } else if (tvTextToAdd.length() > 7 && tvTextToAdd.length() < 12) {

            xPos = java.lang.Float.toString((xTextView / xVideoView * xResolution.toFloat() - (tvTextToAdd.width + tvTextToAdd.width / 10.0)).toFloat())

        } else if (tvTextToAdd.length() > 11) {
            xPos = java.lang.Float.toString((xTextView / xVideoView * xResolution.toFloat() - (tvTextToAdd.width + tvTextToAdd.width / 100.0)).toFloat())

        }

        yPos = java.lang.Float.toString(afterScaleY)

    }

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            3 -> {
                val X = event.x
                val Y = event.y
                Log.d("myLog", "TextView Measure: " + tvTextToAdd.measuredHeight + ":" + tvTextToAdd.measuredWidth)
                xTextView = X - (tvTextToAdd.width / 4).toFloat()
                yTextView = Y - (tvTextToAdd.height / 4).toFloat()
                Log.d("myLog", "TextView X: " + X.toInt() + " Y: " + Y.toInt() + " TEST: " + tvTextToAdd.height + ":" + tvTextToAdd.width)
                val view = event.localState as View
                view.x = X - (view.width / 2).toFloat()
                view.y = Y - (view.height / 2).toFloat()
                view.visibility = 0
            }
        }
        return true
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action != 0) {
            return false
        }
        view.startDrag(null, View.DragShadowBuilder(view), view, 0)
        view.visibility = 4
        return true
    }

    private fun getDataFromIntent() {

        val metaRetriever = MediaMetadataRetriever()
        try {
            metaRetriever.setDataSource(path)
            val height = metaRetriever.extractMetadata(19)
            val width = metaRetriever.extractMetadata(18)
            val rotation = metaRetriever.extractMetadata(24)
            Log.d("myLog", "Resolution: $height : $width :Rotation: $rotation")
            if (rotation == "90" || rotation == "270") {
                this.xResolution = Integer.parseInt(height)
                this.yResolution = Integer.parseInt(width)
                return
            }
            this.xResolution = Integer.parseInt(width)
            this.yResolution = Integer.parseInt(height)
        } catch (e2: Exception) {
        }

    }

    private fun showInterstitial() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    companion object {

        private val TAG = VideoText::class.java.simpleName
        fun deleteDir(dir: File): Boolean {
            if (dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }

            return dir.delete()
        }
    }
}
