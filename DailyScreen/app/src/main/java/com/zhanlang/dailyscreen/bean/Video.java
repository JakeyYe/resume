package com.zhanlang.dailyscreen.bean;

import android.graphics.Bitmap;

import com.zhanlang.dailyscreen.utils.FileUtil;
import com.zhanlang.dailyscreen.utils.TimeUtil;
import com.zhanlang.dailyscreen.utils.VideoUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Video表示"我的视频"展示的RecyclerView的Item
 * Created by SX on 2017/10/16.
 */

public class Video implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;
    //名称
    private String title;//视频名称
    //视频时长
    private String length;
    //视频创建时间，如2017-8-24
    private String createTime;
   //视频路径
    private String videoUrl;
    //第一帧图片
    private Bitmap bitmap;

    public String getMemoryString() {
        return memoryString;
    }

    public void setMemoryString(String memoryString) {
        this.memoryString = memoryString;
    }

    //内存大小
    private String memoryString;


    //构造方法
    public Video(String title, long length, String videoUrl)  {
        //获取第一帧的图片
        VideoUtil videoUtil = new VideoUtil(videoUrl);
        this.bitmap = videoUtil.getSHotScreen();
        this.title = title;
        this.length = new TimeUtil().secToHMS(length/1000);
        this.videoUrl = videoUrl;

        File file = new File(videoUrl);
        try{
            this.memoryString= new FileUtil().FormentFileSize(file);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLength() {
        return length;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
