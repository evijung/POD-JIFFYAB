package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends Activity {

    //Explicit
    public EditText userEditText, passwordEditText;
    private Button button;
    private String userString, passwordString;
    private ImageView logoImageView;
    private Boolean loadStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check Permission
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }

        //Set Locale to TH
        Configuration configuration = new Configuration();
        configuration.locale = new Locale("th");
        getResources().updateConfiguration(configuration, null);


        //Get Device detail
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String device = Build.DEVICE;
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        int version = Build.VERSION.SDK_INT;
//        TelephonyManager telemamanger = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String getSimSerialNumber = telemamanger.getSimSerialNumber();
        String versionRelease = Build.VERSION.RELEASE;

        Log.e("MyActivity", "manufacturer " + manufacturer
                + " \n model " + model
                + " \n device " + deviceName
//                + " \n phone " + getSimSerialNumber
                + " \n version " + version
                + " \n versionRelease " + versionRelease
        );

        // Bind Widget
        userEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        logoImageView = (ImageView) findViewById(R.id.imageView2);

        int res = R.drawable.logo_etg3;
        logoImageView.setImageResource(res);


        // Button controller
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Value From Edit text
                userString = userEditText.getText().toString().trim();
                passwordString = passwordEditText.getText().toString().trim();

                //Check space
                if (userString.equals("")||passwordString.equals("")) {
                    //Have Space
                    MyAlert myAlert = new MyAlert(MainActivity.this);
                    myAlert.myErrorDialog(new MyConstant().getIconAnInt(),
                            new MyConstant().getTitleHaveSpaceString(),
                            new MyConstant().getMessageHaveSpaceString());

                } else {
                    // No Space
                    SynUser synUser = new SynUser(MainActivity.this);
                    MyConstant  myConstant = new MyConstant();
                    synUser.execute(myConstant.getUrlUserString());


                }


            }//Onclick
        });


    }   //Main method

    //Some thing to do when request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //What is permission be request
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    //Check the permission is already have
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //Create Inner Class
    private class SynUser extends AsyncTask<String, Void, String> {
        //Explicit


        private Context context;
        private boolean aBoolean = true;//User false
        private String[] logingStrings = new String[7]; //for User success login
        private String[] columLoginStrings;
        private String truePasswordString;
        private ProgressDialog progressDialog;
        private SynUser synUser = null;


        public SynUser(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            synUser = this;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();


            new CountDownTimer(30000, 10000) {

                @Override
                public void onTick(long l) {
                    //Nothing todo
                    Log.d("TAG", "onTick: Tick ");
                }

                @Override
                public void onFinish() {
                    synUser.cancel(true);
                    progressDialog.dismiss();

                    Log.d("TAG", "onTick: Bool " + loadStatus);
                    if (!loadStatus) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,getResources().getString(R.string.err_internet),Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    Log.d("TAG", "onTick: Finish ");
                }
            }.start();
        }


        @Override
        protected String doInBackground(String... params) {


            if (!isCancelled()) {

                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url(params[0]).build();
                    Response response = okHttpClient.newCall(request).execute();


                    return response.body().string();
                } catch (Exception e) {

                    progressDialog.dismiss();
                    Log.d("11octV1", "e doInback-->" + e.toString());
                    return "NOK";
                }
            } else {
                return "NOK";
            }

        }   // doInback;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("NOK")) {
                progressDialog.dismiss();
                loadStatus = true;
                Log.d("11octV1", "JSON---->" + s);
                MyConstant myConstant = new MyConstant();
                columLoginStrings = myConstant.getColumLogin();

                // using JSON Array
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (userString.equals(jsonObject.getString("drv_username"))) {
                            aBoolean = !aBoolean;
                            for (int i1 = 0; i1 < logingStrings.length; i1++) {
                                logingStrings[i1] = jsonObject.getString(columLoginStrings[i1]);

                                Log.d("11octV2", "LoginString(" + i1 + ")=" + logingStrings[i1]);
                                truePasswordString = jsonObject.getString("drv_password");

                            }//end for i1

                        } //end if

                    }   // end for i

                    if (aBoolean) {
                        MyAlert myAlert = new MyAlert(context);
                        myAlert.myErrorDialog(myConstant.getIconAnInt(),
                                myConstant.getTitleUserFalesString(),
                                myConstant.getMessageUserFalesString());

                    } else if (!passwordString.equals(truePasswordString)) {
                        //password false
                        MyAlert myAlert = new MyAlert(context);
                        myAlert.myErrorDialog(myConstant.getIconAnInt(),
                                myConstant.getTitlePasswordFalse(),
                                myConstant.getMessagePasswordFalse());
                    } else {
                        //password true
                        Log.d("Tag", "Main checkGPSIn ==> " + logingStrings[5] + " checkGPSIn ==> " + logingStrings[6]);
                        Boolean b1, b2;
                        b1 = (logingStrings[5] == "Y");
                        b2 = logingStrings[5].equals("Y");
                        Log.d("Tag", "Main bool '==' ==> " + b1 + " 'equal' ==> " + b2);
                        Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
                        intent.putExtra("Login", logingStrings);
                        intent.putExtra("Date", "");
                        intent.putExtra("PlanId", "");
                        intent.putExtra("TruckNo", "");
                        startActivity(intent);
                        finish();


                    }

                } catch (Exception e) {
                    Log.d("11octV1", "e onPost-->" + e.toString());
                }
            }



        }   //onPost

    }   //SynUser Class




}   //Main Class