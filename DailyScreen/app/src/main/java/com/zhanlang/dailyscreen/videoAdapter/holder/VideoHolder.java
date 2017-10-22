package com.zhanlang.dailyscreen.videoAdapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.bean.Video;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/** ViewHolder视图缓存
 * Created by SX on 2017/10/16.
 */

public class VideoHolder extends RecyclerView.ViewHolder {
    //首张图
    public ImageView videoBtMap;
    //内存大小
    public TextView videoMemoryLong;
    //创建时间
    public TextView videoCreateTime;
    //时长
    public  TextView videoTimeLong;
    //内存大小
    public TextView videoName;
    //更多操作
    public RelativeLayout moreOperation;

    public VideoHolder(View itemView){
        super(itemView);


        videoBtMap = (ImageView)itemView.findViewById(R.id.vedio_Bitmap);

//        // 将列表中的每个视频设置为默认16:9的比例
//        ViewGroup.LayoutParams params =  videoBttMap.getLayoutParams();
//        params.width = itemView.getResources().getDisplayMetrics().widthPixels; // 宽度为屏幕宽度
//        params.height = (int) (params.width * 9f / 16f);    // 高度为宽度的9/16
//        videoBttMap.setLayoutParams(params);

        videoTimeLong=(TextView) itemView.findViewById(R.id.video_time_long);
        videoMemoryLong = (TextView) itemView.findViewById(R.id.video_MemoryLong);
        videoCreateTime = (TextView) itemView.findViewById(R.id.video_CreateTime);
        videoName = (TextView) itemView.findViewById(R.id.video_Name);
        moreOperation = (RelativeLayout) itemView.findViewById(R.id.moreOperate);

    }
    public void bindData(Video video){
        //视图赋值  直接赋值  模型赋值
        videoBtMap.setImageBitmap(video.getBitmap());

        String fileName=video.getTitle().substring(0,video.getTitle().length()-".mp4".length());

        videoName.setText(fileName);

        videoCreateTime.setText(convertToDate(video.getVideoUrl()));
        //这里大小需要处理
        videoMemoryLong.setText(video.getMemoryString());
        //设置视频时长
        videoTimeLong.setText(video.getLength());
    }

    /*返回的是文件最后修改时间，所以也不是那么准确*/
    public static String convertToDate(String fileUrl){
        File file=new File(fileUrl);
        String time = new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date(file.lastModified()));
        return time;
    }



}
