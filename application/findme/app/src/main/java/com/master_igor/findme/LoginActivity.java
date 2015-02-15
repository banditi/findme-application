package com.master_igor.findme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.master_igor.findme.ServerAPIHandler.*;

public class LoginActivity extends Activity {

    private static String VK_APP_ID = "4777396";
    private static String tokenKey = "5E27kyO4tAKdJdUaVy67";
    Intent intent;

    private final VKSdkListener sdkListener = new VKSdkListener() {

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onAcceptUserToken " + token);
            startLoading();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("VkDemoApp", "onReceiveNewToken " + newToken);
            startLoading();
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onRenewAccessToken " + token);
            startLoading();
        }

        @Override
        public void onCaptchaError(VKError captchaError) {
            Log.d("VkDemoApp", "onCaptchaError " + captchaError);
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.d("VkDemoApp", "onTokenExpired " + expiredToken);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            Log.d("VkDemoApp", "onAccessDenied " + authorizationError);
        }

    };
    private Button loginButton;

    public int getMyId() {
        return myId;
    }

    public int myId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        VKSdk.initialize(sdkListener, VK_APP_ID, VKAccessToken.tokenFromSharedPreferences(this, tokenKey));

        VKUIHelper.onCreate(this);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKSdk.authorize(VKScope.FRIENDS);
            }
        });

        if (VKSdk.wakeUpSession()) {
            startLoading();

        } else {
            loginButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    private void startLoading() {

        VKRequest requestMe = VKApi.users().get();
        requestMe.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                setMyId(user.getId());

                String serverURL = "http://master-igor.com/findme/addid/" + getMyId();

                intent = new Intent(LoginActivity.this, MainActivity.class);
                Log.d("userID in intent", "ID: " + getMyId());
                intent.putExtra("userID", getMyId());
                Log.d("Message", "Sending " + serverURL);
                try {
                    //sending GET request with my own ID
                    URL url = new URL(serverURL);
                    ServerAPIHandler server = new ServerAPIHandler(url);
                    Thread thr = new Thread(server);
                    thr.start();
                    thr.join();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (VKSdk.isLoggedIn()) {
                    Log.d("Login", "Yes");
                    loginButton.setVisibility(View.GONE);
//                    sendMessageToGPService();
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("Login", "No");
                }
            }

            @Override
            public void onError(VKError error) {
                //Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                //I don't really believe in progress
            }
        });


    }

    public void setMyId(int myId) {
        this.myId = myId;
    }


}
