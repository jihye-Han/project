package com.example.mobile;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Helper {

    public static String onMakeURL(String ip, String port, String address) {
        return "http://" + ip + ":" + port + address;
    }

    public static HashMap<Boolean, JSONObject> onHttpGET(URL url) {
        HashMap<Boolean, JSONObject> result = new HashMap<>();

        if (url == null) {
            result.put(false, null);
            return result;
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = null;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();

                JSONObject responseResult = new JSONObject(sb.toString());
                result.put(true, responseResult);

            } else {
                result.put(false, null);
            }

        } catch (MalformedURLException e) {
            result.put(false, null);

        } catch (JSONException e) {
            result.put(false, null);

        } catch (IOException e) {
            result.put(false, null);

        } catch (Exception e) {
            result.put(false, null);
        }

        return result;
    }

    public static HashMap<Boolean, JSONObject> onHttpPOST(URL url, JSONObject param) {
        HashMap<Boolean, JSONObject> result = new HashMap<>();

        if (url == null) {
            result.put(false, null);
            return result;
        }
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(onConvertJsonToString(param));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = null;

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();

                if (sb.toString().equals("")) {
                    result.put(true, param);
                } else {
                    JSONObject responseResult = new JSONObject(sb.toString());
                    result.put(true, responseResult);
                }

            } else {
                result.put(false, null);
            }

        } catch (MalformedURLException e) {
            result.put(false, null);
        } catch (JSONException e) {
            result.put(false, null);
        } catch (IOException e) {
            result.put(false, null);
        } catch (Exception e) {
            result.put(false, null);
        }

        return result;
    }

    public static String onConvertJsonToString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public static String onGetStackTrace(Throwable th) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);

        Throwable cause = th;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();

        return stacktraceAsString;
    }

    public static boolean onCheckExternalStorage() {
        boolean result = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            result = true;
        }

        return result;
    }


}
