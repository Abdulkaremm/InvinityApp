package com.example.invinityapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.inventroyscan;

public class SettingActivity extends AppCompatActivity {

    Switch usecamera;
    LinearLayout syncDate;
    private Dialog dialog;
    LinearLayout DeviceConfig;
    LinearLayout removeAllData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        DeviceConfig = findViewById(R.id.DeviceConfig);
        removeAllData = findViewById(R.id.removeAllDate);
        usecamera = findViewById(R.id.UseCamera);
        syncDate = findViewById(R.id.SyncDatePage);



        DeviceConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


        usecamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        removeAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("مسح كل البيانات")
                        .setMessage("هل انت متأكد من عملية الحدف ؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                InfinityDB db = new InfinityDB(SettingActivity.this);

                                db.DAELETE_ALL_DATA();
                                Toast.makeText(SettingActivity.this, "تمت عملية المسح", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();

            }
        });


        syncDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.This.finish();
                Intent intent = new Intent(SettingActivity.this,SyncDataActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CreateDilog();
    }





        public void configPage(View v){


         Intent intent = new Intent(this,DeviceConfigActivity.class);
         startActivity(intent);
         finish();
        }




    public void CreateDilog(){


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.pindialog);

        final EditText pin = dialog.findViewById(R.id.PIN);
        TextView yes =  dialog.findViewById(R.id.yes);
        TextView no = dialog.findViewById(R.id.no);
        final TextView error = dialog.findViewById(R.id.error);

        final InfinityDB db = new InfinityDB(this);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pin.getText().toString().isEmpty()){
                    Cursor res = db.ExbordSetting();
                    res.moveToFirst();
                    if(res.getString(9).compareTo(pin.getText().toString()) == 0){
                        Intent intent = new Intent(SettingActivity.this,DeviceConfigActivity.class);
                        startActivity(intent);
                        SettingActivity.this.finish();
                        dialog.dismiss();
                    }else {
                        error.setText("كلمة المرور غير صحيحة");
                        error.setVisibility(View.VISIBLE);
                    }
                }else {
                    error.setText("الرجاء ادخال كلمة المرور ثم اضغط تأكيد");
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

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

        MainActivity.This.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
