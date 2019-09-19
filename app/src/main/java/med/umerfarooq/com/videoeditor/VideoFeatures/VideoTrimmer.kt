package med.umerfarooq.com.videoeditor.VideoFeatures

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

import life.knowledge4.videotrimmer.K4LVideoTrimmer
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener
import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.MainScreen
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class VideoTrimmer : AppCompatActivity(), OnTrimVideoListener, OnK4LVideoListener {
    internal var mInterstitialAd: InterstitialAd? = null
    internal lateinit var tinyDB: TinyDB
    private val TAG = VideoTrimmer::class.java.simpleName
    private val mAdView: AdView? = null
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null
    private var mVideoTrimmer: K4LVideoTrimmer? = null
    private var mProgressDialog: ProgressDialog? = null

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
        setContentView(R.layout.activity_video_trimmer)
        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@VideoTrimmer)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER
        val extraIntent = intent
        var path = ""

        tinyDB = TinyDB(this)

        if (extraIntent != null) {
            path = extraIntent.getStringExtra(MainScreen.EXTRA_VIDEO_PATH)

            tinyDB.putString("path", path)
        }

        //setting progressbar
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setMessage(getString(R.string.trimming_progress))
        //            mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        //            mInterstitialAd.setAdUnitId(others.intersitial);
        var adRequest = AdRequest.Builder().build()


        //            mInterstitialAd.loadAd(adRequest);
        MainScreen.mInterstitialAd?.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                if (isNetworkAvailable) {
                    startActivity(Intent(this@VideoTrimmer, VideoFilters::class.java))
                }
            }

            override fun onAdClosed() {

                startActivity(Intent(this@VideoTrimmer, VideoFilters::class.java))
            }
        }

        mVideoTrimmer = findViewById<View>(R.id.timeLine) as K4LVideoTrimmer
        if (mVideoTrimmer != null) {
            mVideoTrimmer!!.setMaxDuration(10)
            mVideoTrimmer!!.setDestinationPath(Environment.getExternalStorageDirectory().path + "/Videotemp/" + "trimVideo.mp4")
            mVideoTrimmer!!.setOnTrimVideoListener(this@VideoTrimmer)
            mVideoTrimmer!!.setOnK4LVideoListener(this@VideoTrimmer)

            mVideoTrimmer!!.setVideoURI(Uri.parse(path))
            mVideoTrimmer!!.setVideoInformationVisibility(true)
        }
        adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)

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
        myToolbar.title = "Cut Video"
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        myToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)
        setSupportActionBar(myToolbar)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            if (isNetworkAvailable) {
                showInterstitial()
            } else {
                startActivity(Intent(this@VideoTrimmer, VideoFilters::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId


        if (id == android.R.id.home) {
            finish()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val v = MainScreen()
        MainScreen.TestAsync().execute()
    }

    override fun onTrimStarted() {
        mProgressDialog!!.show()
    }

    override fun getResult(uri: Uri) {
        mProgressDialog!!.cancel()
        tinyDB.putString("path", uri.path)
        runOnUiThread {
            Toast.makeText(this@VideoTrimmer, getString(R.string.video_saved_at, uri.path), Toast.LENGTH_SHORT).show()
            if (isNetworkAvailable) {
                showInterstitial()
            } else {
                startActivity(Intent(this@VideoTrimmer, VideoFilters::class.java))
            }
        }

        finish()
    }

    override fun cancelAction() {
        mProgressDialog!!.cancel()
        mVideoTrimmer!!.destroy()
        finish()
    }

    override fun onError(message: String) {
        mProgressDialog!!.cancel()

        runOnUiThread { Toast.makeText(this@VideoTrimmer, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onVideoPrepared() {
        runOnUiThread {
            //
        }
    }

    private fun showInterstitial() {
        if (MainScreen.mInterstitialAd!!.isLoaded) {
            MainScreen.mInterstitialAd?.show()
        } else {
            startActivity(Intent(this@VideoTrimmer, VideoFilters::class.java))
        }
    }
}
