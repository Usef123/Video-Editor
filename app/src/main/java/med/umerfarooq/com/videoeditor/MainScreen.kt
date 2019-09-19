package med.umerfarooq.com.videoeditor

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

import java.io.File

import life.knowledge4.videotrimmer.utils.FileUtils
import med.umerfarooq.com.videoeditor.Database.TinyDB

import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.VideoFeatures.VideoTrimmer
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainScreen : AppCompatActivity() {
     var tinyDB: TinyDB? = null
    private val mAdView: AdView? = null
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
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

        setContentView(R.layout.activity_main_screen)
        TestAsync().execute()
        mInterstitialAd = InterstitialAd(this@MainScreen)
        mInterstitialAd?.adUnitId = others.intersitial
        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@MainScreen)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER
        val rate = findViewById<ImageView>(R.id.rate)
        rate.setOnClickListener {
            //                        Toast.makeText(MainScreen.this,"Rate",Toast.LENGTH_SHORT).show();
            val uri = Uri.parse("market://details?id=$packageName")
            val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
            try {
                startActivity(myAppLinkToMarket)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this@MainScreen, " unable to find market app", Toast.LENGTH_LONG).show()
            }
        }
        val share = findViewById<ImageView>(R.id.share)
        share.setOnClickListener {
            //                        Toast.makeText(MainScreen.this,"Share",Toast.LENGTH_SHORT).show();
            try {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var sAux = "\nLet me recommend you this application\n\n"
                sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n"
                i.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(i, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build()
        tinyDB = TinyDB(this)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {}

            override fun onAdClosed() {
                //                    Toast.makeText(getApplicationContext(),"Ad is closed!",Toast.LENGTH_SHORT).show();
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

        //            Toolbar myToolbar = findViewById(R.id.my_toolbar);
        //            myToolbar.setTitle("Home Screen");
        //            myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        //            myToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        //            setSupportActionBar(myToolbar);
        //            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        val galleryButton = findViewById<ImageButton>(R.id.fromstorage)
        galleryButton?.setOnClickListener { pickFromGallery() }
        val recordButton = findViewById<ImageButton>(R.id.record)
        recordButton?.setOnClickListener { openVideoCapture() }
    }

    private fun openVideoCapture() {
        val videoCapture = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER)
    }

    private fun pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION)
        } else {
            val intent = Intent()
            intent.setTypeAndNormalize("video/*")
            intent.action = Intent.ACTION_GET_CONTENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_TRIMMER)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        if (id == android.R.id.home) {
            finish()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@MainScreen, Suggestions::class.java))
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                val selectedUri = data?.data
                if (selectedUri != null) {
                    val tinyDB1 = tinyDB?.putString("path", FileUtils.getPath(this, selectedUri))
                    startTrimActivity(selectedUri)
                } else {
                    Toast.makeText(this@MainScreen, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startTrimActivity(uri: Uri) {

        tinyDB?.putString("path", FileUtils.getPath(this, uri))
        val intent = Intent(this, VideoTrimmer::class.java)
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri))
        startActivity(intent)
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private fun requestPermission(permission: String,
                                  rationale: String,
                                  requestCode: Int
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.permission_title_rationale))
            builder.setMessage(rationale)
            builder.setPositiveButton(getString(R.string.label_ok)) { dialog, which -> ActivityCompat.requestPermissions(this@MainScreen, arrayOf(permission), requestCode) }
            builder.setNegativeButton(getString(R.string.label_cancel), null)
            builder.show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_READ_ACCESS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFromGallery()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // UPDATED!
    fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } else
            return null
    }

    class TestAsync : AsyncTask<Void, Int, String>() {
        internal var TAG = javaClass.simpleName

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("$TAG PreExceute", "On pre Exceute......")
        }

        override fun doInBackground(vararg arg0: Void): String {
            Log.d("$TAG DoINBackGround", "On doInBackground...")


            // set the ad unit ID

            adRequest = AdRequest.Builder().build()



            return "You are at PostExecute"
        }

        override fun onPostExecute(result: String) {
            mInterstitialAd?.loadAd(adRequest)
            super.onPostExecute(result)
            Log.d("$TAG onPostExecute", "" + result)
        }
    }

    companion object {
        val EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH"
        private val REQUEST_VIDEO_TRIMMER = 0x01
        private val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
        var adRequest: AdRequest? = null
        var mInterstitialAd: InterstitialAd? = null
    }
}
