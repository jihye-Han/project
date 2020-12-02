package com.example.mobile;


import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class EventBusService {

    /* singletom */
    private static final EventBusService ourInstance = new EventBusService();
    private static final CustomBus eventBus = new CustomBus();

    /* static factory methods */
    public static EventBusService getInstance() {
        return ourInstance;
    }

    public static void onSubscribe(Object object) {
        eventBus.register(object);
    }

    public static void onUnsubscribe(Object object) {
        eventBus.unregister(object);
    }

    public static void onPublish(Object object) {
        eventBus.post(object);
    }

    /* CustomBus */
    private static class CustomBus extends Bus {
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CustomBus.super.post(event);
                    }
                });
            }
        }
    }

    /* Constructor */
    private EventBusService() {
    }
}
