package com.jinlong.videodemo;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
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

    private ExecutorService mExecutorService;//单线程池
    private Handler mMainHandler;//主线成的Handler
    private File mAudioFile;//音频保存的位置
    private Long mStartRecordTime, mEndRecordTime;//开始录制时间和结束录制时间
    private MediaRecorder mMediaRecorder;

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
        mBtnStart.setText("松手就可以停止录音");

        //开始录音
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //释放之前存在的资源,因为这里是按下的时候就开始录音，所以按下的时候一定要先释放相应的资源
                releaseResources();
                if (!doStart()) {//不成功弹Toast提示用户
                    ToastFail();//提示用户
                }
            }
        });
    }

    /**
     * 提示用户失败信息
     */
    private void ToastFail() {
        mAudioFile = null;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileActivity.this, "录制音频失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 开始录音是否成功,
     * 所以所有的逻辑就直接在这里去写了
     */
    private boolean doStart() {
        /*
         * 1.创建MediaRecorder对象
         * 2.创建相应的保存文件
         * 3.配置相应的MediaRecorder
         * 4.开始录音
         */
        try {
            //创建mediaRecorder对象
            mMediaRecorder = new MediaRecorder();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //创建保存的文件
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Video/" + System.currentTimeMillis() + ".m4a";
                mAudioFile = new File(path);
                mAudioFile.getParentFile().mkdirs();
                mAudioFile.createNewFile();

                /*
                 * 重点来了，配置MediaRecorder
                 */
                //配置采集方式，这里用的是麦克风的采集方式
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //配置输出方式，这里用的是MP4，
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                //配置采样频率，频率越高月接近原始声音，Android所有设备都支持的采样频率为44100
                mMediaRecorder.setAudioSamplingRate(44100);
                //配置文件的编码格式,AAC是比较通用的编码格式
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                //配置码率，这里一般通用的是96000
                mMediaRecorder.setAudioEncodingBitRate(96000);
                //配置录音文件的位置
                mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());

                //开始录制音频
                mMediaRecorder.prepare();//准备
                mMediaRecorder.start();//开始录音

                /*因为这里要做相应的时间判断，所以要做时间的记录*/
                mStartRecordTime = System.currentTimeMillis();

            } else {
                //因为这里SD卡没有挂在，所以就直接返回false，如果你真的想在项目中使用的话，需要判断内存大小什么的，这里为了简便就没有写，
                //但是现在基本上都没事，因为手机内容都很大，但是如果做项目的话这些都要做的！
                return false;
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 停止录音
     */
    private void stopRecord() {
        mBtnStart.setText("按下开始录音");
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {

                if (!doStop()) {
                    ToastFail();
                }
                //这里停止后应该，释放相应的资源
                releaseResources();
            }
        });
    }

    /**
     * 停止录音
     */
    private boolean doStop() {
        try {
            //这里说处理停止播放的逻辑
            mMediaRecorder.stop();

            //因为这里要判断相应的时间,如果大于三秒就直接保存，否则删除文件
            mEndRecordTime = System.currentTimeMillis();
            final int time = (int) ((mEndRecordTime - mStartRecordTime) / 1000);
            if (time > 3) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String des = mTvShow.getText() + "录制文件成功" + time + "秒";
                        mTvShow.setText(des);
                    }
                });
            } else {
                if (mAudioFile.exists()) {
                    mAudioFile.delete();
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 释放资源
     */
    private void releaseResources() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        * 我的想法是，在这里直接停止响应的音频录制，
        * 因为在你按住HOME键的时候会走这个方法，直接停止就可以了
        * 走停止的方法，其实你也是可以监听HOME键的点击事件的，其他的随你吧！
        * 我是在这里处理的，嘻嘻*/
        stopRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在主线程关闭的时候一定要停止线程，避免内存泄露
        mExecutorService.shutdownNow();
    }
}
