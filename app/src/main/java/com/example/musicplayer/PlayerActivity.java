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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    private ImageButton btn_next;
    private ImageButton btn_previous;
    private ImageButton btn_forward;
    private ImageButton btn_backward;
    private RadioButton btn_sequential;
    private RadioButton btn_random;
    private RadioButton btn_cyclic;
    private RadioGroup modeChange;
    private boolean buttonChange=true;
    private boolean reload=true;
    private boolean toNext=false;
    private boolean toPrevious=false;
    private boolean toNextRandom=false;
    private boolean cyclic=false;
    private boolean cyclicMode=false;
    private boolean randomMode=false;
    private boolean sequentialMode=true;
    private boolean isSeekbarChange=false;
    private Timer timer;
    private TimerTask timerTask;
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
                         timer=new Timer();
                         timerTask=new TimerTask() {
                         @Override
                         public void run() {
                             Message msg = new Message();
                             msg.what = 1;  //1表示消息信号
                             handler.sendMessage(msg);
                         }

                     };
                         timer.schedule(timerTask,0,200);
                         return;

                 }
             }
                if(!buttonChange) {     //暂停功能
                    mediaPlayer.pause();
                    ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    buttonChange=true;
                    timer.cancel();
                    timerTask.cancel();
                    return;
                }
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {        //停止功能
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    timer.cancel();
                    timerTask.cancel();
                    mediaPlayer.stop();
                    buttonChange = true;
                    btn_play.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    mediaPlayer.release();
                    mediaPlayer = null;
                    currentTime.setText("00:00");
                    controlTime.setProgress(0);
                }
                reload=true;
            }
        });
        //上一首
        btn_previous.setOnClickListener(new View.OnClickListener() {        //上一首功能
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    timer.cancel();
                    timerTask.cancel();
                    mediaPlayer.stop();
                    buttonChange = true;
                    btn_play.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    mediaPlayer.release();
                    mediaPlayer = null;
                    currentTime.setText("00:00");
                    controlTime.setProgress(0);
                }
                reload=true;
                toPrevious=true;
                changeSong();
            }
        });
        //下一首
        btn_next.setOnClickListener(new View.OnClickListener() {        //下一首功能
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    timer.cancel();
                    timerTask.cancel();
                    mediaPlayer.stop();
                    buttonChange = true;
                    btn_play.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
                    mediaPlayer.release();
                    mediaPlayer = null;
                    currentTime.setText("00:00");
                    controlTime.setProgress(0);
                }
                reload=true;
                toNext=true;
                changeSong();
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
                }
                return false;
            }
        });
        //
        modeChange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.cyclic:
                        cyclicMode=true;
                        randomMode=false;
                        sequentialMode=false;
                        break;
                    case R.id.sequential:
                        cyclicMode=false;
                        randomMode=false;
                        sequentialMode=true;
                        break;
                    case R.id.random:
                        cyclicMode=false;
                        randomMode=true;
                        sequentialMode=false;
                        break;
                }
            }
        });

        //进度条控制
        controlTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mediaPlayer != null && mediaPlayer.isPlaying()) { //不在播放时，禁止拖动进度条
                    return false;
                }
                return true;
            }
        });

        controlTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChange=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChange=false;
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
            if(msg.what==1) {
                int position = mediaPlayer.getCurrentPosition();
                int total_time = mediaPlayer.getDuration();
                if(!isSeekbarChange) {
                    controlTime.setMax(total_time);
                    controlTime.setProgress(position);
                }
                updateTime(currentTime,position);
                //歌曲播完切换下一首
                if(position>=total_time-200)
                {
                    if(randomMode)
                        randomPlay();
                    if(sequentialMode)
                        sequentialPlay();
                    if(cyclicMode)
                        cyclicPlay();
                }
            }
        }
    };

    private void initView() {
        title=findViewById(R.id.title);
        name=findViewById(R.id.name);
        duration=findViewById(R.id.total_time);
        controlTime=findViewById(R.id.seek_bar);
        currentTime=findViewById(R.id.current_time);
        btn_play=findViewById(R.id.start);
        btn_stop=findViewById(R.id.stop);
        btn_next=findViewById(R.id.next);
        btn_previous=findViewById(R.id.previous);
        btn_forward=findViewById(R.id.forward);
        btn_backward=findViewById(R.id.backward);
        btn_cyclic=findViewById(R.id.cyclic);
        btn_random=findViewById(R.id.random);
        btn_sequential=findViewById(R.id.sequential);
        modeChange=findViewById(R.id.mode);
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
    private void changeSong()
    {
        if(toNext==true) {
            if(cursor.getPosition()!=(cursor.getCount()-1)) {
                cursor.moveToNext();
            }
            else{
                cursor.moveToPosition(0);
            }
        }

        if(toPrevious==true) {
            if(cursor.getPosition()!=0) {
                cursor.moveToPrevious();
            }
            else{
                cursor.moveToPosition(cursor.getCount()-1);
            }
        }
        if(toNextRandom)
        {
            Random rand = new Random();
            int id=rand.nextInt(cursor.getCount());
            while(id==cursor.getPosition())
            {
                id=rand.nextInt(cursor.getCount());
            }
            cursor.moveToPosition(id);
        }
        String mytitle=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        String myname=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        myduration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        url=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        title.setText(mytitle);
        name.setText(myname);
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
        String songTime=sdf.format(new Date(myduration));
        duration.setText(String.valueOf(songTime));
        toNext=false;
        toPrevious=false;
        toNextRandom=false;
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


    private void randomPlay()   //随机播放
    {
        timer.cancel();
        timerTask.cancel();
        mediaPlayer.stop();
        buttonChange = true;
        btn_play.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
        mediaPlayer.release();
        mediaPlayer = null;
        currentTime.setText("00:00");
        controlTime.setProgress(0);
        reload=true;
        toNextRandom=true;
        changeSong();
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
                ((ImageButton) btn_play).setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                buttonChange = false;
                timer=new Timer();
                timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;  //1表示消息信号
                        handler.sendMessage(msg);
                    }

                };
                timer.schedule(timerTask,0,200);
                return;

            }
        }
    }
    private void sequentialPlay()   //顺序播放
    {
        timer.cancel();
        timerTask.cancel();
        mediaPlayer.stop();
        buttonChange = true;
        btn_play.setImageDrawable(getResources().getDrawable(R.drawable.icon_play));
        mediaPlayer.release();
        mediaPlayer = null;
        currentTime.setText("00:00");
        controlTime.setProgress(0);
        reload=true;
        toNext=true;
        changeSong();
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
                ((ImageButton) btn_play).setImageDrawable(getResources().getDrawable(R.drawable.icon_pause));
                buttonChange = false;
                timer=new Timer();
                timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;  //1表示消息信号
                        handler.sendMessage(msg);
                    }

                };
                timer.schedule(timerTask,0,200);
                return;

            }
        }
    }
    private void cyclicPlay()   //单曲循环
    {
        buttonChange = true;
        mediaPlayer.seekTo(0);
        currentTime.setText("00:00");
        controlTime.setProgress(0);
    }
    @Override
    protected void onDestroy() {//结束时回收资源
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            timer.cancel();
            timerTask.cancel();
        }
        super.onDestroy();
    }


}
