package com.zhanlang.dailyscreen.tabpager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.activity.VideoEditActivity;
import com.zhanlang.dailyscreen.bean.Video;
import com.zhanlang.dailyscreen.utils.DisplayUtil;
import com.zhanlang.dailyscreen.utils.FileUtil;
import com.zhanlang.dailyscreen.videoAdapter.VideoAdapter;

import java.io.File;
import java.util.List;


/**
 * 我的视频界面 TODO RecyclerView Item点击事件
 * Created by jpeng on 16-11-14.
 */
public class Tab2Pager extends Fragment
        implements VideoAdapter.ButtonInterface, View.OnClickListener {

    private static final String TAG = Tab2Pager.class.getSimpleName();

    private VideoAdapter videoAdapter;
    private LinearLayoutManager linearLayoutManager;

    //视频列表
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Video> mVideoList;

    private String curVideoUrl;
    private int curPosition;
    private PopupWindow popupWindow;
    //文件重命名的对话框
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private EditText contentView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 12:
                    recyclerView.setAdapter(videoAdapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
                case 13:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), R.string.error_retry, Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    videoAdapter.updateVideoList(mVideoList);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab2, null);
        init(layout);
        Log.i(TAG, "onCreateView: ");
        return layout;
    }

    //初始化视图
    private void init(final View layout) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

                recyclerView = (RecyclerView) layout.findViewById(R.id.video_list);
                recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                try {
                    mVideoList = new FileUtil().getMp4VideosPathFromFolder(getContext());//创建Video List对象
                    videoAdapter = new VideoAdapter(getContext(), mVideoList);
                    videoAdapter.setBtnInterface(Tab2Pager.this);
                    mHandler.sendEmptyMessage(12);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(13);
                }
            }
        }).start();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, "setUserVisibleHint: ");
        if (isVisibleToUser) {//重新加载，更新数据
            updateTab2Pager();
        }
    }

    private void updateTab2Pager() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mVideoList = new FileUtil().getMp4VideosPathFromFolder(getContext());//创建Video List对象
                    mHandler.sendEmptyMessage(14);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(13);
                }
            }
        }).start();
    }

    @Override
    public void moreOperationBtnSelected(int position, String videoUrl) {
        if (popupWindow == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentView = inflater.inflate(R.layout.view_more_operate, null);
            contentView.setFocusable(true); // 这个很重要
            contentView.setFocusableInTouchMode(true);
            popupWindow = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), 48));
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            contentView.setOnClickListener(this);
            contentView.findViewById(R.id.rename).setOnClickListener(this);
            contentView.findViewById(R.id.cut).setOnClickListener(this);
            contentView.findViewById(R.id.delete).setOnClickListener(this);
            contentView.findViewById(R.id.share).setOnClickListener(this);
        }
        curPosition = position;
        curVideoUrl = videoUrl;
        if (linearLayoutManager.findLastVisibleItemPosition() == position) {
            popupWindow.showAsDropDown(linearLayoutManager.findViewByPosition(position), 0, DisplayUtil.dip2px(getContext(), -148));
        } else {
            popupWindow.showAsDropDown(linearLayoutManager.findViewByPosition(position), 0, DisplayUtil.dip2px(getContext(), -8));
        }
    }

    @Override
    public void playVideo(String videoUrl) {
        Uri uri = Uri.parse("file://" + videoUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.i(TAG, videoUrl);
        intent.setDataAndType(uri, "video/mp4");
        try {
            getActivity().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "没有系统播放器", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_operate:
                popupWindow.dismiss();
                break;
            case R.id.rename:
                if (alertDialogBuilder == null) {
                    alertDialogBuilder = new AlertDialog.Builder(getContext());
                    contentView = new EditText(getContext());
                    contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    int padding = DisplayUtil.dip2px(getContext(), 24);
                    contentView.setPadding(padding, DisplayUtil.dip2px(getContext(),12), 0, padding);
                    alertDialogBuilder.setView(contentView)
                            .setTitle(getString(R.string.file_rename_title))
                            .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String input=contentView.getText().toString();
                                    if(!input.equals("")){
                                        int result = FileUtil.reNameFile(curVideoUrl, contentView.getText().toString());
                                        if (result == -1) {
                                            Toast.makeText(getContext(), R.string.rename_failure, Toast.LENGTH_SHORT).show();
                                        } else if (result == 0) {
                                            Toast.makeText(getContext(), R.string.rename_failure_same_name, Toast.LENGTH_SHORT).show();
                                        } else if (result == 1) {
                                            updateTab2Pager();
                                            Toast.makeText(getContext(), R.string.rename_success, Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Toast.makeText(getContext(), R.string.input_empty, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setCancelable(true);
                    alertDialog = alertDialogBuilder.create();
                }
                contentView.getText().clear();
                alertDialog.show();
                break;
            case R.id.cut:
                Intent eidtIntent=new Intent(getActivity(), VideoEditActivity.class);
                eidtIntent.putExtra("fileUrl",curVideoUrl);
                getActivity().startActivity(eidtIntent);
//                Toast.makeText(getContext(), "cut " + curVideoUrl, Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                if (FileUtil.deleteAnFile(curVideoUrl)) {
                    videoAdapter.notifyItemRemoved(curPosition);
                    videoAdapter.removeVideoFromList(curPosition);
                    Toast.makeText(getContext(), curVideoUrl + getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), curVideoUrl + getString(R.string.delete_failure), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("video/mp4");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(curVideoUrl)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }

    }
}

