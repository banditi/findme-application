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

public class ServerAPIHandler implements Runnable/*extends AsyncTask <URL, Integer, Long>*/ {
    private URL url;

    public ServerAPIHandler(URL url) {
        this.url = url;
    }

    public ServerAPIHandler() {
    }

    @Override
    public void run() {
        HttpClient client = new DefaultHttpClient();
//
//            String serverURL = "http://master-igor.com/findme/addid/" + ID;

        try {
            String setServer = "";

            HttpGet httpGet = new HttpGet(String.valueOf(url));
//            Log.d("ServerAPI", httpGet.getURI());
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            setServer = client.execute(httpGet, responseHandler);
            Log.d("SetServer", "getting" + setServer);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
