package com.example.gy.myapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.gy.myapplication.R;
import com.example.gy.myapplication.adapter.MusicListViewMainAdapter;
import com.example.gy.myapplication.bean.Music;
import com.example.gy.myapplication.service.MusicService;
import com.example.gy.myapplication.util.HttpAsyncRequestTast;
import com.example.gy.myapplication.util.LogUtil;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String HTTP_ROOT = "http://100.64.20.82:8081/";
    private ListView listView;
    private SeekBar seekBar;
    private TextView textViewCurr;
    private TextView textViewTotal;
    private TextView textViewMusicName;

    private ImageButton imageButtonNext;
    private ImageButton imageButtonPrev;
    private ImageButton imageButtonPlay;

    private Context context;
    private MusicReceiver musicReceiver;
    private int musicIndex = -1;
    private static List<Music> musics;

    //记录播放状态的全局变量
    public static boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        //从视图文件中读取各个控件以供后续使用
        listView = (ListView)findViewById(R.id.listv_main_music_list);
        seekBar = (SeekBar)findViewById(R.id.skb_main);
        seekBar.setMax(-999);

        textViewCurr = (TextView)findViewById(R.id.txtv_main_curr_time);
        textViewTotal = (TextView)findViewById(R.id.txtv_main_total_time);
        textViewMusicName = (TextView)findViewById(R.id.txtv_main_music_title);

        imageButtonNext = (ImageButton)findViewById(R.id.imgbtn_main_next);
        imageButtonPrev = (ImageButton)findViewById(R.id.imgbtn_main_prev);
        imageButtonPlay = (ImageButton)findViewById(R.id.imgbtn_main_play);

        ImagbtnClickListener listener = new ImagbtnClickListener();
        imageButtonNext.setOnClickListener(listener);
        imageButtonPrev.setOnClickListener(listener);
        imageButtonPlay.setOnClickListener(listener);

        //注册音乐播放状态更新的接受器
        //特别说明通过动态注册的方式注册的广播接受器，在程序推出前必须要注销，一般写在onDestroy中。
        // 不注销的话推出程序会报错
        MusicReceiver musicReceiver = new MusicReceiver();
        IntentFilter musicReceiverFilter = new IntentFilter(MusicService.CAST_ACTION_UPDATE);
        registerReceiver(musicReceiver,musicReceiverFilter);

        //实例化一个异步数据加载任务
        HttpAsyncRequestTast jquery = new HttpAsyncRequestTast() {
            @Override
            public void onComplete(String s) {
                try{
                    //从json中解析出音乐列表
                    JSONArray array = new JSONArray(s);
                    musics = new ArrayList<Music>();
                    Music music ;
                    for(int i = 0;i<array.length();i++){
                        music = new Music();
                        JSONObject jsonObject = array.getJSONObject(i);
                        music.setTitle(jsonObject.getString("name"));
                        music.setSinger(jsonObject.getString("singer"));
                        music.setPath(jsonObject.getString("mp3"));
                        musics.add(music);
                    }

                    //JSON解析完成后，将音乐列表数据传给适配器
                    MusicListViewMainAdapter adapter = new MusicListViewMainAdapter(musics,context);
                    listView.setAdapter(adapter);

                    //给服务设置播放列表
                    Intent intent = new Intent(context,MusicService.class);

                    //解析播放列表获取播放路径
                    ArrayList<String> musicList = new ArrayList<String>();
                    for(Music music1 : musics){
                        musicList.add(HTTP_ROOT+music1.getPath());
                    }

                    //设置初始化指令
                    intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_INIT);

                    //传递初始化数据
                    intent.putStringArrayListExtra(MusicService.KEY_MUSIC_LIST,musicList);

                    //启动服务
                    startService(intent);
                }catch (Exception e){
                    LogUtil.toast(context,e.getMessage());
                }
            }
        };

        //执行异步加载的任务，将请求的url发送到服务器
        jquery.execute(new HttpGet(HTTP_ROOT+"music/music.json"));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,MusicService.class);

                //设置初始化指令
                intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_PLAY);

                //传递播放位置
                intent.putExtra(MusicService.KEY_MUSIC_INDEX,position);

                //启动服务
                startService(intent);
                isPlaying = true;
                imageButtonPlay.setBackgroundResource(R.drawable.btn_pause);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    Intent intent = new Intent(context,MusicService.class);
                    intent.putExtra("position",progress);
                    intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_PLAY_TIME);
                    seekBar.setProgress(progress);
                    startService(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if(! isPlaying){
            imageButtonPlay.setBackgroundResource(R.drawable.btn_play);
        }else{
            imageButtonPlay.setBackgroundResource(R.drawable.btn_pause);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicReceiver);
    }

    /*
            界面上各个按钮的单击事件的监听类
             */
    class ImagbtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //给服务设置播放列表
            Intent intent = new Intent(context,MusicService.class);
            switch (v.getId()){
                case R.id.imgbtn_main_next:
                    //设置下一曲指令
                    intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_NEXT);
                    break;
                case R.id.imgbtn_main_prev:
                    //设置上一曲指令
                    intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_PREV);
                    break;
                case R.id.imgbtn_main_play:
                    if(isPlaying){
                        //设置暂停
                        intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_PAUSE);
                        isPlaying = false;
                        imageButtonPlay.setBackgroundResource(R.drawable.btn_play);
                    }else{
                        //设置播放指令
                        intent.putExtra(MusicService.KEY_COMMAND,MusicService.CMD_RESUME);
                        isPlaying = true;
                        imageButtonPlay.setBackgroundResource(R.drawable.btn_pause);
                    }
                    break;
                default:
                    break;
            }
            //启动服务
            startService(intent);
        }
    }

    /*
    播放进度更新时的广播接受器
     */
    class MusicReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //从广播中解析各种数据
            int index = intent.getIntExtra(MusicService.KEY_MUSIC_INDEX,0);
            int curr = intent.getIntExtra(MusicService.MUSIC_TIME_CURR,0);
            int total = intent.getIntExtra(MusicService.MUSIC_TIME_TOTAL,0);
            //如果播放的位置发生变化说明已经播放另一首歌曲了，此时需要更新音乐封面名称
            if(musicIndex != index){
                musicIndex = index;
                //设置进度条的最大值
                seekBar.setMax(total);
                textViewTotal.setText(this.msToString(total));
                textViewMusicName.setText(musics.get(musicIndex).getTitle());
                //当开始播放后，将播放按钮换成暂停
                imageButtonPlay.setBackgroundResource(R.drawable.btn_pause);
            }
            //设置当前进度条的当前播放值
            seekBar.setProgress(curr);
            textViewCurr.setText(this.msToString(curr));
        }
    //将毫秒拆成分：秒
        private String msToString(int tiem){
            int s_temp = tiem / 1000;
            int m = s_temp / 60;
            int s = s_temp % 60;
            String str_m = String.valueOf(m);
            String str_s = String.valueOf(s);
            if(m < 10){
                str_m = "0" + String.valueOf(m);
            }
            if(s < 10){
                str_s = "0" + String.valueOf(s);
            }
            return str_m + ":" + str_s;
        }
    }

}
