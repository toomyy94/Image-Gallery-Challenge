package challenge.tomas.pt.imagegalleryapp.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import challenge.tomas.pt.imagegalleryapp.R;
import challenge.tomas.pt.imagegalleryapp.data.FlickerSearchData;

/**
 * Created by Tomás Rodrigues on 04/09/2018.
 */

public class SearchListAdapter extends ArrayAdapter<FlickerSearchData> implements View.OnClickListener{

    private ArrayList<FlickerSearchData> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        ImageView image;
    }

    public SearchListAdapter(ArrayList<FlickerSearchData> data, Context context) {
        super(context, R.layout.search_list, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public void refreshEvents(ArrayList<FlickerSearchData> events) {
        this.dataSet.clear();
        this.dataSet.addAll(events);
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        final FlickerSearchData flickerSearchData = (FlickerSearchData) object;

//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(flickerSearchData.getUrl()));
//        getContext().startActivity(browserIntent);
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FlickerSearchData flickerSearchData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_list, parent, false);
            viewHolder.image = convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        lastPosition = position;

        viewHolder.image.setImageBitmap(flickerSearchData.getSquaredImage().second);

        // Return the completed view to render on screen
        return convertView;
    }

}
