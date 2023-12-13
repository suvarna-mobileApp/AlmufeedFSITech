package com.android.almufeed.ui.services;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public abstract class ConnectAndGetResponseInBackground extends AsyncTask<String, Void, String> {

    String finalJsonStr = null;
    public void setJson(String jStr) {
        finalJsonStr = jStr;
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuffer output = new StringBuffer("");
        String result = null;
        try {

            String jStrParameters = finalJsonStr;
            System.out.println("URL FINAL00 : " + jStrParameters);
            System.out.println("URL FINAL01 : " + urls[0] + jStrParameters);
            String finalURL = urls[0] + URLEncoder.encode(jStrParameters.trim(), "UTF-8");
            System.out.println("URL FINAL11 : " + finalURL);

            InputStream stream = null;
            URL url = new URL(finalURL);
            System.out.println("URL FINAL22 : " + url);

            URLConnection connection = url.openConnection();

            System.out.println("URL FINAL23 : " + url);

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");

            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(10000);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.connect();

            System.out.println("URL FINAL24 : " + httpConnection.getResponseCode() + "*-*" + url);

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                System.out.println("URL FINAL25 : " + url);
                stream = httpConnection.getInputStream();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    output.append(s);
                }
                buffer.close();

                result = output.toString();

                if (!result.contains("json")) {
                    result = null;
                }
            }

            System.out.println("URL FINAL26 : " + url);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("InExcep 3" + e);
            return result;

        }

        System.out.println("result resubmit@@@ " + result);
        return result;

    }

    @Override
    protected void onPostExecute(String output) {
        onResponseReceived(output);
        returnResult(output);
    }

    private String returnResult(String xmlStr) {
        return xmlStr;
    }

    public abstract void onResponseReceived(String result);
}

