package com.master_igor.findme;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class ServerAPIHandler implements Runnable/*extends AsyncTask <URL, Integer, Long>*/ {

    private URL url;

    //    public JSONArray jsonArray;
    public String serverMessage = "";

    public ServerAPIHandler(URL url) {
        this.url = url;
    }

    public ServerAPIHandler() {
    }

    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();

        try {
            HttpGet httpGet = new HttpGet(String.valueOf(url));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            serverMessage = client.execute(httpGet, responseHandler);
//            jsonArray = new JSONArray(setServer);
            Log.d("SetServer", "getting" + serverMessage);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerMessage() {
        return serverMessage;
    }
}
