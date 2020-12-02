package com.example.mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements DataManager.APIResultCallback<Object> {

    private LinearLayout cheerView;
    private TextView fragment_homeCheerMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fragment_home_cheerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_HomeFragment_to_CheerFragment);
            }
        });

        view.findViewById(R.id.fragment_home_refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().onAPIGetCheerInfoList(HomeFragment.this);
            }
        });

        view.findViewById(R.id.fragment_home_settingButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_HomeFragment_to_SettingFragment);
            }
        });

        cheerView = view.findViewById(R.id.fragment_homeCheer);
        fragment_homeCheerMessage = view.findViewById(R.id.fragment_homeCheerMessage);

        if(Global.scanData == null){

            fragment_homeCheerMessage.setText("[응원 대기]");
            fragment_homeCheerMessage.setTextColor(Color.parseColor("#CD5149"));

        }else{
            String[] position = Global.scanData.split("\\.");

            fragment_homeCheerMessage.setText(String.format("[응원 준비 완료(%s, %s)]", position[0], position[1]));
            fragment_homeCheerMessage.setTextColor(Color.parseColor("#ADD315"));
        }

        EventBusService.getInstance().onSubscribe(this);
        DataManager.getInstance().onAPIGetCheerInfoList(this);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onUpdateFloatingActionButton(true);
    }

    @Override
    public void onDestroyView() {

        EventBusService.getInstance().onUnsubscribe(this);

        super.onDestroyView();
    }

    @Subscribe
    public void onSocketEvent(SocketEvent.RefreshEvent refresh) {
        if (refresh.isUpdated) {
            DataManager.getInstance().onAPIGetCheerInfoList(this);
        }
    }

    @Subscribe
    public void onSocketEvent(SocketEvent.BarCodeEvent barcode) {

        Global.scanData = barcode.scans;

        String[] position = barcode.scans.split("\\.");

        fragment_homeCheerMessage.setText(String.format("[응원 준비 완료(%s, %s)]", position[0], position[1]));
        fragment_homeCheerMessage.setTextColor(Color.parseColor("#ADD315"));
    }

    @Override
    public void onAPIResult(Object response) {

        HashMap<Boolean, Object> hashMapObj = (HashMap<Boolean, Object>) response;

        Boolean result = (Boolean) hashMapObj.keySet().toArray()[0];
        if (result == false) {
            return;
        }

        int X = 0;
        int Y = 0;

        List<Model.Cheer> cheerModelList = (List<Model.Cheer>) hashMapObj.get(true);

        for (Model.Cheer model : cheerModelList) {

            if (model.xCount != 0) {
                X = model.xCount;
            }

            if (model.yCount != 0) {
                Y = model.yCount;
            }
        }

        cheerView.removeAllViews();

        for (int i = 0; i < X; i++) {

            LinearLayout row = new LinearLayout(getActivity());
            row.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < Y; j++) {

                String color = "#FFFFFF";

                for (Model.Cheer model : cheerModelList) {

                    if ((i + 1) == model.x && (j + 1) == model.y) {

                        color = "#" + model.color;
                        break;
                    }
                }

                TextView item = new TextView(getActivity());
                item.setText("■");
                item.setTextSize(32);
                item.setTextColor(Color.parseColor(color));
                item.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                row.addView((item));
            }

            cheerView.addView(row);
        }
    }
}