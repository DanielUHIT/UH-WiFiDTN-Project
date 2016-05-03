package com.example.android.wifidirect;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by danie on 5/2/2016.
 */

public class MessageFragment extends Activity {

    /**
     * Called when the activity is first created.
     */

    EditText edtx1;
    TextView txvw1;
    TextView txvw2;
    TextView txvw3;
    TextView txvw4;
    TextView txvw5;
    Button btn;
    Button back;
    Button print;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    Random rand = new Random();
    int r = rand.nextInt(5);

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;

    public static Bundle myBundle = new Bundle();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        back = (Button) findViewById(R.id.button2);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), WiFiDirectActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        print = (Button) findViewById(R.id.button3);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtx1 = (EditText) findViewById(R.id.editText1);
                txvw1 = (TextView) findViewById(R.id.textView1);
                txvw2 = (TextView) findViewById(R.id.textView2);
                txvw3 = (TextView) findViewById(R.id.textView3);
                txvw4 = (TextView) findViewById(R.id.textView4);
                txvw5 = (TextView) findViewById(R.id.textView5);
                makeJSON();
            }
        });

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            takeScreenshot();
                        }
                    }, 500);
//                    MessageFragment.myBundle.putString("Message:", "hello");
            }
        });
    }

    public JSONArray makeJSON() {
        JSONArray jArr = new JSONArray();
        JSONObject jObj = new JSONObject();
        try {

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            String deviceName = myDevice.getName();

            //Timestamp
            //Long tsLong = System.currentTimeMillis() / 1000;
            //String ts = tsLong.toString();

            jObj.put("Created_on:", currentDateTimeString);
            jObj.put("Sent_by:", deviceName);
            jObj.put("MAC_Address:", macAddress);
            jObj.put("Number_of_Hops:", r);
            jObj.put("Message:", edtx1.getText());

            txvw1.setText(jObj.getString("Created_on:"));
            txvw2.setText(jObj.getString("Sent_by:"));
            txvw3.setText(jObj.getString("MAC_Address:"));
            txvw4.setText(jObj.getString("Number_of_Hops:"));
            txvw5.setText(jObj.getString("Message:"));

            jArr.put(jObj);

        } catch (Exception e) {
            System.out.println("Error:" + e);
        }

        return jArr;
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[] { "receiver@website.com" });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Truiton Test Mail");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                    "This is an autogenerated mail from Truiton's InAppMail app");
            emailIntent.setType("image/png");
            Uri myUri = Uri.parse("file://" + imageFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
//        Intent intent = new Intent();
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        Uri uri = Uri.fromFile(imageFile);
//        intent.setDataAndType(uri, "image/*");
////        startActivity(intent);
//        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);

}
