package com.jinlong.videodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btn_file)
    Button mBtnFile;
    @BindView(R.id.btn_byte)
    Button mBtnByte;

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());


    }

    @OnClick({R.id.btn_file, R.id.btn_byte})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_file:
                startActivity(new Intent(MainActivity.this, FileActivity.class));
                break;
            case R.id.btn_byte:
                startActivity(new Intent(MainActivity.this, ByteActivity.class));
                break;
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
