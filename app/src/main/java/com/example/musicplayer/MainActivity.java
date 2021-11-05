package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 定义对象
    RecyclerView recyclerView;
    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
    }

    private void permission() {
        //This method ask for permission to user if it is not already given by user.
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
        else
        {
            //Here User already give permission to app for playing the music.
            initView();     // 控件初始化
            initData();     // 数据初始化
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Do whatever you want permission related ..
                initView();     // 控件初始化
                initData();     // 数据初始化
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview);
    }

    private void initData() {
        List<Music> arrlist = new ArrayList();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        Log.d(TAG,"initData:查询获取到的歌曲共："+cursor.getCount()+"首");
        // 将歌曲添加到动态数组中
        while(cursor.moveToNext()) {
            String mymusictitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String mysingername = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            long mysongduration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            String mymusicurl = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));    // 文件路径
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(mysongduration));
            int isMusic = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
            if (isMusic != 0) { // 只把音乐添加到集合当中
                // 将一行当中的数据封装到对象中
                Music music=new Music(mymusictitle,mysingername,time);
                arrlist.add(music);
            }
        }
        cursor.close();
        // 定制每一行的子布局
        // 创建适配器
        MusicAdapter adapter=new MusicAdapter(arrlist);
        // 让数据显示在recyclerview控件上
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}