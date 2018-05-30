package com.jinlong.videodemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 使用文件格式录制和播放音频
 */
public class FileActivity extends AppCompatActivity {


    @BindView(R.id.tv_show)
    TextView mTvShow;
    @BindView(R.id.btn_start)
    TextView mBtnStart;
    @BindView(R.id.btn_play)
    TextView mBtnPlay;

    private ExecutorService mExecutorService;
    private Handler mMainHandler;
    private boolean isRecord;//是否开始录音

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        ButterKnife.bind(this);

        mMainHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newSingleThreadExecutor();

        //对按钮进行监听
        mBtnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //开始录音
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //停止录音
                        stopRecord();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        //更改UI的状态
        isRecord = true;
        mBtnStart.setText("松手就可以停止录音");

        //开始录音
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //释放之前存在的资源,因为这里是按下的时候就开始录音，所以按下的时候一定要先释放相应的资源
                releaseResources();
            }
        });
    }


    /**
     * 停止录音
     */
    private void stopRecord() {
        isRecord = false;
        mBtnStart.setText("按下开始录音");
    }

    /**
     * 释放资源
     */
    private void releaseResources() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在主线程关闭的时候一定要停止线程，避免内存泄露
        mExecutorService.shutdownNow();
    }
}
