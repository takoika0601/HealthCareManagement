package jp.co.akiguchilab.healthcaremanagement.calendar;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import jp.co.akiguchilab.healthcaremanagement.R;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private final GridAdapter self = this;
    private ArrayList<Uri> mDataList;
    private LayoutInflater mInflater;

    private static class ViewHolder {
        public ImageView mImageView;
    }

    public GridAdapter(Context context, ArrayList<Uri> dataList) {
        super();

        mInflater = LayoutInflater.from(context);
        mDataList = dataList;
    }

    public int getCount() {
        if (mDataList == null) {
            return -1;
        }
        return mDataList.size();
    }

    public Object getItem(int position) {
        return mDataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.mImageView = (ImageView)convertView.findViewById(R.id.lbl_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.mImageView.setImageURI(mDataList.get(position));

        return convertView;
    }
}
