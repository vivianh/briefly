package com.vivianhhuang.briefly;

import android.app.Activity;
import android.graphics.Bitmap;
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
import org.json.JSONArray;
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

    private static String CLIENT_SECRET = "nAjTudnDNpMs2rw5UPVdtKQptb5HYAn4";
    private static String CLIENT_ID = "1391";
    private static String CODE;
    private static String AUTHORIZE_URL = "https://api.venmo.com/oauth/authorize?client_id=" +
            CLIENT_ID + "&scope=make_payments,access_profile&response_type=code";
    private JSONObject JSONresponse;
    private static String ACCESS_TOKEN;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void getAuthorizationCode(View v) {
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
                JSONTokener tokener = new JSONTokener(inputStreamToString(
                        response.getEntity().getContent()).toString());
                JSONresponse = new JSONObject(tokener);
                ACCESS_TOKEN = JSONresponse.get("access_token").toString();
            } catch (IOException e) {

            } catch (JSONException e) {

            }
            return null;
        }

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

}