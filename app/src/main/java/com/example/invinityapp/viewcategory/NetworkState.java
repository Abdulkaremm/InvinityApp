package com.example.invinityapp.viewcategory;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.invinityapp.R;

public class NetworkState extends AppCompatActivity {

    TextView network;
    Button trytocon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_state);

        network = (TextView) findViewById(R.id.network);
        network.setText("لا يوجد أتصال بالشبكة الرجاء التحقق من اتصالك بالشبكة و إعادة المحاولة");
        trytocon = (Button) findViewById(R.id.trytocon);

    }

    public void TryToConnect(View view){

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected == true){

            Intent intent = new Intent(this, DataOrder.class);
            startActivity(intent);
            finish();
        }else{
            network.setText("لا يوجد أتصال بالشبكة الرجاء التحقق من اتصالك بالشبكة و إعادة المحاولة");
        }
    }
}
