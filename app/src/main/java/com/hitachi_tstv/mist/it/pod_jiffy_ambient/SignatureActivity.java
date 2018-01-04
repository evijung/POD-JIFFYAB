package com.hitachi_tstv.mist.it.pod_jiffy_ambient;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class SignatureActivity extends Activity {
    //Explicit
    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign;
    public static String tempDir;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    MyConstant myConstant;
    private String[] loginStrings;
    private String plan2_id, sign_name, planDateString, planIdString, statusString;



    private String uniqueId;
    private EditText yourName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

//        tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        File directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);
//
//        prepareDirectory();
//        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
//        current = uniqueId + ".png";
//        mypath = new File(directory, current);

        plan2_id = getIntent().getStringExtra("PlanDtl");
        loginStrings = getIntent().getStringArrayExtra("Login");
        planDateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("PlanId");
        
        myConstant = new MyConstant();



        mContent = (LinearLayout) findViewById(R.id.linSign);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mClear = (Button) findViewById(R.id.button11);
        mGetSign = (Button) findViewById(R.id.button12);
        mGetSign.setEnabled(false);
        mView = mContent;

        yourName = (EditText) findViewById(R.id.editText5);

        mClear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");
                boolean error = captureSignature();
                if (!error) {
                    mView.setDrawingCacheEnabled(true);
                    mSignature.save(mView);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            //performe the deskred task
                            if (statusString.equals("success")) {
                                Bundle b = new Bundle();
                                b.putString("status", "done");
                                Intent intent = new Intent();
                                intent.putExtras(b);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                //Save Image Don't Success
                            }
                        }
                    }, 45000);


                }
            }
        });

    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";


        if (yourName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, errorMessage + getResources().getString(R.string.err_not_name), Toast.LENGTH_SHORT).show();
        }

        if (error) {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }

        return error;
    }

    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        //Explicit
        private Context context;
        private Bitmap bitmap;
        private UploadImageUtils uploadImageUtils;
        private String mUploadedFileName;
        private String signNameString;


        public SynUploadImage(Context context, Bitmap bitmap, String signNameString) {
            this.context = context;
            this.bitmap = bitmap;
            this.signNameString = signNameString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
            uploadImageUtils = new UploadImageUtils();
            mUploadedFileName = "signature.jpg";

            Log.d("Data", mUploadedFileName);
            Log.d("Data", plan2_id);
            Log.d("Data", bitmap.toString());
            Log.d("Data", myConstant.getUrlSaveImage());
            final String result = uploadImageUtils.uploadFile(mUploadedFileName, myConstant.getUrlSaveImage(), bitmap, plan2_id, "S");
            Log.d("TAG", "Do in back after save:-->" + result);

            if (result.equals("NOK")) {
                return "NOK";
            } else {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("PlanDtl2_ID", plan2_id)
                            .add("Sign_Name", signNameString)
                            .add("File_Name", mUploadedFileName)
                            .add("File_Path", result)
                            .add("drv_username", loginStrings[2])
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url(myConstant.getUrlSaveSignPath()).post(requestBody).build();
                    Response response = okHttpClient.newCall(request).execute();

                    return response.body().string();
                } catch (Exception e) {
                    return "NOK";
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TAG", "JSON_Upload ==> " + s);
            if (s.equals("OK")) {
                statusString = "success";
                Log.d("TAG", "onPostExecute: " + statusString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_comp), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusString = "fail";
                        Toast.makeText(context, getResources().getText(R.string.save_incomp), Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);

                sign_name = yourName.getText().toString();

                Canvas canvas = new Canvas(mBitmap);
                try {
//                    FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                    v.draw(canvas);
//                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                    //   String url = Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
                    Log.v("log_tag", "Bitmap=++++++++++++++: " + mBitmap);

                    SynUploadImage synUploadImage = new SynUploadImage(SignatureActivity.this, mBitmap, sign_name);
                    synUploadImage.execute();
                    //

//                    mFileOutStream.flush();
//                    mFileOutStream.close();

                    //   saveBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(url)));

//                saveBitmap.setImageBitmap(saveBitmap);

                    //In case you want to delete the file
                    //boolean deleted = mypath.delete();
                    //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                    //If you want to convert the image to string use base64 converter

                } catch (Exception e) {
                    Log.v("log_tag", e.toString());
                }
            }//end if
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
