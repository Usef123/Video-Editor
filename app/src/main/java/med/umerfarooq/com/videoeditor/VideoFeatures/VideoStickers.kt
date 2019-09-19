package med.umerfarooq.com.videoeditor.VideoFeatures

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.ListView
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
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

import javax.inject.Inject

import butterknife.ButterKnife
import butterknife.InjectView
import dagger.ObjectGraph
import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.FFmpeg.DaggerDependencyModuleStickers
import med.umerfarooq.com.videoeditor.MainScreen
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.R
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.CustomAdapterStickers
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.DataModelStickers
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class VideoStickers : AppCompatActivity(), OnClickListener, View.OnDragListener, View.OnTouchListener {
    internal lateinit var perviouPath: String
    internal var Pervious: Boolean? = true
    internal lateinit var videoMediaController: MediaController

    internal lateinit var mInterstitialAd: InterstitialAd
    internal lateinit var tvimgToAdd: ImageView
    @Inject
    internal var ffmpeg: FFmpeg? = null
    internal var VideoStickers: TextView? = null
    internal lateinit var skip: Button
    internal var command: Array<String>? = null
    internal lateinit var switch1: Switch
    @InjectView(R.id.run_command)
    internal var runButton: Button? = null
    internal lateinit var tinydb: TinyDB
    internal lateinit var path: String
    internal lateinit var size: EditText
    internal lateinit var dataModels: ArrayList<DataModelStickers>
    //        @InjectView(R.id.command)
    internal lateinit var listView: ListView

    internal lateinit var videoView: VideoView
    internal lateinit var bm: Bitmap
    private val stickersarray = ArrayList<Int>()
    private var xPos = "(w-tw)/2"
    private var xResolution: Int = 0
    private var xTextView: Float = 0.toFloat()
    private var xVideoView: Float = 0.toFloat()
    private var yPos = "(h/PHI)+th"
    private var yResolution: Int = 0
    private var yTextView: Float = 0.toFloat()
    private var yVideoView: Float = 0.toFloat()
    private var progressDialog: ProgressDialog? = null
    private val mAdView: AdView? = null
    private var rlTop: RelativeLayout? = null
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_video_stickers)


        stickersarray.add(R.drawable.bang)
        stickersarray.add(R.drawable.boom)
        stickersarray.add(R.drawable.face)
        stickersarray.add(R.drawable.mario)
        stickersarray.add(R.drawable.skull)
        stickersarray.add(R.drawable.sold)
        stickersarray.add(R.drawable.yo)
        mInterstitialAd = InterstitialAd(this)

        // set the ad unit ID
        mInterstitialAd.adUnitId = others.intersitial

        var adRequest = AdRequest.Builder().build()

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest)
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                //                    showInterstitial();
            }

            override fun onAdClosed() {

            }
        }
        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@VideoStickers)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER
        tvimgToAdd = findViewById(R.id.tvimgToAdd)
        tvimgToAdd.setOnTouchListener(this)
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
        size = findViewById(R.id.size)
        ButterKnife.inject(this)
        ObjectGraph.create(DaggerDependencyModuleStickers(this)).inject(this)

        loadFFMpegBinary()
        initUI()

        listView = findViewById<View>(R.id.list) as ListView

        dataModels = ArrayList()
        //
        //
        bm = BitmapFactory.decodeResource(resources, R.drawable.boom)
        adapter = CustomAdapterStickers(stickersarray, applicationContext)


        val gridview = findViewById<View>(R.id.gridview) as GridView
        gridview.adapter = ImageAdapter(stickersarray, this)

        gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            tvimgToAdd.visibility = View.VISIBLE
            Toast.makeText(this@VideoStickers, "Selected", Toast.LENGTH_SHORT).show()
            bm = BitmapFactory.decodeResource(resources, stickersarray[position])
        }
        adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build()

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {}

            override fun onAdClosed() {
                //
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                //
            }

            override fun onAdLeftApplication() {
                //
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }
        }

        mAdView.loadAd(adRequest)

        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        myToolbar.title = "Add Stickers"
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        myToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        skip = findViewById(R.id.button)
        skip.setOnClickListener {
            val sound = tinydb.getString("sound")
            if (sound.equals("enable", ignoreCase = true)) {

                startActivity(Intent(this@VideoStickers, VideoSound::class.java))
            } else {
                startActivity(Intent(this@VideoStickers, VideoThemes::class.java))

            }
        }
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
                val delfile = File(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sticker.mp4")
                if (delfile.exists()) {

                    delfile.delete()

                }
                return true
            }
            R.id.cancel -> {

                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(this@VideoStickers, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    builder = AlertDialog.Builder(this@VideoStickers)
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you wanna discard all changes and cancel?")
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            deleteDir(File(Environment.getExternalStorageDirectory().toString() + "/", "Videotemp"))
                            startActivity(Intent(this@VideoStickers, MainScreen::class.java))
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
                    tvimgToAdd.visibility = View.GONE

                    Log.d(TAG, "Started command : ffmpeg $command")
                    progressDialog!!.setMessage("Processing...")
                    progressDialog!!.show()
                }

                override fun onFinish() {
                    skip.text = "Next"
                    tvimgToAdd.visibility = View.GONE
                    videoView.setVideoPath(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sticker.mp4")
                    tinydb.putString("path", Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sticker.mp4")
                    videoView.start()

                    Log.d(TAG, "Finished command : ffmpeg $command")
                    progressDialog!!.dismiss()
                }
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            // do nothing for now
        }

    }

    private fun addTextViewToLayout(text: String) {
        val textView = TextView(this@VideoStickers)
        textView.text = text

    }

    private fun showUnsupportedExceptionDialog() {
        AlertDialog.Builder(this@VideoStickers).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(android.R.string.ok) { dialog, which ->
            //                            VideoStickers.finish();
        }.create().show()

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

    override fun onPause() {
        super.onPause()
        videoView.stopPlayback()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.run_command -> {
                prepareScale()

                val file = File(Environment.getExternalStorageDirectory().toString() + "/Android/data/" + "image.png")
                var outStream: FileOutputStream? = null
                try {
                    outStream = FileOutputStream(file)
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                    outStream.flush()
                    outStream.close()

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val imguri = Uri.fromFile(file)

                val command = arrayOf("-y", "-i", path, "-i", imguri.toString(), "-filter_complex", "overlay=$xPos:$yPos", "-c:v", "libx264", "-preset", "ultrafast", Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sticker.mp4")


                if (command.size != 0) {
                    execFFmpegBinary(command)
                } else {
                    Toast.makeText(this@VideoStickers, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun prepareScale() {
        xVideoView = videoView.right.toFloat()
        yVideoView = videoView.bottom.toFloat()
        Log.d("myLog", "Video: $xVideoView:$yVideoView")
        val afterScaleY = yTextView / yVideoView * yResolution.toFloat()
        xPos = java.lang.Float.toString(xTextView / xVideoView * xResolution.toFloat() - tvimgToAdd.width * 2)
        yPos = java.lang.Float.toString(afterScaleY - tvimgToAdd.height)
        //            Toast.makeText(this,"x:    "+tvimgToAdd.getWidth()+"     y:"+tvimgToAdd.getHeight(),Toast.LENGTH_SHORT).show();
    }

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            3 -> {
                val X = event.x
                val Y = event.y
                Log.d("myLog", "TextView Measure: " + tvimgToAdd.measuredHeight + ":" + tvimgToAdd.measuredWidth)
                xTextView = X - (tvimgToAdd.width / 4).toFloat()
                yTextView = Y - (tvimgToAdd.height / 4).toFloat()
                Log.d("myLog", "TextView X: " + X.toInt() + " Y: " + Y.toInt() + " TEST: " + tvimgToAdd.height + ":" + tvimgToAdd.width)
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

    inner class ImageAdapter(private val dataSet: ArrayList<Int>, internal var mContext: Context) : BaseAdapter() {

        override fun getCount(): Int {
            return dataSet.size
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        // create a new ImageView for each item referenced by the Adapter
        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val imageView: ImageView
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView = inflater.inflate(R.layout.listviewstickers, parent, false)

            imageView = rowView.findViewById<View>(R.id.item_info) as ImageView
            imageView.setImageResource(dataSet[position])


            return rowView
        }

        // references to our images

    }

    companion object {


        private val TAG = VideoStickers::class.java.simpleName
        private var adapter: CustomAdapterStickers? = null
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


