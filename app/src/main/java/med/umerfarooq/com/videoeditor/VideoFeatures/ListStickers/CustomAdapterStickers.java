package med.umerfarooq.com.videoeditor.VideoFeatures.ListStickers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import med.umerfarooq.com.videoeditor.R;

/**
 * Created by Umerfarooq on 4/18/2018.
 */

public class CustomAdapterStickers extends ArrayAdapter<Integer>
        implements View.OnClickListener{

    private ArrayList<Integer> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
       
        ImageView info;
    }

    public CustomAdapterStickers(ArrayList<Integer> data, Context context) {
        super(context, R.layout.listviewstickers, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {


       
    }

    private int lastPosition = -1;

    @Override
    public int getCount()
    {
        return dataSet.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

//        DataModelStickers DataModelStickers = getItem(position);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listviewstickers, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_info);
        imageView.setImageResource(dataSet.get(position));



        return rowView;



     

    }
}