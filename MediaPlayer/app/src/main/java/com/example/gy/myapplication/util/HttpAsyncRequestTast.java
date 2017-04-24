package com.example.gy.myapplication.util;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by gy on 2016/10/26.
 */


public abstract class HttpAsyncRequestTast extends AsyncTask<HttpUriRequest,Integer,String> {
    @Override
    protected String doInBackground(HttpUriRequest... params) {
        LogUtil.log("------------------任务开始了,进入后台执行程序---------------------");
        //真正执行异步任务的地方，不可以在此方法修改ui
        //如果是做网络请求，那么读取数据的地方也应该在这里

        HttpUriRequest request = params[0];

        //HttpGet ,HttpPost都继承自HttpUriRequest
        String result = null;


        //Apache提供的一个Http客户端
        HttpClient client = new DefaultHttpClient();
        try{
            //通过客户端执行一个请求
            HttpResponse httpRespose = client.execute(request);
            //EntityUtils,用于处理response通过getEntity()获得数据的类
            result = EntityUtils.toString(httpRespose.getEntity(),"GBK");
         //   return = new String(h)
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch(IOException e1 ) {
            e1.printStackTrace();
        }

        //doInBackground()执行成功之后，将从服务器获取到的数据result作为返回值返回，接着会调用doPostExecute(),result 将作为其输入参数
        return result;
    }

    @Override
    protected void onCancelled( ) {
        //当取消异步任务是调用，此处可以跟新ui
        LogUtil.log("------------任务被取消了----------------------");
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(String s) {
        //当任务完成后毁掉，此方法可以修改ui
        LogUtil.log("--------------任务完成了-------------------");
        super.onPostExecute(s);
        onComplete(s);  //自己写的抽象方法
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //当执行的进度发生变化时毁掉，此方法可以修改ui
        LogUtil.log("---------------任务完成了%" + values[0] + "================");
        super.onProgressUpdate(values);
    }

    public abstract void onComplete(String s);
}
