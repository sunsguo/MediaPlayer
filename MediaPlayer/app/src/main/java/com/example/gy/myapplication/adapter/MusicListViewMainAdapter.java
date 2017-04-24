package com.example.gy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gy.myapplication.R;
import com.example.gy.myapplication.bean.Music;

import java.util.List;

/**
 * Created by gy on 2016/10/27.
 */

public class MusicListViewMainAdapter extends BaseAdapter {

    private List<Music> mData;

    private Context mContext;

    private LayoutInflater mInflater;   //用于读取布局文件

    public MusicListViewMainAdapter(List<Music> mData, Context context){
        this.mData = mData;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            //第一次调用getView()时，conVertView 时空的
            convertView = mInflater.inflate(R.layout.list_item,null);

            //将布局视图封装到对象中，方便层出
            holder = new Holder();
            holder.singer = (TextView)convertView.findViewById(R.id.txtv_main_list_item_singer);
            holder.title = (TextView)convertView.findViewById(R.id.txtv_main_list_item_title);
            //将视图存储起来，用于重复使用
            convertView.setTag(holder);
        }else{
            //从第二次开始，convetView不在为空，切其tag值还附带了一个视图模板
            holder = (Holder)convertView.getTag();
        }
        //给视图上的元素设置数据
        holder.title.setText(mData.get(position).getTitle());
        holder.singer.setText(mData.get(position).getSinger());

        //最后返回这个视图，系统就会根据这个视图绘制到listview上
        return convertView;
    }

    //为了打包视图上的几个空间
    class Holder{
        TextView singer;
        TextView title;
    }

}
