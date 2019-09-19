package med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

import java.util.ArrayList

import med.umerfarooq.com.videoeditor.R

/**
 * Created by Umerfarooq on 4/18/2018.
 */

class CustomAdapterStickers(private val dataSet: ArrayList<Int>, internal var mContext: Context) : ArrayAdapter<Int>(mContext, R.layout.listviewstickers, dataSet), View.OnClickListener {

    private val lastPosition = -1

    // View lookup cache
    private class ViewHolder {

        internal var info: ImageView? = null
    }

    override fun onClick(v: View) {


    }

    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get the data item for this position

        //        DataModelStickers DataModelStickers = getItem(position);
        val inflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.listviewstickers, parent, false)

        val imageView = rowView.findViewById<View>(R.id.item_info) as ImageView
        imageView.setImageResource(dataSet[position])



        return rowView


    }
}