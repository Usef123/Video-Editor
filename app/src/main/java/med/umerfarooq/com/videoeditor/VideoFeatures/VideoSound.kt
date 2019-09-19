package med.umerfarooq.com.videoeditor.VideoFeatures

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.MediaController
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
import java.io.InputStream
import java.util.ArrayList

import javax.inject.Inject

import butterknife.ButterKnife
import butterknife.InjectView
import dagger.ObjectGraph
import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.FFmpeg.DaggerDependencyModuleSound
import med.umerfarooq.com.videoeditor.MainScreen
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.R
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.CustomAdapterStickers
import med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers.DataModelStickers
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class VideoSound : AppCompatActivity(), OnClickListener {
    val AUDIO_REQUEST = 101
    @Inject
    internal var ffmpeg: FFmpeg? = null
    internal var VideoSound: TextView? = null
    //        @InjectView(R.id.command)
    internal var seconds = "0"
    internal lateinit var skip: Button
    internal var extractbol = false
    internal lateinit var perviouPath: String
    internal var Pervious: Boolean? = true
    internal lateinit var videoMediaController: MediaController
    internal var command: Array<String>? = null
    internal lateinit var switch1: Switch
    @InjectView(R.id.run_command)
    internal var runButton: Button? = null
    @InjectView(R.id.extract)
    internal var extract: Button? = null
    internal lateinit var tinydb: TinyDB
    internal lateinit var path: String
    internal lateinit var size: EditText
    internal var merge: Boolean? = false
    internal var dataModels: ArrayList<DataModelStickers>? = null
    internal var listView: ListView? = null
    internal var getse: EditText? = null
    internal lateinit var getsec: EditText
    internal lateinit var videoView: VideoView
    internal lateinit var imguri: Uri

    internal lateinit var mInterstitialAd: InterstitialAd
    internal var bm: Bitmap? = null
    private val soundarray = arrayOf("Rockstar", "Akcent", "Dance Floor", "Dark Thril", "Despicto", "Good Morning", "Let me love you")

    private val soundlist = intArrayOf(R.raw.rockstar, R.raw.akcent, R.raw.dancefloor, R.raw.darkthril, R.raw.despicto, R.raw.goodmorning, R.raw.letmeloveyou)
    private var progressDialog: ProgressDialog? = null
    private val mAdView: AdView? = null
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_video_sound)

        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@VideoSound)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER
        getsec = findViewById(R.id.seconds)
        seconds = getsec.text.toString()

        videoView = findViewById(R.id.VideoVieww)
        tinydb = TinyDB(this)
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
        path = tinydb.getString("path")
        perviouPath = path
        Log.d("path", path)
        //            Toast.makeText(this,""+path,Toast.LENGTH_SHORT).show();
        videoMediaController = MediaController(this)
        videoView.setVideoPath(path)
        videoMediaController.setMediaPlayer(videoView)
        videoView.setMediaController(videoMediaController)
        videoView.requestFocus()
        videoView.start()


        switch1 = findViewById(R.id.switch1)
        size = findViewById(R.id.size)
        ButterKnife.inject(this)
        ObjectGraph.create(DaggerDependencyModuleSound(this)).inject(this)

        loadFFMpegBinary()
        initUI()

        getsec.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == 66 || actionId == 6) {
                hideSoftKeyboard(this@VideoSound, getsec)
            }
            false
        }
        val adapter = ArrayAdapter(this, R.layout.fiter_listview, soundarray)
        val listView = findViewById<View>(R.id.list) as ListView
        listView.adapter = adapter
        imguri = Uri.fromFile(copyFiletoExternalStorage(soundlist[0], "song.mp3"))
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(this@VideoSound, "Sound Selected", Toast.LENGTH_SHORT).show()
            imguri = Uri.fromFile(copyFiletoExternalStorage(soundlist[position], "song.mp3"))
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
        myToolbar.title = "Add Sound"
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        myToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        skip = findViewById(R.id.button)
        skip.setOnClickListener { startActivity(Intent(this@VideoSound, VideoThemes::class.java)) }
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

    @SuppressLint("WrongConstant")
    private fun hideSoftKeyboard(activity: Activity, view: View) {
        (activity.getSystemService("input_method") as InputMethodManager).hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    override fun onPause() {
        super.onPause()
        videoView.stopPlayback()
    }

    private fun getAudioPath(uri: Uri): String {
        val data = arrayOf(MediaStore.Audio.Media.DATA)
        val loader = CursorLoader(applicationContext, uri, data, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.sound_menu, menu)
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
                val delfile = File(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sound.mp4")
                if (delfile.exists()) {

                    delfile.delete()

                }

                return true
            }
            R.id.chooseaudio -> {
                videoView.stopPlayback()
                val videoIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), AUDIO_REQUEST)
                return true
            }
            R.id.cancel -> {

                val builder: AlertDialog.Builder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = AlertDialog.Builder(this@VideoSound, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    builder = AlertDialog.Builder(this@VideoSound)
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you wanna discard all changes and cancel?")
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            deleteDir(File(Environment.getExternalStorageDirectory().toString() + "/", "Videotemp"))
                            startActivity(Intent(this@VideoSound, MainScreen::class.java))
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which -> dialog.cancel() }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUDIO_REQUEST && null != data) {
            if (requestCode == AUDIO_REQUEST) {


                videoView.setVideoPath(path)
                //                    tinydb.putString("path",Environment.getExternalStorageDirectory().getPath() +  "/Videotemp/" + "sound.mp4");
                videoView.start()

                val uri = data.data
                try {
                    val uriString = uri!!.toString()
                    val myFile = File(uriString)
                    //    String path = myFile.getAbsolutePath();
                    val displayName: String? = null
                    val path2 = getAudioPath(uri)
                    val f = File(path2)
                    val fileSizeInBytes = f.length()
                    val fileSizeInKB = fileSizeInBytes / 1024
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB > 8) {
                        //                            customAlterDialog("Can't Upload ", "sorry file size is large");
                    } else {
                        imguri = Uri.parse(path2)

                    }
                } catch (e: Exception) {
                    //handle exception
                    //                        Toast.makeText(GroupDetailsActivity.this, "Unable to process,try again", Toast.LENGTH_SHORT).show();
                }

                //   String path1 = uri.getPath();

            }
        }

    }

    private fun initUI() {
        runButton!!.setOnClickListener(this)
        extract!!.setOnClickListener(this)
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


                    Log.d(TAG, "Started command : ffmpeg $command")
                    progressDialog!!.setMessage("Processing...")
                    progressDialog!!.show()
                }

                override fun onFinish() {
                    skip.text = "Next"

                    if (extractbol) {

                        runButton!!.text = "Merge"
                        merge = true
                    }
                    videoView.setVideoPath(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sound.mp4")
                    tinydb.putString("path", Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sound.mp4")
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
        val textView = TextView(this@VideoSound)
        textView.text = text

    }

    private fun showUnsupportedExceptionDialog() {
        AlertDialog.Builder(this@VideoSound).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.device_not_supported)).setMessage(getString(R.string.device_not_supported_message)).setCancelable(false).setPositiveButton(android.R.string.ok) { dialog, which ->
            //                            VideoSound.finish();
        }.create().show()

    }

    override fun onClick(v: View) {
        val command: Array<String>
        when (v.id) {
            R.id.run_command -> {
                //
                videoView.stopPlayback()

                command = arrayOf("-y", "-i", path, "-itsoffset", "00:" + getsec.text.toString(), "-i", imguri.toString(), "-map", "0:0", "-map", "1:0", "-c:v", "copy", "-shortest", "-async", "1", Environment.getExternalStorageDirectory().path + "/Videotemp/" + "sound.mp4")

                //
                if (command.size != 0) {
                    execFFmpegBinary(command)
                } else {
                    Toast.makeText(this@VideoSound, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show()
                }
            }
            R.id.extract -> {

                extractbol = true
                Toast.makeText(this, "" + seconds, Toast.LENGTH_SHORT).show()
                videoView.stopPlayback()
                //
                command = arrayOf("-y", "-i", path, "-i", imguri.toString(), "-q:a", "0", "-map", "a", Environment.getExternalStorageDirectory().path + "/" + "Extract.mp3")

                if (command.size != 0) {
                    execFFmpegBinary(command)
                } else {
                    Toast.makeText(this@VideoSound, getString(R.string.empty_command_toast), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun copyFiletoExternalStorage(resourceId: Int, resourceName: String): File {
        val pathSDCard = Environment.getExternalStorageDirectory().toString() + "/Android/data/"
        val file = File(pathSDCard, resourceName)
        try {
            val responseInputStream = resources.openRawResource(resourceId)
            var out: FileOutputStream? = null
            out = FileOutputStream(file)
            val buff = ByteArray(1024)
            var read = 0
            try {
                while ((true)){
                val len = responseInputStream.read(buff);

                if (len == -1) break;
                    out.write(buff, 0, read)
                }
            } finally {
                responseInputStream.close()
                out.close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    private fun showInterstitial() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    companion object {
        private val TAG = VideoSound::class.java.simpleName
        private val adapter: CustomAdapterStickers? = null
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


