package com.master_igor.findme;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny D on 13.02.2015.
 */
public class MainActivity extends ListActivity {

    private static String VK_APP_ID = "4777396";
    private static String tokenKey = "5E27kyO4tAKdJdUaVy67";

    int userID;
    private String friendResponse;
    private final List<User> users = new ArrayList<User>();
    private ArrayAdapter<User> listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // TODO:: check and be sure that it doesnt needed anymore with VkSdklistener
        VKSdk.initialize(sdkListener, VK_APP_ID, VKAccessToken.tokenFromSharedPreferences(this, tokenKey));

        userID = getIntent().getIntExtra("userID", 0);
        friendResponse = getIntent().getStringExtra("friends");
        try {
            JSONObject dataJSON = new JSONObject(friendResponse);
            JSONArray friends = dataJSON.getJSONArray("items");
            int count = friends.length();
            for (int i = 0; i < count; i++) {
                JSONObject tempFriend = friends.getJSONObject(i);
                User user = new User(tempFriend.getString("name"));
                user.setDistance(tempFriend.getInt("dist"));
                user.setIdvk(tempFriend.getInt("idvk"));
                user.setLatitude(tempFriend.getDouble("lat"));
                user.setLongitude(tempFriend.getDouble("lng"));
                users.add(user);
                Log.d("My item: ", "lala");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.d("Getting friends in MAIN",(VKUsersArray) friendResponse.parsedModel);
        setContentView(R.layout.main);
        listAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_2, android.R.id.text1, users) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                final User user = getItem(position);

                ((TextView) view.findViewById(android.R.id.text1)).setText(user.getName());

                ((TextView) view.findViewById(android.R.id.text2)).setText("distance: " + user.getDistance() + " m");
                return view;

            }
        };
        setListAdapter(listAdapter);

        startService(new Intent(this, GPSHandler.class));

        VKUIHelper.onCreate(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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

    private final VKSdkListener sdkListener = new VKSdkListener() {

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onAcceptUserToken " + token);
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("VkDemoApp", "onReceiveNewToken " + newToken);
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onRenewAccessToken " + token);
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

    public void signOut(MenuItem item) {
        Log.d("Logout", "main activity");
        String serverURL = "http://master-igor.com/findme/setoffline/" + userID;
        try {
            //sending GET request about offline with my own ID
            URL url = new URL(serverURL);
            Thread thr = new Thread(new ServerAPIHandler(url));
            thr.start();
            thr.join();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VKSdk.logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void closeApp(MenuItem item) {
        String serverURL = "http://master-igor.com/findme/setoffline/" + userID;
        Log.d("URL to server", "Offline status " + serverURL);
        try {
            //sending GET request about offline with my own ID
            URL url = new URL(serverURL);
            Thread thr = new Thread(new ServerAPIHandler(url));
            thr.start();
            thr.join();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onDestroy();
        finish();
        System.exit(0);
    }
}
