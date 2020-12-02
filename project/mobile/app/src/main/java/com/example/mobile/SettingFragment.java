
package com.example.mobile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.otto.Subscribe;

public class SettingFragment extends Fragment {

    private EditText fragment_settingServerIpEdit;
    private EditText fragment_settingServerPortEdit;
    private EditText fragment_settingWebSocketEdit;

    private InputMethodManager inputMethodManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragment_settingServerIpEdit = view.findViewById(R.id.fragment_settingServerIpEdit);
        fragment_settingServerPortEdit = view.findViewById(R.id.fragment_settingServerPortEdit);
        fragment_settingWebSocketEdit = view.findViewById(R.id.fragment_settingWebSocketEdit);

        String ip = PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_IP);
        String port = PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_PORT);

        fragment_settingServerIpEdit.setText(ip);
        fragment_settingServerPortEdit.setText(port);

        if (SocketService.getInstance().onGetSocketStatus()) {
            fragment_settingWebSocketEdit.setText("연결 성공");
        } else {
            fragment_settingWebSocketEdit.setText("연결 실패");
        }

        inputMethodManager = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);

        EventBusService.getInstance().onSubscribe(this);

        view.findViewById(R.id.fragment_settingSaveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreferenceService.getInstance().onSaveServer(fragment_settingServerIpEdit.getText().toString(), fragment_settingServerPortEdit.getText().toString());

                String socketURL = Helper.onMakeURL(
                        PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_IP),
                        PreferenceService.getInstance().onGetPreferences(Constants.PREF_SERVER_PORT),
                        "");

                Global.socketUrl = socketURL;
                SocketService.getInstance().onReconnectSocket();
                fragment_settingWebSocketEdit.setText("연결 실패");

                inputMethodManager.hideSoftInputFromWindow(fragment_settingServerPortEdit.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(fragment_settingServerIpEdit.getWindowToken(), 0);

                Toast.makeText(getContext(), "저장", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.fragment_settingPrevButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SettingFragment.this).navigate(R.id.action_SettingFragment_to_HomeFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {

        inputMethodManager.hideSoftInputFromWindow(fragment_settingServerPortEdit.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(fragment_settingServerIpEdit.getWindowToken(), 0);

        EventBusService.getInstance().onUnsubscribe(this);

        super.onDestroyView();
    }

    @Subscribe
    public void SocketEvent(SocketEvent.ConnectEvent connectEvent) {

        if (connectEvent.isConnected) {
            fragment_settingWebSocketEdit.setText("연결 성공");
        } else {
            fragment_settingWebSocketEdit.setText("연결 실패");
        }
    }
}