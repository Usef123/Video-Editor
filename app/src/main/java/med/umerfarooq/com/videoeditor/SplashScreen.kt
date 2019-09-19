package med.umerfarooq.com.videoeditor


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import med.umerfarooq.com.videoeditor.Database.TinyDB
import med.umerfarooq.com.videoeditor.Json.HttpHandler
import med.umerfarooq.com.videoeditor.Json.JSONParser
import org.json.JSONException
import org.json.JSONObject
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*

class SplashScreen : AppCompatActivity() {
    private var Title: String? = null
    private var Description: String? = null
    private var iconUrl: String? = null
    private var jParser = JSONParser()
    private var review: String? = null
    private var tinydb: TinyDB? = null
    private var title: String? = null
    private var description: String = ""
    private var icon_url: String? = null
    private var target_url: String? = null
    private var sound: String? = null
    private var titlel: ArrayList<String>? = null
    private var descriptionl: ArrayList<String>? = null
    private var icon_urll: ArrayList<String>? = null
    private var target_urll: ArrayList<String>? = null
    private var jsonStr: String? = null

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
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf").setFontAttrId(R.attr.fontPath).build())
        setContentView(R.layout.activity_splash_screen)


        if (isNetworkAvailable) {
            LoadImageData().execute()
        } else {
            val SPLASH_TIME_OUT = 1000
            Handler().postDelayed(/*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                    {
                        startActivity(Intent(this@SplashScreen, Suggestions::class.java))


                        // close this activity
                    }, SPLASH_TIME_OUT.toLong())


        }
        tinydb = TinyDB(this@SplashScreen)


    }

    private inner class LoadImageData : AsyncTask<String, String, String>() {


        override fun onPreExecute() {
            super.onPreExecute()

            //
            titlel = ArrayList<String>()
            descriptionl = ArrayList<String>()
            icon_urll = ArrayList<String>()
            target_urll = ArrayList<String>()

        }

        override fun doInBackground(vararg args: String): String? {


            val sh = HttpHandler()
            // Making a request to url and getting response
            val url = "https://api.myjson.com/bins/pysrr"

            jsonStr = sh.makeServiceCall(url)


            if (jsonStr != null) {
                try {
                    val jsonObj = JSONObject(jsonStr)
                  val  sound = jsonObj.getString("sounds_name")

                    // Getting JSON Array node
                    val contacts = jsonObj.getJSONArray("apps_data")

                    // looping through All Contacts
                    for (i in 0 until contacts.length()) {
                        val c = contacts.getJSONObject(i)

                        val title = c.getString("title")
                        val description = c.getString("description")
                        val icon_url = c.getString("icon_url")
                        val target_url = c.getString("target_url")


                        titlel!!.add(title)
                        descriptionl!!.add(description)
                        icon_urll!!.add(icon_url)
                        target_urll!!.add(target_url)


                    }
                } catch (e: JSONException) {

                    runOnUiThread {
                        //                                        Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }

            } else {

                runOnUiThread {
                    //                                    Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!",Toast.LENGTH_LONG).show();
                }
            }


            return "umer"


        }



        override fun onPostExecute(file_url: String) {


            //
            if (jsonStr != null) {
                if (title != null) {
                    tinydb?.putListString("title", titlel!!)
                }
                if (descriptionl != null) {
                    tinydb?.putListString("description", descriptionl!!)
                }
                if (icon_urll != null) {
                    tinydb?.putListString("icon_url", icon_urll!!)
                }
                if (target_urll != null) {
                    tinydb?.putListString("target_url", target_urll!!)
                }
                if (sound != null) {
                    tinydb?.putString("sound", sound!!)
                }


                startActivity(Intent(this@SplashScreen, Suggestions::class.java))

            } else {
                tinydb?.putString("sound", "enable")

                startActivity(Intent(this@SplashScreen, Suggestions::class.java))
            }
        }
    }

    companion object {
        private val SPLASH_TIME_OUT = 5000
    }
}







