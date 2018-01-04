package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by masterUNG on 10/12/2016 AD.
 */

public class DetailAdapter extends BaseAdapter {

    //Explicit
    private Context context;
    private String[] workSheetStrings, storeNameStrings,
            planArrivalTimeStrings;
    private TextView workSheetTextView, storeNameTextView,
            planArrivalTextView;

    public DetailAdapter(Context context,
                         String[] workSheetStrings,
                         String[] storeNameStrings,
                         String[] planArrivalTimeStrings) {
        this.context = context;
        this.workSheetStrings = workSheetStrings;
        this.storeNameStrings = storeNameStrings;
        this.planArrivalTimeStrings = planArrivalTimeStrings;
    }

    @Override
    public int getCount() {
        return workSheetStrings.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = layoutInflater.inflate(R.layout.detail_listview, viewGroup, false);

        //Bind Widget
        workSheetTextView = (TextView) view1.findViewById(R.id.textView11);
        storeNameTextView = (TextView) view1.findViewById(R.id.textView12);
        planArrivalTextView = (TextView) view1.findViewById(R.id.textView13);

        //Show View
        workSheetTextView.setText(workSheetStrings[i]);
        storeNameTextView.setText(storeNameStrings[i]);
        planArrivalTextView.setText(planArrivalTimeStrings[i]);

        return view1;
    }
}   // Main Class