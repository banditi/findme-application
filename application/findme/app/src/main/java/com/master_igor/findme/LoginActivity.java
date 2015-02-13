package com.master_igor.findme;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class LoginActivity extends Activity {

    private static String VK_APP_ID = "4777396";
    private static String tokenKey = "5E27kyO4tAKdJdUaVy67";


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

    private int myId;


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
                Log.d("Initialise myId", "is "+getMyId());

                String serverURL = "http://master-igor.com/findme/addid/" + getMyId();
                Log.d("Message", "Sending" + serverURL);
                try {
                    URL url = new URL(serverURL);
                    new ServerID().execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
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

        if (VKSdk.isLoggedIn()) {
            Log.d("Login", "Yes");
//            ServerID server = new ServerID(myId);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Log.d("Login", "No");
        }
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }


    private class ServerID extends AsyncTask<URL, Integer, Long> {
        private int ID;
//
//        public ServerID(int curID) {
//            this.ID = curID;
//        }

        TextView content;

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

                content.setText(setServer);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Long.decode("0");
        }
    }

}
