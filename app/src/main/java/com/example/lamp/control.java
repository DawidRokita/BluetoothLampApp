package com.example.lamp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class control extends AppCompatActivity {

    private Button settings, on_btn, off_btn, plus_btn, minus_btn, twenty_prc_btn, fivety_prc_btn, seventyfive_prc_btn, hundred_prc_btn, DiscoBtn;
    String  tmpName, seekBarMsg, OnMsgStr, OffMsgStr, DiscoMsgStr;
    private SeekBar seekBar;

    public BluetoothAdapter btAdapter;
    public BluetoothDevice btDevice;
    public BluetoothSocket btSocket;

    public static final String SERVICE_ID = "00001101-0000-1000-8000-00805f9b34fb"; //SPP UUID //nie trzeba zmieniac

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //ukryj top bar aplikacji
        setContentView(R.layout.activity_control);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        setupUIViews();
        getAllSettings();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });


        BtConnect();

        clickMethod(on_btn, OnMsgStr);
        clickMethod(off_btn, OffMsgStr);
        clickMethod(DiscoBtn, DiscoMsgStr);

        changeBgOnClick();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarMsg = Integer.toString(progress);
                sendMessage(seekBarMsg + "E");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Buttons();

    }

    public void openSettingsActivity(){
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    private void setupUIViews(){
        settings = (Button) findViewById(R.id.settings_btn);
        on_btn = (Button) findViewById(R.id.on_btn);
        off_btn = (Button) findViewById(R.id.off_btn);
        plus_btn = (Button) findViewById(R.id.plus_btn);
        minus_btn = (Button) findViewById(R.id.minus_btn);
        twenty_prc_btn = (Button) findViewById(R.id.twenty_prc_btn);
        fivety_prc_btn = (Button) findViewById(R.id.fivety_prc_btn);
        seventyfive_prc_btn = (Button) findViewById(R.id.seventyfive_prc_btn);
        hundred_prc_btn = (Button) findViewById(R.id.hundred_prc_btn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        DiscoBtn = (Button) findViewById(R.id.DiscoBtn);
    }

    private void getAllSettings(){
        try {
            SharedPreferences sp2 = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
            tmpName = sp2.getString("btDevice", "");
            OnMsgStr = sp2.getString("OnMsg", "");
            OffMsgStr = sp2.getString("OffMsg", "");
            DiscoMsgStr = sp2.getString("DiscoMsg", "");
            btDevice = btAdapter.getRemoteDevice(tmpName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String msg){
        if(btSocket != null) {
            try{
                OutputStream out = btSocket.getOutputStream();
                out.write((msg + "\r\n").getBytes());
            }catch(IOException e) {

            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket thisSocket;
        private final BluetoothDevice thisDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            thisDevice = device;
            try {
                tmp = thisDevice.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_ID));
                Toast.makeText(control.this, "łączenie", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("TEST", "Can't connect to service");
            }
            thisSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            btAdapter.cancelDiscovery();

            try {
                thisSocket.connect();
                Log.d("TESTING", "Connected to shit");
            } catch (IOException connectException) {
                try {
                    thisSocket.close();
                } catch (IOException closeException) {
                    Log.e("TEST", "Can't close socket");
                }
                return;
            }

            btSocket = thisSocket;

        }
        public void cancel() {
            try {
                thisSocket.close();
            } catch (IOException e) {
                Log.e("TEST", "Can't close socket");
            }
        }
    }

    private void clickMethod(final Button btn, final String msg){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(msg);
            }
        });
    }

    private void changeBgOnClick(){
        twenty_prc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twenty_prc_btn.setBackgroundResource(R.drawable.clicked_btn_bg);
                fivety_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                seventyfive_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                hundred_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                seekBar.setProgress(51);
                sendMessage("51E");
            }
        });

        fivety_prc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twenty_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                fivety_prc_btn.setBackgroundResource(R.drawable.clicked_btn_bg);
                seventyfive_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                hundred_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                seekBar.setProgress(127);
                sendMessage("127E");
            }
        });

        seventyfive_prc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twenty_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                fivety_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                seventyfive_prc_btn.setBackgroundResource(R.drawable.clicked_btn_bg);
                hundred_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                seekBar.setProgress(190);
                sendMessage("190E");
            }
        });

        hundred_prc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twenty_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                fivety_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                seventyfive_prc_btn.setBackgroundResource(R.drawable.brightness_btn_background);
                hundred_prc_btn.setBackgroundResource(R.drawable.clicked_btn_bg);
                seekBar.setProgress(255);
                sendMessage("255E");
            }
        });
    }

    private void BtConnect(){
        try {
            if (btAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
            } else {
                if (!btAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, 3);
                } else {
                    control.ConnectThread connectThread = new control.ConnectThread(btDevice);
                    connectThread.start();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Buttons(){
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seekBar.getProgress() < 255)
                {
                    if(seekBar.getProgress() < 15)			seekBar.setProgress(seekBar.getProgress() + 2);
                    else if(seekBar.getProgress() < 100)	seekBar.setProgress(seekBar.getProgress() + 10);
                    else if(seekBar.getProgress() < 220)    seekBar.setProgress(seekBar.getProgress() + 20);
                    else seekBar.setProgress(255);
                }
                sendMessage(seekBarMsg + "E");
            }
        });

        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (seekBar.getProgress() > 0)
                {
                    if(seekBar.getProgress() > 100)			seekBar.setProgress(seekBar.getProgress() - 20);
                    else if(seekBar.getProgress() > 15)	    seekBar.setProgress(seekBar.getProgress() - 10);
                    else if(seekBar.getProgress() > 5)      seekBar.setProgress(seekBar.getProgress() - 2);
                    else seekBar.setProgress(5);
                }
                sendMessage(seekBarMsg + "E");
            }
        });

    }

}
