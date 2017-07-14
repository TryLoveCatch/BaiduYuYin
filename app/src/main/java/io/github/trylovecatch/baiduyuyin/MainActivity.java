package io.github.trylovecatch.baiduyuyin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private WakeManager mWakeManager;
    private RecognizerManager mRecognizerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWakeManager = new WakeManager(this);
        mRecognizerManager = new RecognizerManager();

        mWakeManager.startWake(new WakeManager.WakeListener() {
            @Override
            public void onWakeSuc() {
                mRecognizerManager.start(MainActivity.this);
            }

            @Override
            public void onWakeFail() {

            }

            @Override
            public void onWakeExit() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWakeManager!=null){
            mWakeManager.onDestroy();
        }

        if(mRecognizerManager!=null){
            mRecognizerManager.onDestroy();
        }
    }
}
