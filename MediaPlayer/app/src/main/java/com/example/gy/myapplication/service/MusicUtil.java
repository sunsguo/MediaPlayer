package com.example.gy.myapplication.service;

/**
 这是专门处理播放，暂停等操做的类
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.example.gy.myapplication.bean.Music;

import java.io.IOException;
import java.util.List;

/*
这是一个专门用于处理播放暂停等操作的类
 */

public class MusicUtil {

    private List<String> musics = null;

    private MediaPlayer mediaPlayer;    //系统提供的播放其

    private Context context;


    public static int index = 0;

    public boolean isPlaying = false;   //用于记录播放状态

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent intent = new Intent(MusicService.CAST_ACTION_UPDATE);
            intent.putExtra(MusicService.KEY_MUSIC_INDEX,index);
            intent.putExtra(MusicService.MUSIC_TIME_CURR,mediaPlayer.getCurrentPosition());
            intent.putExtra(MusicService.MUSIC_TIME_TOTAL,mediaPlayer.getDuration());

            //发送一条广播，通过前台界面，跟新播放状态
            context.sendBroadcast(intent);

            //hander自己给自己发消息，以达到没500ms发送一条的效果
            handler.sendEmptyMessageDelayed(1,500);
        }
    };

    public MusicUtil(Context comtext, List<String> musics){
        mediaPlayer = new MediaPlayer();
        this.context = comtext;
        this.musics = musics;

        //设置当前播放完成后的监听事件
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    public void play(){
        mediaPlayer.start();
        isPlaying = true;

        handler.sendEmptyMessage(1);
    }

    /*
    播放制定位置的音乐
     */
    public void play(int index){
        MusicUtil.index = index;

       if(musics != null){
           String music = musics.get(MusicUtil.index);
           try{
               mediaPlayer.reset();

               //从网络读取文件
               mediaPlayer.setDataSource(context, Uri.parse(music));
               mediaPlayer.prepare();
               mediaPlayer.start();

               isPlaying = true;

               //让Handler去发送一个消息
               handler.sendEmptyMessage(0);

           }catch (IllegalArgumentException ee){
               ee.printStackTrace();
           }catch (SecurityException e2){
               e2.printStackTrace();
           }catch (IllegalStateException e3){
               e3.printStackTrace();
           }catch (IOException e){
               e.printStackTrace();
           }
       }
    }

    /*
    停止方法
     */
    public void stop(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            isPlaying = false;
        }
    }

    /*
    暂停方法
     */
    public void pause(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    /*
    下一曲
     */
    public void next(){
        if(musics != null){
            if(index == musics.size() -1){
                index = 0;
            }else {
                ++index;
            }
            play(index);
        }
    }
    /*
    上一曲
     */
    public void prev(){
        if(musics != null){
            if(index == 0){
                index = musics.size()-1;
            }else {
                index--;
            }
            play(index);
        }
    }
    /*
    从制定位置播放
     */
    public void play_time(int postion){
        if(musics != null){
            try{
                mediaPlayer.seekTo(postion);
            }catch (IllegalArgumentException ee){
                ee.printStackTrace();
            }catch (SecurityException e2){
                e2.printStackTrace();
            }catch (IllegalStateException e3){
                e3.printStackTrace();
            }
        }
    }

}
