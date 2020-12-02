package com.example.mobile;

import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {
    public interface APIResultCallback<T> {
        void onAPIResult(T response);
    }

    private static DataManager dataManager;

    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    private DataManager() {
    }

    public void onAPIGetCheerInfo(String index, APIResultCallback<Object> callback) {
        new GetCheerInfo(index, callback).execute();
    }

    public void onAPIGetCheerInfoList(APIResultCallback<Object> callback) {
        new GetCheerInfoList(callback).execute();
    }

    public static class GetCheerInfo extends AsyncTask<Void, Void, Object> {
        private final String index;
        private final WeakReference<APIResultCallback<Object>> callBack;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HashMap<Boolean, Model.Cheer> doInBackground(Void... params) {

            HashMap<Boolean, Model.Cheer> result = new HashMap<>();

            try {

                HashMap<Boolean, JSONObject> response;

                String httpURL = Helper.onMakeURL(
                        PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_IP),
                        PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_PORT),
                        Constants.URL_GETCHEERINFO);

                Uri.Builder urlBuilder = Uri.parse(httpURL).buildUpon();
                urlBuilder.appendQueryParameter("index", index);

                URL url = new URL(urlBuilder.toString());
                //HTTP Get
                response = Helper.onHttpGET(url);

                JSONObject resultObj = response.get(true);
                if (resultObj == null) {
                    result.put((Boolean) response.keySet().toArray()[0], null);
                } else {

                    Model.Cheer cheerModel = new Model.Cheer();

                    Model.SerializeModel.CheerItemInfo cheerItem = new Gson().fromJson(resultObj.toString(), Model.SerializeModel.CheerItemInfo.class);

                    String[] indexs = cheerItem.item.index.split("\\.");

                    cheerModel.x = Integer.valueOf(indexs[0]);
                    cheerModel.y = Integer.valueOf(indexs[1]);
                    cheerModel.color = cheerItem.item.color;

                    result.put(true, cheerModel);
                }

                return result;

            } catch (InternalError err) {
                result.put(false, null);
            } catch (MalformedURLException e) {
                result.put(false, null);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object obj) {
            super.onPostExecute(obj);

            callBack.get().onAPIResult(obj);
        }

        public GetCheerInfo(String index, APIResultCallback<Object> callBack) {
            this.index = index;
            this.callBack = new WeakReference<>(callBack);
        }
    }

    public static class GetCheerInfoList extends AsyncTask<Void, Void, Object> {
        private final WeakReference<APIResultCallback<Object>> callBack;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected HashMap<Boolean, List<Model.Cheer>> doInBackground(Void... params) {

            HashMap<Boolean, List<Model.Cheer>> result = new HashMap<>();
            try {

                HashMap<Boolean, JSONObject> response;

                String httpURL = Helper.onMakeURL(
                        PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_IP),
                        PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_PORT),
                        Constants.URL_GETCHEERINFOLIST);

                Uri.Builder urlBuilder = Uri.parse(httpURL).buildUpon();

                URL url = new URL(urlBuilder.toString());
                //HTTP Get
                response = Helper.onHttpGET(url);

                JSONObject resultObj = response.get(true);
                if (resultObj == null) {
                    result.put((Boolean) response.keySet().toArray()[0], null);
                } else {

                    List<Model.Cheer> cheerModelList = new ArrayList<>();

                    Model.SerializeModel.CheerItemList cheerItemList = new Gson().fromJson(resultObj.toString(), Model.SerializeModel.CheerItemList.class);

                    for (Model.SerializeModel.CheerItem item : cheerItemList.items) {


                        Model.Cheer model = new Model.Cheer();

                        if (item.index.equals("x")) {
                            model.xCount = Integer.valueOf(item.color);
                        } else if (item.index.equals("y")) {
                            model.yCount = Integer.valueOf(item.color);
                        } else {

                            String[] indexs = item.index.split("\\.");

                            model.x = Integer.valueOf(indexs[0]);
                            model.y = Integer.valueOf(indexs[1]);
                            model.color = item.color;
                        }

                        cheerModelList.add(model);

                    }

                    result.put(true, cheerModelList);
                }


                return result;

            } catch (InternalError err) {
                result.put(false, null);
            } catch (MalformedURLException e) {
                result.put(false, null);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Object obj) {
            super.onPostExecute(obj);

            callBack.get().onAPIResult(obj);
        }

        public GetCheerInfoList(APIResultCallback<Object> callBack) {
            this.callBack = new WeakReference<>(callBack);
        }
    }
}


