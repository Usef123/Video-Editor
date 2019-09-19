package med.umerfarooq.com.videoeditor.VideoFeatures.Listhome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide

import java.util.ArrayList

import med.umerfarooq.com.videoeditor.R

/**
 * Created by Umerfarooq on 4/13/2018.
 */

class CustomAdapter(private val dataSet: ArrayList<DataModel>?, internal var mContext: Context) : ArrayAdapter<DataModel>(mContext, R.layout.list_item, dataSet), View.OnClickListener {

    private var lastPosition = -1

    // View lookup cache
    private class ViewHolder {
        internal var title: TextView? = null
        internal var decription: TextView? = null
        internal var picaso: ImageView? = null

    }

    override fun onClick(v: View) {

        val position = v.tag as Int
        val `object` = getItem(position)
        val dataModel = `object`

        //        switch (v.getId())
        //        {
        //            case R.id.item_info:
        //                Snackbar.make(v,"Release date " +dataModel.getFeature(),Snackbar.LENGTH_LONG)
        //                        .setAction("No action", null).show();
        //                break;
        //        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val dataModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        val result: View

        if (convertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.list_item, parent, false)
            viewHolder.title = convertView!!.findViewById<View>(R.id.title) as TextView
            viewHolder.decription = convertView.findViewById<View>(R.id.discription) as TextView
            viewHolder.picaso = convertView.findViewById<View>(R.id.picaso) as ImageView


            result = convertView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val animation = AnimationUtils.loadAnimation(mContext, if (position > lastPosition) R.anim.up_from_bottom else R.anim.down_from_top)
        result.startAnimation(animation)
        lastPosition = position

        viewHolder.title!!.text = dataModel!!.title
        viewHolder.decription!!.text = dataModel.descriptiob

        Glide.with(mContext)
                .load(dataModel.icon)
                .into(viewHolder.picaso!!)


        return convertView
    }
}