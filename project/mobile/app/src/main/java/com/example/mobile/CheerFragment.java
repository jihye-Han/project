package com.example.mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;

public class CheerFragment extends Fragment implements DataManager.APIResultCallback<Object> {

    private TextView fragment_cheerText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cheer, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragment_cheerText = view.findViewById(R.id.fragment_cheerText);

        EventBusService.getInstance().onSubscribe(this);

        if (Global.scanData == null) {
            fragment_cheerText.setBackgroundColor(Color.parseColor("#FFFFFF"));
            fragment_cheerText.setText("스캔 정보가 없습니다");
        } else {
            String[] index = Global.scanData.split("\\.");

            String getIndex = index[0] + "." + index[1];

            DataManager.getInstance().onAPIGetCheerInfo(getIndex, this);
        }


        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onUpdateFloatingActionButton(false);
    }

    @Override
    public void onDestroyView() {

        EventBusService.getInstance().onUnsubscribe(this);

        super.onDestroyView();
    }

    @Subscribe
    public void onSocketEvent(SocketEvent.RefreshEvent refresh) {
        if (refresh.isUpdated) {

            String[] index = Global.scanData.split("\\.");

            String getIndex = index[0] + "." + index[1];

            DataManager.getInstance().onAPIGetCheerInfo(getIndex, this);
        }
    }

    @Override
    public void onAPIResult(Object response) {

        HashMap<Boolean, Object> hashMapObj = (HashMap<Boolean, Object>) response;

        Boolean result = (Boolean) hashMapObj.keySet().toArray()[0];
        if (result == false) {

            fragment_cheerText.setBackgroundColor(Color.parseColor("#FFFFFF"));
            fragment_cheerText.setText("서버 정보가 없습니다");
            return;
        }

        Model.Cheer cheerModel = (Model.Cheer) hashMapObj.get(true);

        fragment_cheerText.setBackgroundColor(Color.parseColor("#" + cheerModel.color));
        fragment_cheerText.setText("");
    }
}