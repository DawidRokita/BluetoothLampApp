package com.example.lamp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class settings extends AppCompatActivity {

    private TextView btDeviceTextViev;
    private Button select_btn, save_btn;
    public ListView listView;
    public BluetoothAdapter btAdapter;
    private EditText DiscoMsgPT, OnMsgPT, OffMsgPT;

    SharedPreferences sp;
    String btDevice, theme, tmpName, OnMsgStr, OffMsgStr, DiscoMsgStr;
    String[] strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //ukryj top bar aplikacji
        setContentView(R.layout.activity_settings);

        setupUIViews();
        getAllSettings();

        sp = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        listView.setVisibility(View.GONE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        implementListeners();

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnMsgStr = OnMsgPT.getText().toString();
                OffMsgStr = OffMsgPT.getText().toString();
                DiscoMsgStr = DiscoMsgPT.getText().toString();
                btDevice = btDeviceTextViev.getText().toString();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("btDevice", btDevice);
                editor.putString("OnMsg", OnMsgStr);
                editor.putString("OffMsg", OffMsgStr);
                editor.putString("DiscoMsg", DiscoMsgStr);
                editor.commit();
                Toast.makeText(settings.this, "Zapisano ustawienia", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

    }

    private void setupUIViews(){
        listView = (ListView) findViewById(R.id.ListView);
        select_btn = (Button) findViewById(R.id.select_btn);
        btDeviceTextViev = (TextView) findViewById(R.id.btDeviceTextViev);
        save_btn = (Button) findViewById(R.id.save_btn);
        DiscoMsgPT = (EditText) findViewById(R.id.DiscoMsgPT);
        OnMsgPT = (EditText) findViewById(R.id.OnMsgPT);
        OffMsgPT = (EditText) findViewById(R.id.OffMsgPT);
    }

    int licznik = 0;
    private void implementListeners() {

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(licznik==0){
                    listView.setVisibility(View.VISIBLE);
                    licznik=1;
                }else{
                    listView.setVisibility(View.GONE);
                    licznik=0;
                }

                Set<BluetoothDevice> bt= btAdapter.getBondedDevices();
                strings = new String[bt.size()];
                int index = 0;

                if(bt.size()>0){
                    for(BluetoothDevice device : bt){
                        strings[index] = device.getName() + "/" + device.getAddress();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = strings[position].toString();
                String[] parts = temp.split("/");
                btDeviceTextViev.setText(parts[1]);
            }
        });
    }

    private void getAllSettings(){
        try {
            SharedPreferences sp2 = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
            tmpName = sp2.getString("btDevice", "");
            theme = sp2.getString("theme", "");
            OnMsgStr = sp2.getString("OnMsg", "");
            OffMsgStr = sp2.getString("OffMsg", "");
            DiscoMsgStr = sp2.getString("DiscoMsg", "");
            btDeviceTextViev.setText(tmpName);
            OnMsgPT.setText(OnMsgStr);
            OffMsgPT.setText(OffMsgStr);
            DiscoMsgPT.setText(DiscoMsgStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToMainActivity(){
        Intent intent = new Intent(this, control.class);
        startActivity(intent);
    }
}

