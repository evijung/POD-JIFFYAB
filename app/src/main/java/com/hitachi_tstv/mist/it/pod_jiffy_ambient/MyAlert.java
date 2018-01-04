package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by musz on 10/11/2016.
 */

public class MyAlert {
    //Explicit
    private Context context;

    public MyAlert(Context context) {
        this.context = context;
    }

    // Dialog  for Error
    public void myErrorDialog(int intIcon,
                              String strTitle,
                              String strMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setIcon(intIcon);
        builder.setTitle(strTitle);
        builder.setMessage(strMessage);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}   // Main Class