package com.example.gy.myapplication.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by gy on 2016/10/27.
 */

public class MusicService extends Service {

    //指令
    public static final String KEY_COMMAND = "k_command";

    //音乐名称
    public static final String KEY_MUSIC_NAME = "k_music_name";

    //音乐路径
    public static final String KEY_MUSIC_PATH = "k_music_path";

    //播放位置
    public static final String KEY_MUSIC_INDEX = "k_music_index";

    //音乐列表
    public static final String KEY_MUSIC_LIST = "k_music_index";

    //总播放时长
    public static final String MUSIC_TIME_TOTAL = "music_time_tatol";

    //当前进度
    public static final String MUSIC_TIME_CURR = "music_time_curr";


    //播放进度更新的广播
    public static final String CAST_ACTION_UPDATE = "com.exameple.gy.myapplication.MUSIC_TIME_UPDATE";



    public static final int CMD_INIT = 1000;    //初始化
    public static final int CMD_PLAY = 1001;    //播放
    public static final int CMD_PAUSE = 1002;   //暂停
    public static final int CMD_NEXT = 1003;    //下一曲
    public static final int CMD_PREV = 1004;    //上一曲
    public static final int CMD_STOP = 1005;    //停止
    public static final int CMD_RESUME = 1006;  //从暂停状态恢复
    public static final int CMD_PLAY_TIME = 1007;     //从制定位置播放

    private MusicUtil musicUtil;
    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = intent.getIntExtra(KEY_COMMAND,-1);
        int position = intent.getIntExtra("position",0);
        switch (command){
            case CMD_INIT:
                List<String> musicList = intent.getStringArrayListExtra(KEY_MUSIC_LIST);
                musicUtil = new MusicUtil(context,musicList);
                break;
            case CMD_PLAY:
                int index = intent.getIntExtra(KEY_MUSIC_INDEX,0);
                musicUtil.play(index);
                break;
            case CMD_PAUSE:
                musicUtil.pause();
                break;
            case CMD_RESUME:
                musicUtil.play();
                break;
            case CMD_NEXT:
                musicUtil.next();
                break;
            case CMD_PREV:
                musicUtil.prev();
                break;
            case CMD_STOP:
                musicUtil.stop();
                break;
            case CMD_PLAY_TIME:
                musicUtil.play_time(position);
                musicUtil.play();
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
