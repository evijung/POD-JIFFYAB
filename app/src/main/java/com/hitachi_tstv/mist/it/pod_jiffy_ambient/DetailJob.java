package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailJob extends Activity implements View.OnClickListener {
    //Explicit
    private TextView jobNoTextView, storeCodeTextView, storeNameTextView, arrivalTextView, intentToCallTextView;
    private ListView listView;
    private ImageView firstImageView, secondImageView, thirdImageView, fourthImageView,fifthImageView;
    private Uri firstUri, secondUri, thirdUri, fourthUri, fifthUri;
    private Button arrivalButton, takeImgButton, returnButton, confirmButton, signatureButton, contractButton, backButton, startLoadButton, finLoadButton, sendButton;
    private MyConstant myConstant = new MyConstant();
    private String[] loginStrings, containerStrings, quantityStrings;
    private String dbFirstImgString, dbSecondImgString, dbThirdImgString, dbArriveImgString,dbFourthImgString, dbFirstPathString, dbSecondPathString, dbThirdPathString, dbArrivePathString, dbFourthPathString;
    private String planDtl2_Id, storeCodeString, storeNameString, planIdString, flagString, planDateString, pathFirstImageString, pathSecondImageString, pathThirdImageString, pathFourthImageString,pathFifthImageString, driverUserNameString, getTimeDate, storeLatString, storeLngString, storeRadiusString;
    private boolean sendStatus,emptyBoxConfigABoolean,milesConfigABoolean;
    private LocationManager locationManager;
    private Criteria criteria;
    private Bitmap firstBitmap = null;
    private Bitmap secondBitmap = null;
    private Bitmap thirdBitmap = null;
    private Bitmap fourthBitmap = null;
    private Bitmap fifthBitmap = null;
    private String startLoadString, finLoadString;
    private int version;

//    Create Option Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.in_app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    Menu Option Select
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName componentName = intent.getComponent();
                Intent backToMainIntent = IntentCompat.makeRestartActivityTask(componentName);
                startActivity(backToMainIntent);
                break;
            case R.id.back:
                Intent intent1 = new Intent(DetailJob.this, ServiceActivity.class);
                intent1.putExtra("Login", loginStrings);
                intent1.putExtra("Date", planDateString);
                intent1.putExtra("PlanId", planIdString);
                intent1.putExtra("TruckNo", "");
                startActivity(intent1);

                finish();
                break;

        }
        return true;
    }

//    Main Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_job);


        version = Build.VERSION.SDK_INT;

        //bind Widget
        bindWidget();

        //Set Value
        setValue();

        //Get IntentData
        getIntentData();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date date = new Date();
        getTimeDate = dateFormat.format(date);

        //Load Data
        loadData();

        //Get Event From Click Button or Image
        getEventFromClick();


    }//Main Method

    private void getIntentData() {
        loginStrings = getIntent().getStringArrayExtra("Login");
        planDtl2_Id = getIntent().getStringExtra("planDtl2_id");
        planDateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("PlanId");
        driverUserNameString = loginStrings[2];
    }

    private void setValue() {
        sendStatus = false;
        int picRes = R.drawable.picture;
        firstImageView.setImageResource(picRes);
        secondImageView.setImageResource(picRes);
        thirdImageView.setImageResource(picRes);
        fourthImageView.setImageResource(picRes);
        fifthImageView.setImageResource(picRes);
    }

    private void loadData() {
        SynData synData = new SynData(DetailJob.this);
        synData.execute(myConstant.getUrlDetailWherePlanId(), planDtl2_Id);

        SynContainList synContainList = new SynContainList(DetailJob.this);
        synContainList.execute(myConstant.getUrlContainerList(), planDtl2_Id);
    }

    private void getEventFromClick() {
        firstImageView.setOnClickListener(DetailJob.this);
        secondImageView.setOnClickListener(DetailJob.this);
        thirdImageView.setOnClickListener(DetailJob.this);
        fourthImageView.setOnClickListener(DetailJob.this);
        fifthImageView.setOnClickListener(DetailJob.this);
        arrivalButton.setOnClickListener(DetailJob.this);
        confirmButton.setOnClickListener(DetailJob.this);
        signatureButton.setOnClickListener(DetailJob.this);
        contractButton.setOnClickListener(DetailJob.this);
        backButton.setOnClickListener(DetailJob.this);
        sendButton.setOnClickListener(DetailJob.this);
        returnButton.setOnClickListener(DetailJob.this);
    }

    private Bitmap rotateBitmap(Bitmap src) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    @Override
    public void onBackPressed() {

    }

    private class SynGetImgName extends AsyncTask<String, Void, String> {

        private Context context;

        public SynGetImgName(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.d("TAG", "PlanDtl_2 ==> " + strings[0]);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", strings[0]).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(myConstant.getUrlGetPlanImg()).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                Log.d("TAG", "e doInBack SynGetImgName ==> " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("TAG", "JSON SynGetImgName ==> " + s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("planFileName").contains("first.jpg")) {
                        dbFirstImgString = jsonObject.getString("planFileName");
                        dbFirstPathString = jsonObject.getString("planFilePath");
                        String link = myConstant.getImgLink().toString() + dbFirstPathString.toString();

                        SynLoadImage synLoadImage = new SynLoadImage(firstImageView, context, link);
                        synLoadImage.execute();


                    } else if (jsonObject.getString("planFileName").contains("second.jpg")) {
                        dbSecondImgString = jsonObject.getString("planFileName");
                        dbSecondPathString = jsonObject.getString("planFilePath");
                        String link = myConstant.getImgLink().toString() + dbSecondPathString.toString();

                        SynLoadImage synLoadImage = new SynLoadImage(secondImageView, context, link);
                        synLoadImage.execute();

                    } else if (jsonObject.getString("planFileName").contains("third.jpg")) {
                        dbThirdImgString = jsonObject.getString("planFileName");
                        dbThirdPathString = jsonObject.getString("planFilePath");
                        String link = myConstant.getImgLink().toString() + dbThirdPathString.toString();

                        SynLoadImage synLoadImage = new SynLoadImage(thirdImageView, context, link);
                        synLoadImage.execute();

                    } else if (jsonObject.getString("planFileName").contains("arrive.jpg")) {
                        dbArriveImgString = jsonObject.getString("planFileName");
                        dbArrivePathString = jsonObject.getString("planFilePath");
                        String link = myConstant.getImgLink().toString() + dbArrivePathString.toString();

                        SynLoadImage synLoadImage = new SynLoadImage(fourthImageView, context, link);
                        synLoadImage.execute();

                    } else if (jsonObject.getString("planFileName").contains("fourth.jpg")) {
                        dbFourthImgString = jsonObject.getString("planFileName");
                        dbFourthPathString = jsonObject.getString("planFilePath");
                        String link = myConstant.getImgLink().toString() + dbFourthPathString.toString();

                        SynLoadImage synLoadImage = new SynLoadImage(fifthImageView, context, link);
                        synLoadImage.execute();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class SynLoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView view;
        private Context context;
        private ProgressDialog progressDialog;
        private String urlString;

        public SynLoadImage(ImageView view, Context context, String urlString) {
            this.view = view;
            this.context = context;
            this.urlString = urlString;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlString).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

                Log.d("TAG", "bitmap SynLoadImage ==> " + bitmap);
            } catch (MalformedURLException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            } catch (IOException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            view.setImageBitmap(bitmap);
            progressDialog.dismiss();
        }
    }

    private class SynContainList extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;

        public SynContainList(Context context) {
            this.context = context;
            Log.d("Tag", "Construct SynContainList");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", strings[1]).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();


            } catch (Exception e) {
                Log.d("12octV6", "e doInBack ==> " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray jsonArray = new JSONArray(s);

                containerStrings = new String[jsonArray.length()];
                quantityStrings = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    containerStrings[i] = jsonObject.getString("container");
                    quantityStrings[i] = jsonObject.getString("qty");
                }

                ContainerListAdapter containerListAdapter = new ContainerListAdapter(containerStrings, quantityStrings, context);
                listView.setAdapter(containerListAdapter);

            } catch (Exception e) {
                Log.d("12octV6", "e onPost ==> " + e);
            }
            Log.d("12octV6", "JSON ==> " + s);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0: //From Take Photo
                if (resultCode == RESULT_OK) {
                    Log.d("12octV5", "Take Photo and Save Success");
                }
                break;
            case 1://From Select Image First

                if (resultCode == RESULT_OK) {
                    pathFirstImageString = firstUri.getPath();
                    Log.d("12octV5", "Path First ==> " + pathFirstImageString);
                    try {
                        firstBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(firstUri));
                        if (firstBitmap.getHeight() < firstBitmap.getWidth()) {
                            firstBitmap = rotateBitmap(firstBitmap);
                        }
                        firstImageView.setImageBitmap(firstBitmap);
                        Log.d("TAG", "Height ==> " + firstBitmap.getHeight() + " Width ==> " + firstBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {

                    pathSecondImageString = secondUri.toString();
                    Log.d("12octV5", "Path Second ==> " + pathSecondImageString);
                    try {
                        secondBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(secondUri));
                        if (secondBitmap.getHeight() < secondBitmap.getWidth()) {
                            secondBitmap = rotateBitmap(secondBitmap);
                        }
                        secondImageView.setImageBitmap(secondBitmap);
                        Log.d("TAG", "Height ==> " + secondBitmap.getHeight() + " Width ==> " + secondBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {

                    pathThirdImageString = thirdUri.toString();
                    Log.d("12octV5", "Path Third ==> " + pathThirdImageString);
                    try {
                        thirdBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(thirdUri));
                        if (thirdBitmap.getHeight() < thirdBitmap.getWidth()) {
                            thirdBitmap = rotateBitmap(thirdBitmap);
                        }
                        thirdImageView.setImageBitmap(thirdBitmap);
                        Log.d("TAG", "Height ==> " + thirdBitmap.getHeight() + " Width ==> " + thirdBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {

                    pathFourthImageString = fourthUri.toString();
                    Log.d("12octV5", "Path Fourth ==> " + pathFourthImageString);
                    try {
                        fourthBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fourthUri));
                        if (fourthBitmap.getHeight() < fourthBitmap.getWidth()) {
                            fourthBitmap = rotateBitmap(fourthBitmap);
                        }
                        fourthImageView.setImageBitmap(fourthBitmap);
                        Log.d("TAG", "Height ==> " + fourthBitmap.getHeight() + " Width ==> " + fourthBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 5:
                if (resultCode == RESULT_OK) {

                    pathFifthImageString = fifthUri.toString();
                    Log.d("12octV5", "Path Fifth ==> " + pathFifthImageString);
                    try {
                        fifthBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fifthUri));
                        if (fifthBitmap.getHeight() < fifthBitmap.getWidth()) {
                            fifthBitmap = rotateBitmap(fifthBitmap);
                        }
                        fifthImageView.setImageBitmap(fifthBitmap);
                        Log.d("TAG", "Height ==> " + fifthBitmap.getHeight() + " Width ==> " + fifthBitmap.getWidth());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    private double getMeterFromLatLong(double lat1, double lat2, double lng1, double lng2) {
        Math math = null;
        double result, diffLat, diffLong;
        lat1 = lat1 * (math.PI / 180);
        lat2 = lat2 * (math.PI / 180);
        lng1 = lng1 * (math.PI / 180);
        lng2 = lng2 * (math.PI / 180);

        diffLat = lat2 - lat1;
        diffLong = lng2 - lng1;
        result = (math.sin(diffLat / 2) * math.sin(diffLat / 2)) + (math.cos(lat1) * math.cos(lat2) * (math.sin(diffLong / 2) * math.sin(diffLong / 2)));
        result = 2 * math.atan2(math.sqrt(result), math.sqrt(1 - result));
        result = 6371 * result;
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView5: //First
                File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "first.png");

                Intent cameraIntent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                firstUri = Uri.fromFile(originalFile1);
                Log.d("TAG", "Path 1 " + firstUri);
                cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, firstUri);
                startActivityForResult(cameraIntent1, 1);

                break;
            case R.id.imageView4: //Second
                File originalFile2 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "second.png");

                Intent cameraIntent2 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                secondUri = Uri.fromFile(originalFile2);
                Log.d("TAG", "Path 2 " + secondUri);
                cameraIntent2.putExtra(MediaStore.EXTRA_OUTPUT, secondUri);
                startActivityForResult(cameraIntent2, 2);

                break;
            case R.id.imageView3: //Third
                File originalFile3 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "third.png");

                Intent cameraIntent3 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                thirdUri = Uri.fromFile(originalFile3);
                Log.d("TAG", "Path 3 " + thirdUri);
                cameraIntent3.putExtra(MediaStore.EXTRA_OUTPUT, thirdUri);
                startActivityForResult(cameraIntent3, 3);

                break;
            case R.id.imageView6://Fourth
                File originalFile4 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "arrive.png");

                Intent cameraIntent4 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                fourthUri = Uri.fromFile(originalFile4);
                Log.d("TAG", "Path 4 " + fourthUri);
                cameraIntent4.putExtra(MediaStore.EXTRA_OUTPUT, fourthUri);
                startActivityForResult(cameraIntent4, 4);

                break;
            case R.id.imageView7://Fifth
                File originalFile5 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "fourth.png");

                Intent cameraIntent5 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                fifthUri = Uri.fromFile(originalFile5);
                Log.d("TAG", "Path 5 " + fifthImageView);
                cameraIntent5.putExtra(MediaStore.EXTRA_OUTPUT, fifthUri);
                startActivityForResult(cameraIntent5, 5);
                break;

            case R.id.button7: //Arrival
                if (pathFourthImageString == "" || pathFourthImageString == null) {
                    Toast.makeText(DetailJob.this, getResources().getString(R.string.err_conf1), Toast.LENGTH_SHORT).show();
                } else {
                    String strLat = "Unknown";
                    String strLng = "Unknown";
                    setupLocation();
                    Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
                    if (networkLocation != null) {
                        strLat = String.format("%.7f", networkLocation.getLatitude());
                        strLng = String.format("%.7f", networkLocation.getLongitude());
                    }

                    Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
                    if (gpsLocation != null) {
                        strLat = String.format("%.7f", gpsLocation.getLatitude());
                        strLng = String.format("%.7f", gpsLocation.getLongitude());
                    }


                    if (strLat.equals("Unknown") && strLng.equals("Unknown")) {
                        Toast.makeText(this, getResources().getString(R.string.err_gps1), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("13OctV1", " ++++++++++Latitude.-> " + strLat + " Longitude.-> " + strLng);

                        double lat1, lat2, lng1, lng2;
                        lat1 = Double.parseDouble(strLat);
                        lng1 = Double.parseDouble(strLng);
                        lat2 = Double.parseDouble(storeLatString);
                        lng2 = Double.parseDouble(storeLngString);

                        Log.d("TAG", "lat1 ==> " + lat1 + " lng1 ==> " + lng1 + " lat2 ==> " + lat2 + " lng2 ==> " + lng2);

                        double result = getMeterFromLatLong(lat1, lat2, lng1, lng2);
                        float km = (float) result;
                        float m = Float.parseFloat(String.format("%.2f", km)) * 1000;

                        if (loginStrings[5].equals("Y")) {
                            if (m < Float.parseFloat(storeRadiusString)) {

                                SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, fourthBitmap, "arrive.jpg");
                                synUploadImage.execute();

                                if (sendStatus) {
                                    Toast.makeText(this, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();
                                } else {
                                    sendStatus = false;
                                    pathFourthImageString = null;

                                    SynGPStoServer synGPStoServer = new SynGPStoServer(DetailJob.this);
                                    synGPStoServer.execute(myConstant.getUrlArrivalGPS(), strLat, strLng, getTimeDate, driverUserNameString, planDtl2_Id);

                                }


                            } else {
                                Toast.makeText(this, getResources().getString(R.string.err_gps2), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, fourthBitmap, "arrive.jpg");
                            synUploadImage.execute();

                            if (sendStatus) {
                                Toast.makeText(this, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();

                            } else {
                                sendStatus = false;
                                pathFourthImageString = null;

                                SynGPStoServer synGPStoServer = new SynGPStoServer(DetailJob.this);
                                synGPStoServer.execute(myConstant.getUrlArrivalGPS(), strLat, strLng, getTimeDate, driverUserNameString, planDtl2_Id);

                            }

                        }
                    }


                }
                break;

            case R.id.button8: //Signature
                Intent intentSign = new Intent(DetailJob.this, SignatureActivity.class);
                intentSign.putExtra("Login", loginStrings);
                intentSign.putExtra("PlanDtl", planDtl2_Id);
                intentSign.putExtra("Date", planDateString);
                intentSign.putExtra("PlanId", planIdString);
                startActivity(intentSign);
                break;

            case R.id.button9: //Confirm
                String strLat = "Unknown";
                String strLng = "Unknown";
                setupLocation();

                Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER, "No Internet");
                if (networkLocation != null) {
                    strLat = String.format("%.7f", networkLocation.getLatitude());
                    strLng = String.format("%.7f", networkLocation.getLongitude());
                }

                Location gpsLocation = requestLocation(LocationManager.GPS_PROVIDER, "No GPS card");
                if (gpsLocation != null) {
                    strLat = String.format("%.7f", gpsLocation.getLatitude());
                    strLng = String.format("%.7f", gpsLocation.getLongitude());
                }


                if (strLat.equals("Unknown") && strLng.equals("Unknown")) {
                    Toast.makeText(this, getResources().getString(R.string.err_gps1), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("13OctV1", " ++++++++++Latitude.-> " + strLat + " Longitude.-> " + strLng);

                    double lat1, lat2, lng1, lng2;
                    lat1 = Double.parseDouble(strLat);
                    lng1 = Double.parseDouble(strLng);
                    lat2 = Double.parseDouble(storeLatString);
                    lng2 = Double.parseDouble(storeLngString);

                    Log.d("TAG", "lat1 ==> " + lat1 + " lng1 ==> " + lng1 + " lat2 ==> " + lat2 + " lng2 ==> " + lng2);

                    double result = getMeterFromLatLong(lat1, lat2, lng1, lng2);
                    float km = (float) result;
                    final float m = Float.parseFloat(String.format("%.2f", km)) * 1000;
                    if (loginStrings[6].equals("Y") ) {

                        if (milesConfigABoolean) {
                            final Dialog dialog = new Dialog(DetailJob.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.miles_dialog);
                            dialog.setCancelable(true);

                            Button confirmButton = (Button) dialog.findViewById(R.id.btnDialogConfirm);
                            Button cancelButton = (Button) dialog.findViewById(R.id.btnDialogCancel);
                            final EditText milesEditText = (EditText) dialog.findViewById(R.id.edtMiles);

                            confirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (m < Float.parseFloat(storeRadiusString)) {

                                        SynUpdateStatus synUpdateStatus = new SynUpdateStatus(DetailJob.this, milesEditText.getText().toString());
                                        synUpdateStatus.execute();
                                        dialog.cancel();

                                    } else {
                                        Toast.makeText(DetailJob.this, getResources().getString(R.string.err_gps2), Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                }
                            });
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                }
                            });

                            dialog.show();
                        } else {
                            if (m < Float.parseFloat(storeRadiusString)) {

                                SynUpdateStatus synUpdateStatus = new SynUpdateStatus(DetailJob.this);
                                synUpdateStatus.execute();

                            } else {
                                Toast.makeText(DetailJob.this, getResources().getString(R.string.err_gps2), Toast.LENGTH_SHORT).show();
                            }
                        }



                    } else {
                        if (milesConfigABoolean) {
                            final Dialog dialog = new Dialog(DetailJob.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.miles_dialog);
                            dialog.setCancelable(true);

                            Button confirmButton = (Button) dialog.findViewById(R.id.btnDialogConfirm);
                            Button cancelButton = (Button) dialog.findViewById(R.id.btnDialogCancel);
                            final EditText milesEditText = (EditText) dialog.findViewById(R.id.edtMiles);

                            confirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    SynUpdateStatus synUpdateStatus = new SynUpdateStatus(DetailJob.this, milesEditText.getText().toString());
                                    synUpdateStatus.execute();
                                    dialog.cancel();
                                }
                            });

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                }
                            });

                            dialog.show();
                        } else {
                            SynUpdateStatus synUpdateStatus = new SynUpdateStatus(DetailJob.this);
                            synUpdateStatus.execute();
                        }
                    }
                }

                break;

            case R.id.button14:
                Intent returnContIntent = new Intent(DetailJob.this, ReturnContainerActivity.class);
                returnContIntent.putExtra("Login", loginStrings);
                returnContIntent.putExtra("PlanDtl", planDtl2_Id);
                returnContIntent.putExtra("Date", planDateString);
                returnContIntent.putExtra("PlanId", planIdString);
                returnContIntent.putExtra("StoreCode", storeCodeString);
                returnContIntent.putExtra("StoreName", storeNameString);
                startActivity(returnContIntent);

                break;

            case R.id.button10: //Call
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String phoneNo = "tel:" + intentToCallTextView.getText().toString();
                callIntent.setData(Uri.parse(phoneNo));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);

                break;

            case R.id.button13: //Back
                Log.d("TAG", "Press Back Date ==> " + planDateString);
                Intent backIntent = new Intent(DetailJob.this, ServiceActivity.class);
                backIntent.putExtra("Login", loginStrings);
                backIntent.putExtra("Date", planDateString);
                backIntent.putExtra("PlanId", planIdString);
                backIntent.putExtra("TruckNo", "");
                startActivity(backIntent);
                finish();
                break;

            case R.id.button4:// Send Picture
                Log.d("TAG", "Path bool ==> " + (pathFirstImageString == null));
                Log.d("TAG", "Path bool ==> " + (pathSecondImageString == null));
                Log.d("TAG", "Path bool ==> " + (pathThirdImageString == null));
                Log.d("TAG", "Path bool ==> " + (pathFifthImageString == null));

                if (pathFirstImageString == null && pathSecondImageString == null && pathThirdImageString == null && pathThirdImageString == null && pathFifthImageString == null) {
                    Toast.makeText(this, getResources().getString(R.string.err_conf1), Toast.LENGTH_SHORT).show();
                }

                if (pathFirstImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, firstBitmap, "first.jpg");
                    synUploadImage.execute();
                    if (sendStatus) {
                        //Send Uncomplete
                        Toast.makeText(this, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();

                    } else {
                        //Send Complete
                        sendStatus = false;
                        pathFirstImageString = null;
                    }
                }
                if (pathSecondImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, secondBitmap, "second.jpg");
                    synUploadImage.execute();
                    if (sendStatus) {
                        //Send Uncomplete
                        Toast.makeText(this, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();

                    } else {
                        //Send Complete
                        sendStatus = false;
                        pathSecondImageString = null;
                    }
                }
                if (pathThirdImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, thirdBitmap, "third.jpg");
                    synUploadImage.execute();
                    if (sendStatus) {
                        //Send Uncomplete
                        Toast.makeText(this, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();

                    } else {
                        //Send Complete
                        sendStatus = false;
                        pathThirdImageString = null;
                    }
                }
                if (pathFifthImageString != null) {
                    SynUploadImage synUploadImage = new SynUploadImage(DetailJob.this, fifthBitmap, "fourth.jpg");
                    synUploadImage.execute();
                    if (sendStatus) {
                        //Send Uncomplete
                        Toast.makeText(this, getResources().getString(R.string.save_incomp), Toast.LENGTH_SHORT).show();

                    } else {
                        //Send Complete
                        sendStatus = false;
                        pathFifthImageString = null;
                    }
                }
                break;
        }//switch
    }// onClick

    private class SynUpdateStatus extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;
        private String miles;

        public SynUpdateStatus(Context context, String miles) {
            this.context = context;
            this.miles = miles;
        }

        public SynUpdateStatus(Context context) {
            this.context = context;
            miles = "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TAG", "JSON_Upload ==> " + s);
            boolean b = (s.equals("OK"));
            final String text = s.toString();
            Log.d("Tag", "Bool ==> " + b);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.save_comp), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(DetailJob.this, ServiceActivity.class);
                intent.putExtra("Login", loginStrings);
                intent.putExtra("Date", planDateString);
                intent.putExtra("PlanId", planIdString);
                intent.putExtra("TruckNo", "");
                startActivity(intent);

                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.d("Tag", "PlanDtl2_ID ==> " + planDtl2_Id + " :: Driver_Name ==> " + loginStrings[2]);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("PlanDtl2_ID", planDtl2_Id)
                        .add("Driver_Name", loginStrings[2])
                        .add("Miles", miles)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(myConstant.getUrlUpdateStatus()).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (Exception e) {
                return "NOK2";
            }
        }
    }

    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        //Explicit
        private Context context;
        private Bitmap bitmap;
        private UploadImageUtils uploadImageUtils;
        private String mUploadedFileName;
        ProgressDialog progress;
        Runnable progressRunnable;
        Handler pdCanceller;
        private SynUploadImage synUploadImage;
        private Boolean loadStatus = false;

        public SynUploadImage(Context context, Bitmap bitmap, String mUploadedFileName) {
            this.context = context;
            this.bitmap = bitmap;
            this.mUploadedFileName = mUploadedFileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            synUploadImage = this;
            progress = new ProgressDialog(context);
            progress.setCancelable(false);
            progress.setMessage(getResources().getString(R.string.waiting));
            progress.show();

            progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progress.dismiss();
                }
            };

            new CountDownTimer(30000, 10000){

                @Override
                public void onTick(long l) {
                    //Nothing todo
                    Log.d("TAG", "onTick: Tick ");
                }

                @Override
                public void onFinish() {

                    progress.dismiss();
                    synUploadImage.cancel(true);

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
        protected String doInBackground(Void... voids) {
            if (!isCancelled()) {

                uploadImageUtils = new UploadImageUtils();
                final String result = uploadImageUtils.uploadFile(mUploadedFileName, myConstant.getUrlSaveImage(), bitmap, planDtl2_Id, "P");
                Log.d("TAG", "Do in back after save:-->" + mUploadedFileName);
                Log.d("TAG", "Result :-->" + result);
                if (result.equals("NOK")) {
                    return "NOK1";
                } else {
                    try {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Log.d("name", mUploadedFileName);
                        RequestBody requestBody = new FormEncodingBuilder()
                                .add("isAdd", "true")
                                .add("PlanDtl2_ID", planDtl2_Id)
                                .add("File_Name", mUploadedFileName)
                                .add("File_Path", result)
                                .add("Driver_Name", loginStrings[2])
                                .build();
                        Request.Builder builder = new Request.Builder();
                        Request request = builder.url(myConstant.getUrlSaveImagePath()).post(requestBody).build();
                        Response response = okHttpClient.newCall(request).execute();

                        return response.body().string();
                    } catch (Exception e) {
                        sendStatus = true;
                        return "NOK2";
                    }
                }
            } else {

                sendStatus = true;
                return "NOK2";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            loadStatus = true;

            Log.d("TAG", "JSON_Upload ==> " + s);
            boolean b = (s.equals("OK"));
            Log.d("Tag", "Bool ==> " + b);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_img_comp), Toast.LENGTH_SHORT).show();
                    }
                });

                pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_img_incomp), Toast.LENGTH_SHORT).show();
                    }
                });
                pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 3000);
            }
        }
    }

    private class SynData extends AsyncTask<String, Void, String> {
        //Explicit
        private Context context;

        public SynData(Context context) {
            this.context = context;
        }

        @Override   
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", strings[1]).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strings[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();


            } catch (Exception e) {
                Log.d("12octV4", "e doInBack ==> " + e);
                return null;
            }
        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("12octV4", "JSON ==> " + s);

            try {

                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                //Set Data
                storeCodeString = jsonObject.getString("store_code");
                storeNameString = jsonObject.getString("store_name");

                //Show Text
                jobNoTextView.setText(getResources().getString(R.string.job) + " : " + jsonObject.getString("work_sheet_no"));
                storeCodeTextView.setText(getResources().getString(R.string.store_code) + " : " + storeCodeString);
                storeNameTextView.setText(getResources().getString(R.string.store_name) + " : " + storeNameString);
                arrivalTextView.setText(getResources().getString(R.string.time) + " : " + jsonObject.getString("plan_arrivalDateTime"));
                intentToCallTextView.setText(getResources().getString(R.string.call) + " : " + jsonObject.getString("store_tel"));
                if (jsonObject.getString("config_empty_box").equals("Y")) {
                    emptyBoxConfigABoolean = true;
                } else {
                    emptyBoxConfigABoolean = false;
                }
                if (jsonObject.getString("config_miles").equals("Y")) {
                    milesConfigABoolean = true;
                } else {
                    milesConfigABoolean = false;
                }

                storeLatString = jsonObject.getString("store_lat");
                storeLngString = jsonObject.getString("store_long");
                storeRadiusString = jsonObject.getString("gps_radius");
                flagString = jsonObject.getString("flagArrivaled");

                Log.d("TAG", "Flag ==> " + flagString);

                if (flagString.contains("Y")) {

                    //Next Step to Button set 2 (Signature, Save Img, Confirm, Return Container)
                    firstImageView.setVisibility(View.VISIBLE);
                    secondImageView.setVisibility(View.VISIBLE);
                    thirdImageView.setVisibility(View.VISIBLE);
                    fourthImageView.setVisibility(View.VISIBLE);
                    fifthImageView.setVisibility(View.VISIBLE);
                    confirmButton.setVisibility(View.VISIBLE);
                    signatureButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.VISIBLE);
                    arrivalButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.GONE);
                    if (emptyBoxConfigABoolean){
                        returnButton.setVisibility(View.VISIBLE);
                    }else {
                        returnButton.setVisibility(View.INVISIBLE);
                    }

                    fourthImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    SynGetImgName synGetImgName = new SynGetImgName(context);
                    synGetImgName.execute(planDtl2_Id);


                } else {
                    //Show Button set 1(Arrival and Back) //Set Invisible The Button
                    firstImageView.setVisibility(View.GONE);
                    secondImageView.setVisibility(View.GONE);
                    thirdImageView.setVisibility(View.GONE);
                    fifthImageView.setVisibility(View.GONE);
                    confirmButton.setVisibility(View.GONE);
                    signatureButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.GONE);
                    returnButton.setVisibility(View.GONE);

                }
            } catch (Exception e) {
                Log.d("12octV4", "e onPost ==> " + e);
            }
        }
    }//SynData

    private void bindWidget() {
        //Bind Widget
        jobNoTextView = (TextView) findViewById(R.id.textView14);
        storeCodeTextView = (TextView) findViewById(R.id.textView15);
        storeNameTextView = (TextView) findViewById(R.id.textView16);
        arrivalTextView = (TextView) findViewById(R.id.textView17);
        intentToCallTextView = (TextView) findViewById(R.id.textView18);
        listView = (ListView) findViewById(R.id.livContainer);
        firstImageView = (ImageView) findViewById(R.id.imageView5);
        secondImageView = (ImageView) findViewById(R.id.imageView4);
        thirdImageView = (ImageView) findViewById(R.id.imageView3);
        fourthImageView = (ImageView) findViewById(R.id.imageView6);
        fifthImageView = (ImageView) findViewById(R.id.imageView7);
        arrivalButton = (Button) findViewById(R.id.button7);
        confirmButton = (Button) findViewById(R.id.button9);
        signatureButton = (Button) findViewById(R.id.button8);
        contractButton = (Button) findViewById(R.id.button10);
        backButton = (Button) findViewById(R.id.button13);
        sendButton = (Button) findViewById(R.id.button4);
        returnButton = (Button) findViewById(R.id.button14);
    }

    private class SynGPStoServer extends AsyncTask<String, Void, String> {

        //Explicit
        private Context context;

        public SynGPStoServer(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("Lat", params[1])
                        .add("Lng", params[2])
                        .add("stamp", params[3])
                        .add("drv_username", params[4])
                        .add("planDtl2_id", params[5])
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(params[0]).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.d("13OctV1", "doInBackSynGPS--->" + e.toString());
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("13OctV1", "JSON__GPS->" + s);
            if (s.equals("Success")) {
                pathFourthImageString = null;
                firstImageView.setVisibility(View.VISIBLE);
                secondImageView.setVisibility(View.VISIBLE);
                thirdImageView.setVisibility(View.VISIBLE);
                fifthImageView.setVisibility(View.VISIBLE);
                signatureButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.VISIBLE);
                if (emptyBoxConfigABoolean) {
                    returnButton.setVisibility(View.VISIBLE);
                } else {
                    returnButton.setVisibility(View.INVISIBLE);
                }
                confirmButton.setVisibility(View.VISIBLE);
                arrivalButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);

                fourthImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_gps_comp), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getString(R.string.add_gps_incomp), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }   //onPostExcute
    }// Class Syn GPS TO SERVER

    public Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("GPS", strError);
        }


        return location;
    }

    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            latTextView.setText(String.format("%.7f", location.getLatitude()));
//            lngTextView.setText(String.format("%.7f", location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void setupLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);


    }   // setupLocation
}//Main Class
