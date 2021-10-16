package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {
    //定义对象
    private TextView textView1;
    private Timer timer;  //创建定时器
    private TimerTask timerTask; //创建定时器任务
    private int count=3;
    private static final String TAG = "WelcomeActivity";
    private Handler handler;  //消息处理器，专门发送和接收消息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();  //控件初始化
        initData();  //数据初始化
    }

    private void initView() {
        textView1=findViewById(R.id.text1);
    }
    private void initData() {
        timer=new Timer();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                //耗时的操作都放在子线程中进行
                count--;
                Log.d(TAG, "run: " + count);
                // textView1.setText(count+"");
                if (count != 0) {
                    Message msg = new Message();
                    msg.what = 1;  //1表示消息信号
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 0;  //0表示消息信号
                    handler.sendMessage(msg);
                }
            }

        };
        //开启定时器   参数1：定时器任务   参数2：延迟     参数3：变化的周期
        timer.schedule(timerTask,0,1000);
        //主线程接受到消息信号对主界面数字显示进行更新
        handler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //主线程根据接收到的消息进行判断
                switch (msg.what){
                    case 1:
                        count--;
                        textView1.setText(count+"");  //让变化的数字显示在主界面上
                        break;
                    case 0:
                        Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        timer.cancel();
                        timerTask.cancel();
                        break;
                    default:
                        break;

                }
            }
        };
    }
}

