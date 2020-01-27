package com.example.invinityapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.invinityapp.inventoryitems.InfinityDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class GetSitting extends AsyncTask<List<String>,Void,List<String>> {




    @Override
    protected ArrayList<String> doInBackground(List<String>... lists) {

        InfinityDB db = new InfinityDB(MainActivity.This);
        Cursor r = db.ExbordSetting();
        r.moveToFirst();

        String urls[] =  new String[]{"http://"+r.getString(7)+"/api/InfinityRetail/ExpiryState",
                "http://"+r.getString(7)+"/api/InfinityRetail",
                "http://"+r.getString(7)+"/api/InfinityRetail/GetAllBranchs",
                "http://"+r.getString(7)+"/api/InfinityRetail/GetAllBranchLocations"};


            HttpURLConnection urlConnection;
            InputStream inputStream;
            BufferedReader bufferedReader;
            URL urlIfSup;
            ArrayList<String> result = new ArrayList<>();
            try {

                // Check If It's Support Expired Data OR Not

                for (int i = 0; i < 4; i++){ // connect using urls array

                    urlIfSup = new URL(urls[i]);
                    urlConnection = (HttpURLConnection) urlIfSup.openConnection();
                    inputStream = urlConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));

                    String data = bufferedReader.readLine();

                    while(bufferedReader.readLine() != null){

                        data += bufferedReader.readLine();
                    }
                    if (i==0) { // for the first url // check if it's support expired date or not

                        result.add(data);

                    }else if (i==1){ // the second url // get the names of prices using json


                        JSONArray jsonArray = new JSONArray(data);
                        for (int legnht = 0; legnht < jsonArray.length(); legnht++){

                            JSONObject jsonObject = (JSONObject) jsonArray.get(legnht);
                            result.add(jsonObject.getString("ConfigurationValue"));

                        }

                        urlConnection.disconnect();
                        inputStream.close();
                        bufferedReader.close();

                    }else if(i == 2){

                        db.DELETE_ALL_BARNCHES();

                        JSONArray jsonArray = new JSONArray(data);
                        for (int legnht = 0; legnht < jsonArray.length(); legnht++){

                            JSONObject jsonObject = (JSONObject) jsonArray.get(legnht);
                            ContentValues values = new ContentValues();

                            values.put("BranchID_PK", jsonObject.getString("BranchID_PK"));
                            values.put("BranchName", jsonObject.getString("BranchName"));

                            if(jsonObject.getString("IsCurrentBranch").compareTo("true") == 0){

                                values.put("IsCurrentBranch", 1);

                            }else{

                                values.put("IsCurrentBranch", 0);

                            }

                            db.insertBranchs(values);
                            values.clear();
                        }

                    }else if(i == 3){

                        JSONArray jsonArray = new JSONArray(data);
                        for (int legnht = 0; legnht < jsonArray.length(); legnht++){

                            JSONObject jsonObject = (JSONObject) jsonArray.get(legnht);
                            ContentValues values = new ContentValues();

                            values.put("LocationID_PK", jsonObject.getString("LocationID_PK"));
                            values.put("BranchID_FK", jsonObject.getString("BranchID_FK"));
                            values.put("LocationName", jsonObject.getString("LocationName"));

                            db.insertLocations(values);
                            values.clear();

                        }

                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return result;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);

        SyncDataActivity.getSetting((ArrayList<String>) strings);
    }
}
