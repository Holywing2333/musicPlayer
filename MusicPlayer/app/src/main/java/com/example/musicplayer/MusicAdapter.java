package com.example.musicplayer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter <MusicAdapter.ViewHolder>{
    List<Music> mymusiclist;
    public MusicAdapter(List<Music> arrlist) {
        mymusiclist=arrlist;
    }
//创建viewholder实例
    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        //点击跳转
        holder.musicview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                Intent intent=new Intent(view.getContext(),PlayerActivity.class);
                intent.putExtra("myposition",position);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }
//用于对Recyclerview中子项的数据赋值
    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder holder, int position) {
        Music music=mymusiclist.get(position);
        holder.music_title.setText(music.getMusicTitle());
        holder.singer_name.setText(music.getSingerName());
        holder.song_duration.setText(music.getSongDuration());
    }
//返回recyclerview中一共有多少行数据
    @Override
    public int getItemCount() {
        return mymusiclist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View musicview;
        TextView music_title;
        TextView singer_name;
        TextView song_duration;
        public ViewHolder(@NonNull View view) {
            super(view);
            musicview=view;
            music_title=view.findViewById(R.id.music_title);
            singer_name=view.findViewById(R.id.singer_name);
            song_duration=view.findViewById(R.id.song_duration);
        }
    }
}
