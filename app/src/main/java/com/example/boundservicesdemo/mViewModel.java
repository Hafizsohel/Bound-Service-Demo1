package com.example.boundservicesdemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class mViewModel extends ViewModel {
    private static final String TAG = "mViewModel";
    private MutableLiveData<Boolean>mIsProgressUpdating= new MutableLiveData<>();
    private MutableLiveData<App.MyBinder>mBinder= new MutableLiveData<>();

    private ServiceConnection serviceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: connected to service.");
            App.MyBinder binder= (App.MyBinder)  iBinder;
            mBinder.postValue(binder);
            
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mBinder.postValue(null);

        }
    };


    public LiveData<Boolean>getIsProgressUpdating(){
        return mIsProgressUpdating;
    }
    public LiveData<App.MyBinder>getBinder(){
        return mBinder;
    }
    public ServiceConnection getServiceConnection(){
        return serviceConnection;

    }
    public void setIsUpdating(Boolean isUpdating){
        mIsProgressUpdating.postValue(isUpdating);
    }
}
