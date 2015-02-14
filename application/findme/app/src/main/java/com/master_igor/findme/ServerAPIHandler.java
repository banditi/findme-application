package com.master_igor.findme;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;

public class ServerAPIHandler extends AsyncTask <URL, Integer, Long> {
    @Override
    protected Long doInBackground(URL... params) {
        HttpClient client = new DefaultHttpClient();
//
//            String serverURL = "http://master-igor.com/findme/addid/" + ID;

        try {
            String setServer = "";

            HttpGet httpGet = new HttpGet(String.valueOf(params[0]));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            setServer = client.execute(httpGet, responseHandler);
            Log.d("SetServer", "getting" + setServer);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Long.decode("0");
    }
}
