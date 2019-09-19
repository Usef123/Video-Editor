package med.umerfarooq.com.videoeditor.Json

import android.util.Log

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

// constructor
class JSONParser {

    // function get json from url
    // by making HTTP POST or GET mehtod
    fun makeHttpRequest(url: String): JSONObject? {

        // Making HTTP request
        try {

            // check for request method
            // request method is POST
            // defaultHttpClient
            val httpClient = DefaultHttpClient()
            val httpPost = HttpPost(url)


            val httpResponse = httpClient.execute(httpPost)
            val httpEntity = httpResponse.entity
            `is` = httpEntity.content


            // request method is GET

        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val reader = BufferedReader(InputStreamReader(
                    `is`!!, "iso-8859-1"), 8)
            val sb = StringBuilder()
            var line: String = ""
            while ((line in reader.readLine()) != null) {
                sb.append(line!! + "\n")
            }
            `is`!!.close()
            json = sb.toString()
        } catch (e: Exception) {
            Log.e("Buffer Error", "Error converting result $e")
        }

        // try parse the string to a JSON object
        try {
            jObj = JSONObject(json)
        } catch (e: JSONException) {
            Log.e("JSON Parser", "Error parsing data $e")
        }

        // return JSON String
        return jObj

    }

    companion object {

        internal var `is`: InputStream? = null
        internal var jObj: JSONObject? = null
        internal var json = ""
    }
}