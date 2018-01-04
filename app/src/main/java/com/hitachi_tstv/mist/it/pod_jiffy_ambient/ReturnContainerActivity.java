package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class ReturnContainerActivity extends Activity {
    private Button saveButton, clearButton;
    private MyConstant myConstant = new MyConstant();
    private String storeCodeString, storeNameString, planString;
    private String[] imageStrings, containerNameStrings, contIdStrings, dbQuantityStrings, dbContainerNameStrings, dbContIdStrings, loginStrings;
    private TextView storeTextView, quantityTextView;
    private ListView containerListView, dbReturnContListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_container);

        //bind widget
        saveButton = (Button) findViewById(R.id.button6);
//        clearButton = (Button) findViewById(R.id.button5);
        containerListView = (ListView) findViewById(R.id.containerListview);
        dbReturnContListView = (ListView) findViewById(R.id.dbReturn);
        storeTextView = (TextView) findViewById(R.id.txtStoreCode);

        //Get String Extra
        storeCodeString = getIntent().getStringExtra("StoreCode");
        storeNameString = getIntent().getStringExtra("StoreName");

        storeTextView.setText(getResources().getString(R.string.store_code) + " " + storeCodeString + " " + storeNameString);

        planString = getIntent().getStringExtra("PlanDtl");
        loginStrings = getIntent().getStringArrayExtra("Login");

        Log.d("Tag", planString);

        //Syn data to get Basket
        SynGetBasket synGetBasket = new SynGetBasket(ReturnContainerActivity.this);
        synGetBasket.execute(myConstant.getUrlDataContainer());

        SyncGetReturnContQty syncGetReturnContQty = new SyncGetReturnContQty(ReturnContainerActivity.this);
        syncGetReturnContQty.execute(planString);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }

    private class DBReturnContAdaptor extends BaseAdapter {

        private Context context;
        private String[] contNameStrings, contQtyStrings;
        private TextView contNameTextView, contQtyTextView;

        public DBReturnContAdaptor(Context context) {
            this.context = context;
        }

        public DBReturnContAdaptor(Context context, String[] contNameStrings, String[] contQtyStrings) {
            this.context = context;
            this.contNameStrings = contNameStrings;
            this.contQtyStrings = contQtyStrings;
        }

        @Override
        public int getCount() {
            return contNameStrings.length;
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
            Log.d("Tag1", "Layout ==> " + layoutInflater);

            View view1 = layoutInflater.inflate(R.layout.db_return_listview, viewGroup, false);

            //Bind widget
            contNameTextView = (TextView) view1.findViewById(R.id.textView30);
            contQtyTextView = (TextView) view1.findViewById(R.id.textView29);

            contNameTextView.setText(contNameStrings[i]);
            contQtyTextView.setText(contQtyStrings[i]);

            return view1;
        }
    }


    private class SyncGetReturnContQty extends AsyncTask<String, Void, String> {

        private Context context;

        public SyncGetReturnContQty(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Log.d("Tag", "SyncGetReturnContQty JSON ==> " + s);

            Boolean aBoolean = s.equals("null");
            if (s.equals("null")) {
//                String[] oneStrings = new String[0];
//                String[] twoStrings = new String[0];
//
//                DBReturnContAdaptor dbReturnContAdaptor = new DBReturnContAdaptor(context,oneStrings,twoStrings);
                dbReturnContListView.setAdapter(null);
            }else {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    dbQuantityStrings = new String[jsonArray.length()];
                    dbContainerNameStrings = new String[jsonArray.length()];
                    dbContIdStrings = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dbQuantityStrings[i] = jsonObject.getString("qty");
                        dbContIdStrings[i] = jsonObject.getString("conNo");
                        dbContainerNameStrings[i] = jsonObject.getString("container");
                    }

                    Log.d("Tag", "dbQuantityStrings ==> " + Arrays.deepToString(dbQuantityStrings));
                    Log.d("Tag", "dbContIdStrings ==> " + Arrays.deepToString(dbContIdStrings));
                    Log.d("Tag", "dbContainerNameStrings ==> " + Arrays.deepToString(dbContainerNameStrings));

                    DBReturnContAdaptor dbReturnContAdaptor = new DBReturnContAdaptor(context, dbContainerNameStrings, dbQuantityStrings);
                    dbReturnContListView.setAdapter(dbReturnContAdaptor);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", strings[0]).build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(myConstant.getUrlGetReturnContainerQuantity()).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    private class SyncUploadQuantity extends AsyncTask<String, Void, String> {
        private Context context;
        private String containerString, quantityString, driverString, planIdString;

        public SyncUploadQuantity(Context context, String containerString, String quantityString, String driverString, String planIdString) {
            this.context = context;
            this.containerString = containerString;
            this.quantityString = quantityString;
            this.driverString = driverString;
            this.planIdString = planIdString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "SyncUploadQuantity JSON ==> " + s);


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();

                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("ContainerId", containerString)
                        .add("Quantity", quantityString)
                        .add("drv_username", driverString)
                        .add("planDtl2_id", planIdString).build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(myConstant.getUrlSaveReturnCont()).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class SynGetBasket extends AsyncTask<String, Void, String> {
        private Context context;

        public SynGetBasket(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag20/03/2017", "JSON ==> " + s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                imageStrings = new String[jsonArray.length()];
                containerNameStrings = new String[jsonArray.length()];
                contIdStrings = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    imageStrings[i] = myConstant.getLink() + jsonObject.getString("conFilePath");
                    containerNameStrings[i] = jsonObject.getString("contType_description");
                    contIdStrings[i] = jsonObject.getString("conType_id");
                }

                ReturnContainerAdapter returnContainerAdapter = new ReturnContainerAdapter(context, containerNameStrings, imageStrings, contIdStrings);
                containerListView.setAdapter(returnContainerAdapter);
                containerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.d("Tag", String.valueOf(i));


                        final Dialog dialog = new Dialog(ReturnContainerActivity.this);
                        dialog.setTitle(containerNameStrings[i]);
                        dialog.setContentView(R.layout.dialog_get_return);

                        final String container = contIdStrings[i];


                        quantityTextView = (TextView) view.findViewById(R.id.textView24);
                        final EditText quantityEditText = (EditText) dialog.findViewById(R.id.editText4);

                        Button saveButton = (Button) dialog.findViewById(R.id.button15);

                        quantityEditText.setText(quantityTextView.getText().toString());

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("TAG", "Edit Text ==> " + quantityEditText.getText());
                                quantityTextView.setText(quantityEditText.getText());

                                SyncUploadQuantity syncUploadQuantity = new SyncUploadQuantity(context, container, quantityEditText.getText().toString(), loginStrings[1], planString);
                                syncUploadQuantity.execute();

                                SyncGetReturnContQty syncGetReturnContQty = new SyncGetReturnContQty(context);
                                syncGetReturnContQty.execute(planString);

                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                        Log.d("Tag", String.valueOf(dialog));
                    }
                });

            } catch (JSONException e) {
                Log.d("Tag", "e onPost ==> " + e.toString());
            }


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Log.d("Tag20/03/2017", "Do in back");

                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("planDtl2_id", planString).build();
                Request request = builder.post(requestBody).url(strings[0]).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag20/03/2017", "e doInBack ==> " + e.toString());
                return null;
            }

        }//DoInBack
    }
}
