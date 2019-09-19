package med.umerfarooq.com.videoeditor.VideoFeatures

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView

object VideoControl {
    fun injectVideo(context: Context, vv: VideoView, path: String) {
        vv.visibility = 0
        val videoMediaController = MediaController(context)
        vv.setVideoPath(path)
        videoMediaController.setMediaPlayer(vv)
        vv.setMediaController(videoMediaController)
        vv.requestFocus()
        vv.start()
    }

    fun getDuration(context: Context, uri: Uri): Long {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            return java.lang.Long.parseLong(retriever.extractMetadata(9))
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }

    }

    fun getDuration(timeInmillisec: Long): String {
        val duration = timeInmillisec / 1000
        val hours = duration / 3600
        val minutes = (duration - 3600 * hours) / 60
        val seconds = duration - (3600 * hours + 60 * minutes)
        val percent = timeInmillisec % 1000 / 10
        return (if (hours < 10) "0" + java.lang.Long.toString(hours) else java.lang.Long.toString(hours)) + ":" + (if (minutes < 10) "0" + java.lang.Long.toString(minutes) else java.lang.Long.toString(minutes)) + ":" + (if (seconds < 10) "0" + java.lang.Long.toString(seconds) else java.lang.Long.toString(seconds)) + "." + if (percent < 10) "0" + java.lang.Long.toString(percent) else java.lang.Long.toString(percent)
    }

    fun getDurationInSecond(timeInmillisec: Long): String {
        val duration = timeInmillisec / 1000
        val hours = duration / 3600
        val minutes = (duration - 3600 * hours) / 60
        val seconds = duration - (3600 * hours + 60 * minutes)
        return (if (hours < 10) "0" + java.lang.Long.toString(hours) else java.lang.Long.toString(hours)) + ":" + (if (minutes < 10) "0" + java.lang.Long.toString(minutes) else java.lang.Long.toString(minutes)) + ":" + if (seconds < 10) "0" + java.lang.Long.toString(seconds) else java.lang.Long.toString(seconds)
    }

    fun progressDurationInMs(s: String): Long {
        val timeCode = s.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return (java.lang.Double.parseDouble(timeCode[0].trim { it <= ' ' }) * 60.0 * 60.0 * 1000.0 + java.lang.Double.parseDouble(timeCode[1].trim { it <= ' ' }) * 60.0 * 1000.0 + java.lang.Double.parseDouble(timeCode[2].trim { it <= ' ' }) * 1000.0).toLong()
    }
}
