package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //定义对象
    RecyclerView recyclerView;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();     //控件初始化
        initData();     //数据初始化
    }

    private void initView() {
        recyclerView=findViewById(R.id.recyclerview);
    }
    private void initData() {
        List<Music> arrlist=new ArrayList();
        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        Log.d(TAG,"initData:查询获取到的歌曲共："+cursor.getCount()+"首");
        //将歌曲添加到动态数组中
        while(cursor.moveToNext()){
            String mymusictitle=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String mysingername=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            long mysongduration=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
            String time=sdf.format(new Date(mysongduration));
           //将一行当中的数据封装到对象中
            Music music=new Music(mymusictitle,mysingername,time);
            arrlist.add(music);

        }
        cursor.close();
    //定制每一行的子布局
    //创建适配器
        MusicAdapter adapter=new MusicAdapter(arrlist);
        //让数据显示在recyclerview控件上
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
  }
}
