package com.example.mobile;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceService {

    private static PreferenceService preferenceService = null;
    private static Context preferenceContext = null;

    public static PreferenceService getInstance() {
        if (preferenceService == null) {
            preferenceService = new PreferenceService();
        }

        return preferenceService;
    }

    public void onSetContext(Context context) {
        preferenceContext = context;
    }

    private PreferenceService() {

    }

    public String onGetPreferences(String tag) {
        SharedPreferences sharedPreferences = preferenceContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(tag, "");

        return result;
    }

    private void onAddPreference(String tag, String data) {
        SharedPreferences sharedPreferences = preferenceContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, data);
        editor.commit();
    }

    private void onDeletePreferences(String tag) {
        SharedPreferences sharedPreferences = preferenceContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(tag);
        editor.commit();
    }

    public void onSaveServer(String ip, String port) {
        onAddPreference(Constants.PREF_SERVER_IP, ip);
        onAddPreference(Constants.PREF_SERVER_PORT, port);
    }


}
