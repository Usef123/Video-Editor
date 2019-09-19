package med.umerfarooq.com.videoeditor

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import med.umerfarooq.com.videoeditor.MainScreen.Companion.mInterstitialAd

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import med.umerfarooq.com.videoeditor.Other.others
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class LoadingScreen : AppCompatActivity() {
    internal var SPLASH_TIME_OUT = 8000
    internal var i = 0
    internal var addload: Boolean? = false
    private val TAG = LoadingScreen::class.java.simpleName

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return cm.activeNetworkInfo != null
        }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_loading_screen)

        TestAsync().execute()
        mInterstitialAd = InterstitialAd(this)
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

        // set the ad unit ID
        mInterstitialAd?.adUnitId = others.intersitial

        val adRequest = AdRequest.Builder().build()

        // Load ads into Interstitial Ads
        mInterstitialAd?.loadAd(adRequest)

        mInterstitialAd?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                showInterstitial()
            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)

                startActivity(Intent(this@LoadingScreen, MainScreen::class.java))
            }

            override fun onAdClosed() {
                addload = true
                startActivity(Intent(this@LoadingScreen, MainScreen::class.java))
            }
        }

        loading()

        //

    }

    private fun loading() {
        //


        Handler().postDelayed({
            if ((addload)!!) {
                startActivity(Intent(this@LoadingScreen, MainScreen::class.java))
            }

            //                          showInterstitial();


            // close this activity
        }, SPLASH_TIME_OUT.toLong())

    }

    private fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded) {
            mInterstitialAd?.show()
        }
    }

    private fun copyAssets() {
        val assetManager = assets
        var files: Array<String>? = null
        try {
            files = assetManager.list("")
        } catch (e: IOException) {
            Log.e("tag", "Failed to get asset file list.", e)
        }

        for (filename in files!!) {
            var `in`: InputStream? = null
            var out: OutputStream? = null
            try {
                `in` = assetManager.open("Roboto-Medium.ttf")

                val outDir = Environment.getExternalStorageDirectory().absolutePath

                val outFile = File(outDir, "Roboto-Medium.ttf")

                out = FileOutputStream(outFile)
                copyFile(`in`!!, out)
                `in`.close()
                `in` = null
                out.flush()
                out.close()
                out = null
            } catch (e: IOException) {
                Log.e("tag", "Failed to copy asset file: $filename", e)
            }

            //                Toast.makeText(this,"done",Toast.LENGTH_SHORT).show();
        }
    }

    @Throws(IOException::class)
    private fun copyFile(responseInputStream: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while(true){
            val read= responseInputStream.read(buffer);

            if(read==-1) break;
            out.write(buffer, 0, read)
        }
    }

    internal inner class TestAsync : AsyncTask<Void, Int, String>() {
        var TAG = javaClass.simpleName

        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("$TAG PreExceute", "On pre Exceute......")
        }

        override fun doInBackground(vararg arg0: Void): String {
            Log.d("$TAG DoINBackGround", "On doInBackground...")
            copyAssets()

            return "You are at PostExecute"
        }



        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d("$TAG onPostExecute", "" + result)
        }
    }

    companion object {

        var mInterstitialAd: InterstitialAd? = null
    }
}