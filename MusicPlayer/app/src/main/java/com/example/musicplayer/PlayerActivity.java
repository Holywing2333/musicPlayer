package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerActivity extends AppCompatActivity {
    //定义对象
    private TextView title;
    private TextView name;
    private TextView duration;
    private TextView currentTime;
    private SeekBar controlTime;
    private Cursor cursor;
    private ImageButton btn_play;
    private ImageButton btn_stop;
    //private ImageButton btn_next;
    //private ImageButton btn_last;
    private ImageButton btn_forward;
    private ImageButton btn_backward;
    private boolean buttonChange=true;
    private boolean reload=true;
    public String url;
    public int myduration;
    public static final int UPDATE_UI = 1;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();     //控件初始化
        initData();     //数据初始化
        btn_play.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
             if(buttonChange) {          //播放功能
                 File file=new File(url);
                 if(file.exists()&&file.length()>0)
                 {
                     if(reload) {
                         try {
                             mediaPlayer = new MediaPlayer();
                             mediaPlayer.setDataSource(url);
                             mediaPlayer.prepare();
                             reload = false;


                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                         mediaPlayer.start();
                         ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                         buttonChange = false;
                         return;

                 }
             }
                if(!buttonChange) {     //暂停功能
                    mediaPlayer.pause();
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    buttonChange=true;
                    return;
                }
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {        //停止功能
            @Override
            public void onClick(View view) {    //停止功能
                mediaPlayer.stop();
                buttonChange=true;
                btn_play.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                mediaPlayer.release();
                mediaPlayer=null;
                reload=true;
            }
        });

        //倒退功能
        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null) {
                    if (mediaPlayer.getCurrentPosition() > 5000) {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                    }
                }
            }
        });
        final CountDownTimer continuousBackward=new CountDownTimer(100000,100) {//长按连续触发实现持续倒退
            @Override
            public void onTick(long millisUntilFinished) {
                if(mediaPlayer!=null) {
                    if (mediaPlayer.getCurrentPosition() > 3000) {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                    }
                }
            }

            @Override
            public void onFinish() {

            }

        };
        btn_backward.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mediaPlayer!=null) {
                    mediaPlayer.pause();
                    continuousBackward.start();
                }
                return false;
            }
        });
        btn_backward.setOnTouchListener(new View.OnTouchListener() {        //抬手停止
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_UP&&mediaPlayer!=null){
                    continuousBackward.cancel();
                    mediaPlayer.start();
                }
                return false;
            }
        });


        //快进功能
        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null) {
                    if (myduration - mediaPlayer.getCurrentPosition() > 5000) {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                    }
                }
            }
        });
        final CountDownTimer continuousForward=new CountDownTimer(100000,100) {//长按连续触发实现持续快进
            @Override
            public void onTick(long millisUntilFinished) {
                if(mediaPlayer!=null) {
                    if (myduration - mediaPlayer.getCurrentPosition() > 3000) {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                    }
                }
            }

            @Override
            public void onFinish() {

            }

        };
        btn_forward.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mediaPlayer!=null) {
                    mediaPlayer.pause();
                    continuousForward.start();
                }
                return false;
            }
        });
        btn_forward.setOnTouchListener(new View.OnTouchListener() {        //抬手停止，同上
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_UP&&mediaPlayer!=null){
                    continuousForward.cancel();
                    mediaPlayer.start();
                }
                return false;
            }
        });

        //进度条控制
        controlTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int process = seekBar.getProgress();
                if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(process);
                }
            }
        });
    }
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==UPDATE_UI) {
                int position = mediaPlayer.getCurrentPosition();
                int total_time = mediaPlayer.getDuration();

                controlTime.setMax(total_time);
                controlTime.setProgress(position);

                updateTime(currentTime,position);

                handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
            }
        }
    };

    private void initView() {
        title=findViewById(R.id.title);
        name=findViewById(R.id.name);
        duration=findViewById(R.id.total_time);
        controlTime=findViewById(R.id.seek_bar);
        btn_play=findViewById(R.id.start);
        btn_stop=findViewById(R.id.stop);
        //btn_next=findViewById(R.id.next);
        //btn_last=findViewById(R.id.last);
        btn_forward=findViewById(R.id.forward);
        btn_backward=findViewById(R.id.backward);
    }
    private void initData() {
        int position=getIntent().getIntExtra("myposition",0);
        cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        cursor.moveToPosition(position);
        String mytitle=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        String myname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        myduration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        url=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        title.setText(mytitle);
        name.setText(myname);
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
        String songTime=sdf.format(new Date(myduration));
        duration.setText(String.valueOf(songTime));
    }

    private void updateTime(TextView textView,int time)     //更新时间的方法
    {
        int hh = time/1000 / 3600;
        int mm = time/1000 % 3600 / 60;
        int ss = time/1000 % 60;
        String now_time = null;
        if(hh!=0)
        {
            now_time = String.format("%02d:%02d:%02d",hh,mm,ss);
        }
        else
        {
            now_time = String.format("%02d:%02d",mm,ss);
        }
        currentTime.setText(now_time);
    }

    @Override
    protected void onDestroy() {//结束时回收资源
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }


}
