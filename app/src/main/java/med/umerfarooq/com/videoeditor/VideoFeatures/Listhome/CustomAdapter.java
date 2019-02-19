package med.umerfarooq.com.videoeditor.VideoFeatures.Listhome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import med.umerfarooq.com.videoeditor.R;

/**
 * Created by Umerfarooq on 4/13/2018.
 */

public class CustomAdapter extends ArrayAdapter<DataModel>
        implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView decription;
        ImageView picaso;

    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context,R.layout.list_item,data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v,"Release date " +dataModel.getFeature(),Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.decription = (TextView) convertView.findViewById(R.id.discription);
            viewHolder.picaso = (ImageView) convertView.findViewById(R.id.picaso);
          

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.title.setText(dataModel.getTitle());
        viewHolder.decription.setText(dataModel.getDescriptiob());
        ;

        Glide.with(mContext)
                .load(dataModel.getIcon())
                .into(viewHolder.picaso);


        return convertView;
    }
}