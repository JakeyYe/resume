package com.zhanlang.dailyscreen.videoAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.bean.Video;
import com.zhanlang.dailyscreen.videoAdapter.holder.VideoHolder;

import java.util.List;

/**
 * "我的视频"界面的RecyclerView的适配器
 * * Created by SX on 2017/10/16.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoHolder>{


    private static final String TAG = "VideoAdapter";
    private Context mContext;
    private List<Video> mVideoList;

    private  ButtonInterface btnInterface;


    public VideoAdapter(Context context, List<Video> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    public void updateVideoList(List<Video> videos){
        mVideoList.clear();
        mVideoList.addAll(videos);
        notifyDataSetChanged();
    }

    public void removeVideoFromList(int pos){
        mVideoList.remove(pos);
    }

    //设置回调借口
    public void setBtnInterface(ButtonInterface btnInterface) {
        this.btnInterface = btnInterface;
    }


    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.recycler_video_item, parent, false);
        VideoHolder holder = new VideoHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, final int position) {
        final Video video = mVideoList.get(position);
        holder.bindData(video);

        holder.videoBtMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnInterface != null) {
                    btnInterface.playVideo(video.getVideoUrl());
                }
            }
        });
        holder.moreOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnInterface != null) {
                    //接口实例化后的对象，调用重写后的方法
                    btnInterface.moreOperationBtnSelected(position, video.getVideoUrl());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    /**
     * 按钮点击事件对应的接口
     */
    public interface ButtonInterface{
        public void moreOperationBtnSelected(int position, String videoUrl);
        public void playVideo(String videoUrl);

    }
}
