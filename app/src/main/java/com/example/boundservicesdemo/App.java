package com.example.boundservicesdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class App extends Service {
    private static final String TAG = "App";
    private IBinder mBinder = new MyBinder();
    private Handler mHandler;
    private int mProgress, mMaxValue;
    private Boolean mIsPaused;
    private mViewModel viewModel;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mIsPaused = true;
        mProgress = 0;
        mMaxValue = 5000;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBinder extends Binder {
        App getService() {
            return App.this;
        }
    }

    public void startPretendLongRunningTask() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (mProgress >= mMaxValue || mIsPaused) {
                    Log.d(TAG, "run: removing callbacks.");
                    mHandler.removeCallbacks(this);
                    pausePretendLongRunningTask();
                } else {
                    Log.d(TAG, "run: progress: " + mProgress);
                    mProgress += 100;
                    mHandler.postDelayed(this, 100);

                }
            }
        };
        mHandler.postDelayed(runnable, 100);

    }

    public void pausePretendLongRunningTask() {
        mIsPaused = true;

    }

    public void unPausePretendLongRunningTask() {
        mIsPaused = false;
        startPretendLongRunningTask();

    }

    public Boolean getIsPaused() {
        return mIsPaused;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void resetTask() {
        mProgress = 0;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}

