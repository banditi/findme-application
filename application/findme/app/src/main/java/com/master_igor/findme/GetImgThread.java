package com.master_igor.findme;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class GetImgThread implements Runnable {

    public URL url;

    public InputStream bitmap;

    public GetImgThread(URL url) {
        this.url = url;
    }

    @Override
    public void run() {
        try {
            bitmap = url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getBitmap() {
        return bitmap;
    }
}