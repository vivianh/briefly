package com.vivianhhuang.briefly;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    private static String CLIENT_SECRET = "nAjTudnDNpMs2rw5UPVdtKQptb5HYAn4";
    private static String CLIENT_ID = "1391";
    private static String CODE = "1";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void getAccessToken(View v) {
        new GetAccessTokenTask().execute();
    }

    private class GetAccessTokenTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://api.venmo.com/oauth/access_token");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
                nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
                nameValuePairs.add(new BasicNameValuePair("code", CODE));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                Log.v("vivbriefly", response.toString());
            } catch (IOException e) {

            }
            return null;
        }

    }

}