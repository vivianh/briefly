package com.vivianhhuang.briefly;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    // CLIENT_SECRET & CLIENT_ID are specific to 'briefly'
    private static String CLIENT_SECRET = "nAjTudnDNpMs2rw5UPVdtKQptb5HYAn4";
    private static String CLIENT_ID = "1391";
    private static String CODE;
    private static String ACCESS_TOKEN;
    private static String AUTHORIZE_URL = "https://api.venmo.com/oauth/authorize?client_id=" +
            CLIENT_ID + "&scope=make_payments,access_profile&response_type=code";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ACCESS_TOKEN == null) {
            getAuthorizationCode();
        } else {
            startMainActivity();
        }
    }

    public void getAuthorizationCode() {
        setContentView(R.layout.activity_login_webview);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("github")) {
                    // redirect url to github...
                    CODE = url.split("=")[1];
                    new GetAccessTokenTask().execute();
                } else {
                    view.loadUrl(AUTHORIZE_URL);
                }
                return true;
            }

        });

        webView.loadUrl(AUTHORIZE_URL);
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
                JSONTokener tokener = new JSONTokener(inputStreamToString(response.getEntity().getContent()).toString());
                JSONObject JSONresponse = new JSONObject(tokener);
                // Log.v("vivbriefly", "response dict " + JSONresponse.toString());
                ACCESS_TOKEN = JSONresponse.get("access_token").toString();
                startMainActivity();
            } catch (IOException e) {

            } catch (JSONException e) {

            }
            return null;
        }

        // converts from InputStream to StringBuilder
        private StringBuilder inputStreamToString(InputStream is) {
            String line;
            StringBuilder total = new StringBuilder();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            try {
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {

            }

            return total;
        }

    }

    // starts main app activity
    private void startMainActivity() {
        Intent i = new Intent(this, GroupActivity.class);
        startActivity(i);
    }
}