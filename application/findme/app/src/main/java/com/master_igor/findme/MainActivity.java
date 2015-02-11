package com.master_igor.findme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;


public class MainActivity extends Activity {

    private static String appId = "4777396";
    private static String tokenKey = "5E27kyO4tAKdJdUaVy67";
    private static String[] sMyScope = new String[]{VKScope.FRIENDS, VKScope.MESSAGES,
            VKScope.STATUS, VKScope.WALL, VKScope.NOHTTPS};


    VKSdkListener listener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {

        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {

        }

        @Override
        public void onAccessDenied(VKError authorizationError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VKSdk.initialize(listener, appId, VKAccessToken.tokenFromSharedPreferences(this, tokenKey));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VKSdk.authorize(sMyScope);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*VK methods*/

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }
}
