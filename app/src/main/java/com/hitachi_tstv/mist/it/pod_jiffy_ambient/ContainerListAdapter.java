package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by tunyaporns on 10/12/2016.
 */

public class ContainerListAdapter extends BaseAdapter {
    //Explicit
    private TextView containerTextView, quantityTextView, idTextView;
    private String[] containerStrings, quantityStrings;
    private Context context;

    public ContainerListAdapter(String[] containerStrings, String[] quantityStrings, Context context) {
        this.containerStrings = containerStrings;
        this.quantityStrings = quantityStrings;
        this.context = context;
    }

    @Override
    public int getCount() {
        return containerStrings.length;
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
        View view1 = layoutInflater.inflate(R.layout.container_detail_listview, viewGroup, false);

        containerTextView = (TextView) view1.findViewById(R.id.textView20);
        quantityTextView = (TextView) view1.findViewById(R.id.textView19);
        idTextView = (TextView) view1.findViewById(R.id.textView22);

        containerTextView.setText(containerStrings[i]);
        quantityTextView.setText(quantityStrings[i]);
        idTextView.setText(String.valueOf(i+1));

        return view1;
    }
}
