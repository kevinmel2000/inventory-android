package com.hartz.inventory;

/**
 * Created by hrtz on 12/24/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;

/**
 * Created by Ravi Tamada on 01/09/16.
 * www.androidhive.info
 */
public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    public static final String LINK_PPRE_CREATE = "/inventaris/public/api/ppre";
    public static final String LINK_MRMART_GET = "/inventaris/public/api/mrmart";
    public static final String LINK_SATUAN_GET = "/inventaris/public/api/satuan";
    public static final String LINK_LOGIN = "/inventaris/public/api/login";



    SharedPreferences preferences;


    public HttpHandler(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public String makePostCall(LinkedHashMap<String, Object> params, String urlAddress) throws IOException {

            String server = preferences.getString("ServerName", "");
            URL url = new URL("http://"+server+urlAddress);
            Log.v("connecting to", url.toString());
            StringBuilder postData = new StringBuilder();
            for (LinkedHashMap.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(8000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = convertStreamToString(in);
            return response;

    }

    public String makePostJSONCall(String reqUrl, JSONObject content) {

        try {
            String server = preferences.getString("ServerName", "");
            URL url = new URL("http://"+server+reqUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            //write json object to string
            wr.write(content.toString());
            wr.flush();

//display what returns the POST request

            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            } else {
                System.out.println(con.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    public String makeGetCall(String reqUrl) throws IOException {
        String response = null;
        String server = preferences.getString("ServerName", "");
        URL url = new URL("http://"+server+reqUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);

        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}