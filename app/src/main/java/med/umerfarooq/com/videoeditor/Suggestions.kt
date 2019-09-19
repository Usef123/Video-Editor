package med.umerfarooq.com.videoeditor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

import java.util.ArrayList

import de.hdodenhof.circleimageview.CircleImageView
import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.Other.others
import med.umerfarooq.com.videoeditor.VideoFeatures.Listhome.CustomAdapter
import med.umerfarooq.com.videoeditor.VideoFeatures.Listhome.DataModel
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class Suggestions : AppCompatActivity() {
    internal var url: String? = null
    internal var title: String? = null
    internal var description: String? = null
    internal var button: Button? = null
    internal var textone: TextView? = null
    internal var texttwo: TextView? = null
    internal var dataModels: ArrayList<DataModel>? = null
    internal var listView: ListView?=null
    private val mAdView: AdView? = null
    private val btnFullscreenAd: Button? = null
    private val btnShowRewardedVideoAd: Button? = null
    private val mCircleImageView: CircleImageView? = null
    private var tinydb: TinyDB? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-Medium.ttf").setFontAttrId(R.attr.fontPath).build())


        val adContainer = findViewById<View>(R.id.adView)
        val mAdView = AdView(this@Suggestions)
        mAdView.adSize = AdSize.SMART_BANNER
        (adContainer as FrameLayout).addView(mAdView)
        mAdView.adUnitId = others.BANNER

        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708").build()

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {}

            override fun onAdClosed() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {

            }

            override fun onAdLeftApplication() {
                //
            }

            override fun onAdOpened() {
                super.onAdOpened()
            }
        }

        mAdView.loadAd(adRequest)


        tinydb = TinyDB(this@Suggestions)
        val url = tinydb!!.getListString("icon_url")
        val title = tinydb!!.getListString("title")
        val description = tinydb!!.getListString("description")
        val target_url = tinydb!!.getListString("target_url")
        listView = findViewById<View>(R.id.list) as ListView

        dataModels = ArrayList()
        for (i in title.indices) {
            run {

                dataModels?.add(DataModel(url[i], title[i], description[i]))

            }

        }


        adapter = CustomAdapter(dataModels, applicationContext)

        listView?.adapter = adapter
        listView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(target_url[position])
            startActivity(i)
        }

        val app = findViewById<RelativeLayout>(R.id.app)
        app.setOnClickListener { startActivity(Intent(this@Suggestions, LoadingScreen::class.java)) }
    }

    companion object {
        private var adapter: CustomAdapter? = null
    }


}
