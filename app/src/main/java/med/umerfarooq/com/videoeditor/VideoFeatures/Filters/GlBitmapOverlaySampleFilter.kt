package med.umerfarooq.com.videoeditor.VideoFeatures.Filters

import android.graphics.Bitmap
import android.graphics.Canvas

import com.daasuu.mp4compose.filter.GlOverlayFilter

/**
 * Created by sudamasayuki on 2018/01/07.
 */
class GlBitmapOverlaySampleFilter(private val bitmap: Bitmap?) : GlOverlayFilter() {

    override fun drawCanvas(canvas: Canvas) {
        if (bitmap != null && !bitmap.isRecycled) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        }
    }

    override fun release() {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
}