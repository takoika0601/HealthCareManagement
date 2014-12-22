package jp.co.akiguchilab.healthcaremanagement.training;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jp.co.akiguchilab.healthcaremanagement.R;

public class ListViewItemAdapter extends ArrayAdapter<ListViewData> {
    private LayoutInflater inflater;
    private int loopCount = 1;

    private Context mContext;

    public ListViewItemAdapter(Context context, List<ListViewData> objects) {
        super(context, 0, objects);
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            view = inflater.inflate(R.layout.my_custom_view, null);
        } else {
            view = convertView;
        }

        TextView textView = (TextView) view.findViewById(R.id.textView1);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
        ListViewData item = (ListViewData) getItem(position);

        if (loopCount % 2 == 0) {
            textView.setTextColor(mContext.getResources().getColor(R.color.listfirst));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.listsecond));
        }
        loopCount++;

        imageView.setImageBitmap(item.getBitmap());
        textView.setText(item.getString());

        return view;
    }
}