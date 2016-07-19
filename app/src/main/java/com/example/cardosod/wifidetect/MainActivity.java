package com.example.cardosod.wifidetect;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    private final String TAG = getClass().getSimpleName();
    private static final int REQUEST_FINE_LOCATION=0;


    private Handler handler;
    ArrayList<WifiInfo> wifis = new ArrayList<WifiInfo>();

    private TextView textView;
    private EditText roomEt;
    private EditText idCollect;
    private Boolean controlButton = false;
    private Boolean permission = false;
    private int collect_id = 0;
    private MediaScannerHelper mediaScannerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaScannerHelper = new MediaScannerHelper();
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if(mainWifi.isWifiEnabled()==false)
        {
            mainWifi.setWifiEnabled(true);
        }

        textView = (TextView) findViewById(R.id.textView);
        roomEt = (EditText) findViewById(R.id.roomEt);
        idCollect = (EditText) findViewById(R.id.idCollect);
        mayRequestLocation();
        handler = new Handler();

        final Button btSave = (Button) findViewById(R.id.btSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roomEt.length() > 0) {
                    collect_id = (idCollect.length()==0) ? 0 : Integer.valueOf(idCollect.getText().toString());
                    controlButton ^= true;
                    if (controlButton && permission) {
                    //if (controlButton){
                        Log.d(TAG, "Collecting");
                        doInback();
                        btSave.setText("Stop Scan");
                    } else {

                        roomEt.setText("");
                        idCollect.setText("");
                        Log.d(TAG, "Not Collecting");
                        if(receiverWifi!=null) {
                            unregisterReceiver(receiverWifi);
                        }
                        btSave.setText("Start Scan");
                        setTextView("");
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Type location name",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void doInback() {
        if(controlButton && permission) {
        //if (controlButton){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Running");
                    mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    receiverWifi = new WifiReceiver();
                    registerReceiver(receiverWifi, new IntentFilter(
                            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    mainWifi.startScan();
                    doInback();
                }
            }, 10 * 1000);
        }
    }

    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permission = true;
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(getCurrentFocus(), "Do you agree?", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FINE_LOCATION);
                        }
                    });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_FINE_LOCATION);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                } else {
                    permission = false;
                    moveTaskToBack(true);
                    finish();
                    //System.exit(1);
                }
                return;
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);}


    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(receiverWifi != null) {
            unregisterReceiver(receiverWifi);
        }
        controlButton = false;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTextView(String msg){
        System.out.println(msg);
        textView.setText(msg);
    }

    public void saveWifiData(){
        String filename = roomEt.getText().toString() + ".txt";
        String root = Environment.getExternalStorageDirectory().toString();
        String filePath = root + "/";
        Log.d(getClass().getSimpleName(),filePath);
        System.out.println("File: " + filePath + filename);
        File file = new File(filePath, filename);

        String room = roomEt.getText().toString();


        FileOutputStream stream;
        PrintWriter writer;
        try {
            stream = new FileOutputStream(file, true);
            writer = new PrintWriter(stream);
            String prefix = room + ";" + String.valueOf(collect_id) + ";";
            for(WifiInfo info : wifis){
                String data = prefix + info.toPipe();
                Log.d(TAG,data);
                writer.println(data);
            }
            writer.close();
            stream.close();
            mediaScannerHelper.addFile(String.valueOf(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class WifiReceiver extends BroadcastReceiver
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void onReceive(Context c, Intent intent)
        {
            wifis.clear();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for(int i = 0; i < wifiList.size(); i++)
            {
                ScanResult wifi =  wifiList.get(i);
                WifiInfo info = new WifiInfo();
                info.setBssid(wifi.BSSID);
                info.setSignal(wifi.level);
                if(wifi.SSID == "" || wifi.SSID == null) {
                    info.setSsid("BRANCO");
                }
                else{
                    info.setSsid(wifi.SSID);
                }
                info.setFrequency(wifi.frequency);
                info.setTimestamp(wifi.timestamp);
                wifis.add(info);
            }
            if(wifiList.size()!=0 && controlButton!=false) {
                setTextView("Access points found: " + wifis.size() + " with collect id: "+ collect_id);
                saveWifiData();
                collect_id+=1;
            }
        }
    }

    public class MediaScannerHelper implements MediaScannerConnection.MediaScannerConnectionClient {

        public void addFile(String filename)
        {
            String [] paths = new String[1];
            paths[0] = filename;
            MediaScannerConnection.scanFile(getApplicationContext(), paths, null, this);
        }

        public void onMediaScannerConnected() {
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.i("ScannerHelper", "Scan done - path:" + path + " uri:" + uri);
        }
    }
}
