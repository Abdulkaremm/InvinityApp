package com.example.invinityapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invinityapp.inventoryitems.InfinityDB;

public class DeviceConfigActivity extends AppCompatActivity {

    EditText ApiIP, customerID,OnlineAPI;
    InfinityDB db;
    TextView DeviceID,getID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_config);


        db = new InfinityDB(this);
        DeviceID = findViewById(R.id.DeviceID);
        getID = findViewById(R.id.getID);
        ApiIP = findViewById(R.id.ApiIP);
        customerID = findViewById(R.id.customerid);
        OnlineAPI = findViewById(R.id.OnlineAPI);

        Cursor res = db.ExbordSetting();

        res.moveToFirst();

        ApiIP.setText(res.getString(7));
        customerID.setText(res.getString(10));
       // if(!res.getString(11).isEmpty())
            OnlineAPI.setText(res.getString(11));

        if(!res.getString(8).isEmpty()) {
            getID.setVisibility(View.INVISIBLE);
            DeviceID.setText(res.getString(8));

        }

        if (OnlineAPI.getText().toString().compareTo("") == 0)
            OnlineAPI.setHint("URL");
        res.close();
    }


    public void update(View v) {

        if (ApiIP.toString().isEmpty()) {

            Toast.makeText(this, "Please Insert IP Address", Toast.LENGTH_SHORT).show();

        } else {

            ContentValues data = new ContentValues();

            data.put("API_IP", ApiIP.getText().toString());
            db.updateSetting(data);
            Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show();


        }

    }

    public void UpdateOnlineAPI(View v){


        if (OnlineAPI.getText().toString().isEmpty()) {

            Toast.makeText(this, "الرجاء ادخال الرابط", Toast.LENGTH_SHORT).show();

        } else {

            ContentValues data = new ContentValues();

            data.put("OnlineAPI", OnlineAPI.getText().toString());
            db.updateSetting(data);
            Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show();


        }

    }

    public void updateCustomer(View v) {

        if (customerID.getText().toString().isEmpty()) {

            Toast.makeText(this, "الرجاء ادخال رقم الزبون", Toast.LENGTH_SHORT).show();

        } else {

            ContentValues data = new ContentValues();

            data.put("CustomerID", customerID.getText().toString());
            db.updateSetting(data);
            Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show();


        }

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetDeviceID(View v){

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // Requesting the permission
            ActivityCompat.requestPermissions(DeviceConfigActivity.this,
                    new String[] { Manifest.permission.READ_PHONE_STATE },
                    100);
        }
        else {

            assert telephonyManager != null;
            @SuppressLint("HardwareIds") String Id = telephonyManager.getDeviceId();
            DeviceID.setText(Id);
            ContentValues data = new ContentValues();
            data.put("deviceID", DeviceID.getText().toString());
            db.updateSetting(data);
            Toast.makeText(DeviceConfigActivity.this,
                    " Done",
                    Toast.LENGTH_SHORT)
                    .show();
            getID.setVisibility(View.INVISIBLE);

        }

    }
    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DeviceConfigActivity.this,
                        " Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(DeviceConfigActivity.this,
                        " Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bacToMain(View view){

        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        finish();
    }
}





