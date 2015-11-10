package jp.co.akiguchilab.healthcaremanagement.calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import jp.co.akiguchilab.healthcaremanagement.R;

public class PicturesFragment extends Fragment {
    private GridView mGridView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PicturesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_preview_dialog, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setAdapter(new GridAdapter(getActivity(), null));

        return view;
    }
}
