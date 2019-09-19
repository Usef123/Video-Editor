package med.umerfarooq.com.videoeditor.VideoFeatures

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Random

import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.MainScreen
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class ShareVideo : AppCompatActivity() {
    private var tinydb: TinyDB?=null
    private var share: ImageButton?=null
    private var save: ImageButton?=null
    private var path: String?=null
    private var min = 100
    private var max = 500
    private var mInterstitialAd: InterstitialAd?=null
    private var finish: Button?=null
    private val TAG = ShareVideo::class.java.simpleName

    val galleryPath: String
        get() {
            val folder = Environment.getExternalStoragePublicDirectory("VideoEditorApp")

            if (!folder.exists()) {
                folder.mkdir()
            }

            return folder.absolutePath
        }

    val appDirPath: String
        get() = if (localPath != null) {
            "$localPath$APP_DIR/"
        } else null as String

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_share_video)
        tinydb = TinyDB(this)
        mInterstitialAd = InterstitialAd(this)

        // set the ad unit ID
        mInterstitialAd?.adUnitId = others.intersitial

        val adRequest = AdRequest.Builder().build()

        // Load ads into Interstitial Ads
        mInterstitialAd?.loadAd(adRequest)
        mInterstitialAd?.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                Toast.makeText(this@ShareVideo, "Video saved Please check your Gallery..Finalvideo$random.mp4", Toast.LENGTH_LONG).show()

            }

            override fun onAdClosed() {
                Toast.makeText(this@ShareVideo, "Video saved Please check your Gallery..Finalvideo$random.mp4", Toast.LENGTH_LONG).show()

            }
        }
        finish = findViewById(R.id.finish)
        finish?.setOnClickListener {
            val i = Intent(this@ShareVideo, MainScreen::class.java)
            startActivity(i)
        }
        val r = Random()
        random = r.nextInt(max - min + 1) + min
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        myToolbar.title = "Share and Save Video "
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        myToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(myToolbar)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        share = findViewById(R.id.share)
        save = findViewById(R.id.save)
        path = tinydb?.getString("path")
        file = File(path)
        share?.setOnClickListener { shareVideo("Video", tinydb!!.getString("path")) }
        showInterstitial()
        save?.setOnClickListener { TestAsync().execute() }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        if (id == android.R.id.home) {
            finish()

        }
        return super.onOptionsItemSelected(item)
    }

    fun shareVideo(title: String, path: String) {

        MediaScannerConnection.scanFile(this@ShareVideo, arrayOf(path), null
        ) { path, uri ->
            val shareIntent = Intent(android.content.Intent.ACTION_SEND)
            shareIntent.type = "video/*"
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title)
            shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, title)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            startActivity(Intent.createChooser(shareIntent, "Share Video"))
        }
    }

    private fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded) {
            mInterstitialAd?.show()
        }
    }

    @Throws(IOException::class)
    fun createTempFile(prefix: String, extension: String): File {
        val file = File(appDirPath + "/" + prefix + System.currentTimeMillis() + extension)
        file.createNewFile()
        return file
    }

    private inner class TestAsync : AsyncTask<Void, Int, String>() {
        var TAG = javaClass.simpleName

        override fun onPreExecute() {
            super.onPreExecute()
            if (isNetworkAvailable) {

                showInterstitial()
            } else {
                Toast.makeText(this@ShareVideo, "Please wait...", Toast.LENGTH_SHORT).show()
            }

        }

        override fun doInBackground(vararg arg0: Void): String? {
            Log.d("$TAG DoINBackGround", "On doInBackground...")

            try {
                copyDirectoryOneLocationToAnotherLocation(file, File(Environment.getExternalStorageDirectory().toString() + "/" + "Finalvideo" + random + ".mp4"))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            MediaScannerConnection.scanFile(this@ShareVideo, arrayOf(Environment.getExternalStorageDirectory().toString() + "/" + "Finalvideo" + random + ".mp4"), null) { path, uri ->

                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }

            try {
                copyDirectoryOneLocationToAnotherLocation(file, File("$galleryPath/Finalvideo$random.mp4"))
                //                            exportMp4ToGallery(ShareVideo.this,path)
                createTempFile("Finalvideo$random", ".mp4")

            } catch (e: IOException) {
                e.printStackTrace()
            }

            deleteDir(File(Environment.getExternalStorageDirectory().toString() + "/", "Videotemp"))
            val dir = File(Environment.getExternalStorageDirectory(), "/Videotemp")
            try {
                if (dir.mkdir()) {
                    println("Directory created")
                } else {
                    println("Directory is not created")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }


        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (isNetworkAvailable) {

            } else {
                Toast.makeText(this@ShareVideo, "Video saved Please check your Gallery..Finalvideo$random.mp4", Toast.LENGTH_LONG).show()

            }

        }
    }

    companion object {
        private val APP_DIR = "VideoAPPEditor"
        var random: Int = 0
        var file: File?=null

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

        @Throws(IOException::class)
        fun copyDirectoryOneLocationToAnotherLocation(sourceLocation: File?,
                                                      targetLocation: File
        ) {

            if (sourceLocation!!.isDirectory) {
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
                var len: Int?=null
                while (true){
                    val len= responseInputStream.read(buf);

                    if(len==-1) break;

                    out.write(buf, 0, len!!)
                }
                responseInputStream.close()
                out.close()
            }
            //            copyDirectoryOneLocationToAnotherLocation(file,new File(Environment.getExternalStorageDirectory() + "/","Finalvideo" + random + ".mp4"));

        }

        fun exportMp4ToGallery(context: Context, filePath: String) {
            // ビデオのメタデータを作成する
            val values = ContentValues(2)
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATA, filePath)
            // MediaStoreに登録
            context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$filePath")))
        }

        private val localPath: String
            get() = Environment.getExternalStorageDirectory().toString() + "/"
    }
}
