package med.umerfarooq.com.videoeditor.VideoFeatures

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView

import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.composer.Mp4Composer
import com.daasuu.mp4compose.filter.GlBoxBlurFilter
import com.daasuu.mp4compose.filter.GlBulgeDistortionFilter
import com.daasuu.mp4compose.filter.GlFilter
import com.daasuu.mp4compose.filter.GlGrayScaleFilter
import com.daasuu.mp4compose.filter.GlHazeFilter
import com.daasuu.mp4compose.filter.GlLutFilter
import com.daasuu.mp4compose.filter.GlMonochromeFilter
import com.daasuu.mp4compose.filter.GlVignetteFilter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date

import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.MainScreen
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class VideoFilters : AppCompatActivity() {
    internal lateinit var perviouPath: String
    internal var Pervious: Boolean? = true
    internal lateinit var videoMediaController: MediaController
    internal lateinit var videoPath: String
    internal lateinit var path: String
    internal lateinit var tinydb: TinyDB
    internal lateinit var skip: Button
    internal lateinit var mInterstitialAd: InterstitialAd
    internal lateinit var videoView: VideoView
    internal var filterArray = arrayOf("Simple Filter", "GlayScaleFilter", "MonoChromeFilter", "BoxBlurFilter", "LutFilter", "BulgeDistortionFilter", "VignetteFilter", "HazeFilter")
    private val mAdView: AdView? = null
    private var mp4Composer: Mp4Composer? = null
    private var bitmap: Bitmap? = null
    private var muteCheckBox: CheckBox? = null
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null

    val androidMoviesFolder: File
        get() = Environment.getExternalStorageDirectory()

    val videoFilePath: String
        @SuppressLint("SimpleDateFormat")
        get() = androidMoviesFolder.absolutePath + "/Videotemp/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + "filter_apply.mp4"

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_video_filters)
        MainScreen.TestAsync().execute()
        tinydb = TinyDB(this)
        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@VideoFilters)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER

        mInterstitialAd = InterstitialAd(this)

        // set the ad unit ID
        mInterstitialAd.adUnitId = others.intersitial

        var adRequest = AdRequest.Builder().build()

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest)
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }
        }
        tinydb.putString("filter", filterArray[0])

        val adapter = ArrayAdapter(this, R.layout.fiter_listview, filterArray)

        val listView = findViewById<View>(R.id.video_list) as ListView
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(this@VideoFilters, "Filter Selected", Toast.LENGTH_SHORT).show()
            tinydb.putString("filter", filterArray[position])
        }

        videoPath = videoFilePath
        videoView = findViewById<View>(R.id.VideoView) as VideoView

        path = tinydb.getString("path")
        perviouPath = path
        videoMediaController = MediaController(this)
        videoView.setVideoPath(path)
        videoMediaController.setMediaPlayer(videoView)
        videoView.setMediaController(videoMediaController)
        videoView.requestFocus()
        videoView.start()


        muteCheckBox = findViewById(R.id.mute_check_box)

        findViewById<View>(R.id.adda).setOnClickListener { v ->
            //                v.setEnabled(false);
            videoView.stopPlayback()
            startCodec()
        }

        findViewById<View>(R.id.cancel_button).setOnClickListener { v ->
            if (mp4Composer != null) {
                mp4Composer!!.cancel()
            }
        }

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.lookup_sample)


        adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build()


        mAdView.loadAd(adRequest)

        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        myToolbar.title = "Add Filters"
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        myToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(myToolbar)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        skip = findViewById(R.id.button)
        skip.setOnClickListener {
            val i = Intent(this@VideoFilters, VideoText::class.java)


            startActivity(i)
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
                val delfile = File(videoPath)
                if (delfile.exists()) {

                    delfile.delete()

                }

                return true
            }
            R.id.cancel -> {

                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(this@VideoFilters, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    builder = AlertDialog.Builder(this@VideoFilters)
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you wanna discard all changes and cancel?")
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            deleteDir(File(Environment.getExternalStorageDirectory().toString() + "/", "Videotemp"))
                            startActivity(Intent(this@VideoFilters, MainScreen::class.java))
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which -> dialog.cancel() }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }

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

    private fun startCodec() {


        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.max = 100

        val mute = muteCheckBox!!.isChecked

        mp4Composer = null
        val mp4Composer = Mp4Composer(path, videoPath)
        // .rotation(Rotation.ROTATION_270)
        //.size(720, 1280)
        mp4Composer.fillMode(FillMode.PRESERVE_ASPECT_FIT)
        //            mp4Composer.filter(new GlMonochromeFilter());
        val filtermatch = tinydb.getString("filter")


        if (filtermatch == filterArray[0]) {

            mp4Composer.filter(GlFilter())

        }

        if (filtermatch == filterArray[1]) {

            mp4Composer.filter(GlGrayScaleFilter())

        }

        if (filtermatch == filterArray[2]) {

            mp4Composer.filter(GlMonochromeFilter())

        }

        if (filtermatch == filterArray[3]) {

            mp4Composer.filter(GlBoxBlurFilter())

        }

        if (filtermatch == filterArray[4]) {

            mp4Composer.filter(GlLutFilter(bitmap))

        }

        if (filtermatch == filterArray[5]) {

            mp4Composer.filter(GlBulgeDistortionFilter())

        }

        if (filtermatch == filterArray[6]) {

            mp4Composer.filter(GlVignetteFilter())

        }

        if (filtermatch == filterArray[7]) {

            mp4Composer.filter(GlHazeFilter())

        }


        mp4Composer.mute(mute)
        mp4Composer.listener(object : Mp4Composer.Listener {
            override fun onProgress(progress: Double) {
                Log.d(TAG, "onProgress = $progress")
                runOnUiThread { progressBar.progress = (progress * 100).toInt() }
            }

            override fun onCompleted() {
                //                            skip.setText("Next");
                runOnUiThread { skip.text = "Next" }
                Log.d(TAG, "onCompleted()")
                exportMp4ToGallery(applicationContext, videoPath)
                tinydb.putString("path", videoPath)
                runOnUiThread {
                    progressBar.progress = 100
                    findViewById<View>(R.id.adda).isEnabled = true
                    //                                Toast.makeText(VideoFilters.this, "codec complete path =" + videoPath, Toast.LENGTH_SHORT).show();
                    videoView.setVideoPath(videoPath)

                    videoView.start()
                }
            }

            override fun onCanceled() {

            }

            override fun onFailed(exception: Exception) {
                Log.d(TAG, "onFailed()")
            }
        })
        mp4Composer.start()


    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        // request permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@VideoFilters, "permission has been grunted.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@VideoFilters, "[WARN] permission is not grunted.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showInterstitial() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    companion object {
        val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
        private val TAG = "SAMPLE"
        private val PERMISSION_REQUEST_CODE = 88888

        /**
         * ギャラリーにエクスポート
         *
         * @param filePath
         * @return The video MediaStore URI
         */
        fun exportMp4ToGallery(context: Context, filePath: String) {
            // ビデオのメタデータを作成する
            val values = ContentValues(1)
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATA, filePath)
            // MediaStoreに登録
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$filePath")))
        }

        @Throws(IOException::class)
        fun copyDirectoryOneLocationToAnotherLocation(sourceLocation: File,
                                                      targetLocation: File
        ) {

            if (sourceLocation.isDirectory) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdir()
                }

                val children = sourceLocation.list()
                for (i in 0 until sourceLocation.listFiles().size) {

                    copyDirectoryOneLocationToAnotherLocation(File(sourceLocation, children[i]), File(targetLocation, children[i]))
                }
            } else {

                val responseInputStream = FileInputStream(sourceLocation)

                val out = FileOutputStream(targetLocation)

                // Copy the bits from instream to outstream
                val buf = ByteArray(1024)
                var len: Int? = null
                while (true){
                val len = responseInputStream.read(buf);

                if (len == -1) break;
                    out.write(buf, 0, len)
                }
                responseInputStream.close()
                out.close()
            }


        }

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
