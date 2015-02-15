package com.master_igor.findme;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.PendingIntent.getActivity;

/**
 * Created by Johnny D on 13.02.2015.
 */
public class MainActivity extends ListActivity {

    private static String VK_APP_ID = "4777396";
    private static String tokenKey = "5E27kyO4tAKdJdUaVy67";

    int userID;
    private User currentUser = new User();
    private String friendResponse;
    private final List<User> users = new ArrayList<User>();
    private ArrayAdapter<User> listAdapter;
    private Intent intent;
    public Handler handler = new Handler();
    private Runnable r = new Runnable() {

        @Override
        public void run() {
            String serverURL = "http://master-igor.com/findme/getfriends/" + currentUser.getIdvk() + "/" + currentUser.getDistance() + "/";
            try {
                //sending GET request with my own ID
                URL url = new URL(serverURL);
                ServerAPIHandler server = new ServerAPIHandler(url);
                Thread thr = new Thread(server);
                thr.start();
                thr.join();
                setFriends(server.getServerMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.postDelayed(r, 10000);
        }
    };

    MyReceiver myReceiver;

    private void sendMessageToGPService() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GPSHandler.GET_GPS);
        registerReceiver(myReceiver, intentFilter);

        //Start our own service
        intent = new Intent(MainActivity.this, GPSHandler.class);

        // You can also include some extra data.
        intent.putExtra("userID", userID);
        Log.d("here we are", "" + userID);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getIntent().getIntExtra("userID", 0);

        currentUser.setIdvk(userID);

//        if (savedInstanceState != null) {
//            // Restore value of members from saved state
//            userID = savedInstanceState.getInt("userID");
//        } else {
//            // Probably initialize members with default values for a new instance
//            Log.d("NULL_BUNDLE", "" + userID);
//        }
//        Log.d("Create", "" + userID);
        setContentView(R.layout.main);

        VKUIHelper.onCreate(this);
//
//        sendMessageToGPService();

//        registerForContextMenu(getListView());
//        handler.post(r);
    }

    void setFriends(String friendResponse) {
        users.clear();
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
                user.setImg(tempFriend.getString("img"));
//                user.setImg("https://pp.vk.me/c622228/v622228596/1d20d/DpsgmS66AP4.jpg");
                users.add(user);
                Log.d("My item: ", "lala");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listAdapter = new ArrayAdapter<User>(this, R.layout.myitem, R.id.firstLine, users) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);

                final User user = getItem(position);

                ((TextView) view.findViewById(R.id.firstLine)).setText(user.getName());

                ((TextView) view.findViewById(R.id.secondLine)).setText("Distance: " + user.getDistance() + " m");

                ImageView avatar = (ImageView) view.findViewById(R.id.avatar);

                String url_img = user.getImg();
                URL url = null;
                Bitmap bmp = null;
                try {
                    url = new URL(url_img);
                    GetImgThread server = new GetImgThread(url);
                    Thread thr = new Thread(server);
                    thr.start();
                    thr.join();
                    bmp = BitmapFactory.decodeStream(server.getBitmap());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                UrlImageView urlImg = new UrlImageView(getContext());
                avatar.setImageBitmap(bmp);

//                urlImg.setImageUrl("http://abc2.png");

//                avatar.setImageBitmap();

                return view;

            }
        };
        setListAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.actions, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.cnt_mnu_geo:
                String uriGM = String.format(Locale.ENGLISH, "google.navigation:q=%.6f,%.6f&mode=w",
                        users.get(info.position).getLatitude(),
                        users.get(info.position).getLongitude(),
                        users.get(info.position).getName());
                Intent intentGM = new Intent(Intent.ACTION_VIEW, Uri.parse(uriGM));

                intentGM.setPackage("com.google.android.apps.maps");
                if (intentGM.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentGM);
                } else {
                    Toast.makeText(this, "Warning! " + "There is no GoogleMaps in your Android", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cnt_mnu_write:
                String uriVK = String.format("vkontakte://profile/%d", users.get(info.position).getIdvk());
                Intent intentVK = new Intent(Intent.ACTION_VIEW, Uri.parse(uriVK));
                if (intentVK.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentVK);
                } else {
                    String uriVKbro = String.format("http://vk.com/write%d", users.get(info.position).getIdvk());
                    Intent intentVKbro = new Intent(Intent.ACTION_VIEW, Uri.parse(uriVKbro));
                    if (intentVKbro.resolveActivity(getPackageManager()) != null) {
                        startActivity(intentVKbro);
                    } else {
                        Toast.makeText(this, "Warning! " + "There is no Bro in your Android", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
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

        sendMessageToGPService();
        registerForContextMenu(getListView());
        handler.post(r);

//        try {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int dist = Integer.parseInt(settings.getString("max_distance", "1000"));
        currentUser.setDistance(dist);
        Log.d("Settings", "Max dist: " + currentUser.getDistance());
//        } catch (Exception e) {

//        }

        Log.d("Resume", "" + userID);
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
//        Log.d("Stop", "" + userID);
        unregisterReceiver(myReceiver);
        handler.removeCallbacks(r);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        Log.d("Destroy", "" + userID);
        super.onDestroy();
        stopService(intent);
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        try {
//            outState.putInt("userID", userID);
//            Log.d("SaveInst", "" + userID);
//        } catch (NullPointerException e) {
//
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle state) {
//        super.onRestoreInstanceState(state);
//
//        userID = state.getInt("userID");
//        Log.d("RestoreInst", "" + userID);
//    }

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

    public void openSettings(MenuItem item) {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
//        onPause();
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("friendsGPS");
            setFriends(data);
            Log.d("onReceive", data);
        }
    }
}
