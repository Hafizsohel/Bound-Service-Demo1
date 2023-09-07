package com.example.boundservicesdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //UI Components
    private ProgressBar pBar;
    private TextView textView;
    private Button button;

    //Vars
    private App app;
    private mViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pBar=findViewById(R.id.PBarID);
        textView=findViewById(R.id.TextViewID);
        button=findViewById(R.id.btn);

        viewModel= ViewModelProviders.of(this).get(mViewModel.class);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUpdates();
            }
        });

        viewModel.getBinder().observe(this, new Observer<App.MyBinder>() {
            @Override
            public void onChanged(App.MyBinder myBinder) {
                if (myBinder!=null){
                    Log.d(TAG, "onChanged: connected to service");
                    app= myBinder.getService();
                }
                else {
                    Log.d(TAG, "onChanged: unbound from service.");
                    app= null;
                }
            }
        });
        viewModel.getIsProgressUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(final Boolean aBoolean) {
                final Handler handler= new Handler();
                final Runnable runnable= new Runnable() {
                    @Override
                    public void run() {
                        if (aBoolean) {
                            if (viewModel.getBinder().getValue() != null) {
                                if (app.getProgress() == app.getMaxValue()) {
                                    viewModel.setIsUpdating(false);
                                }
                                pBar.setProgress(app.getProgress());
                                pBar.setMax(app.getMaxValue());
                                String progress =
                                        String.valueOf(100 * app.getProgress() / app.getMaxValue() + "%");
                                textView.setText(progress);
                                handler.postDelayed(this, 100);
                            }
                            else {
                                handler.removeCallbacks(this);
                            }
                        }
                    }
                };
                if (aBoolean){
                    button.setText("Pause");
                    handler.postDelayed(runnable, 100);

                }
                else {
                    if (app.getProgress()==app.getMaxValue()){
                        button.setText("Restart");
                    }
                    else {
                        button.setText("Start");

                    }
                }
            }
        });
    }

    private void toggleUpdates(){
        if (app!=null){
            if (app.getProgress()==app.getMaxValue()){
                app.resetTask();
                button.setText("Start");
            }
            else {
                if (app.getIsPaused()){
                    app.unPausePretendLongRunningTask();
                    viewModel.setIsUpdating(true);
                }
                else {
                    app.pausePretendLongRunningTask();
                    viewModel.setIsUpdating(false);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel.getBinder()!=null){
            unbindService(viewModel.getServiceConnection());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }
    private void startService(){
        Intent serviceIntent= new Intent(this, App.class);
        startService(serviceIntent);

        bindService();

    }
    private void bindService(){
        Intent serviceIntent=new Intent(this, App.class);
        bindService(serviceIntent, viewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }
}