package com.zhanlang.dailyscreen.tabpager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.jpeng.jptabbar.JPTabBar;
import com.zhanlang.dailyscreen.MainActivity;
import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.service.ScreenRecordService;

import static android.app.Activity.RESULT_OK;

/**
 * 主页界面
 * Created by jpeng on 16-11-14.
 */

/*主页界面Tab1Pager*/
public class Tab1Pager extends Fragment implements View.OnClickListener {

    private static final String TAG = "Tab1Pager";

    private JPTabBar mTabBar;

    private SwitchCompat switchCompat;

    private Button hor_record_btn, ver_record_btn;

    //表示是否录制音频内容，默认开启录制音频
    private boolean voiceRecordIsOpen = true;

    /*是否已经开启了视频录制*/
    private boolean isStartRecord = false;

    /*表示是否是竖屏录制，默认为竖屏录制*/
    private boolean isVerticalRecord = true;

    private static final int RECORD_REQUEST_CODE = 1;
    private static final int STORAGE_REQUEST_CODE = 2;
    private static final int AUDIO_REQUEST_CODE = 3;
    private static final int TWO_REQUEST_CODE = 4;

//    private static final String RECORD_STATUS="record_status";

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private int interval = 100;//间隔取样时间
    private long startTime;//录制开始时间

    //代表两个权限
    private boolean hasStoragePermission = false;
    private boolean hasRecordPermisssion = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab1, null);
        init(layout);
//        initPermissions();
        return layout;
    }

    private void init(View layout) {
        mTabBar = ((MainActivity) getActivity()).getTabbar();//获取底部的导航栏控件

        switchCompat = (SwitchCompat) layout.findViewById(R.id.switch_o_or_v);
        switchCompat.setText(R.string.close_voice_record);//默认开启录音
        switchCompat.setChecked(true);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    switchCompat.setChecked(true);
                    switchCompat.setText(R.string.close_voice_record);
                    voiceRecordIsOpen = true;
                    Toast.makeText(getContext(), R.string.open_voice_record, Toast.LENGTH_SHORT).show();
                } else {
                    switchCompat.setChecked(false);
                    switchCompat.setText(R.string.open_voice_record);
                    voiceRecordIsOpen = false;
                    Toast.makeText(getContext(), R.string.close_voice_record, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //横屏录制开启按钮
        hor_record_btn = (Button) layout.findViewById(R.id.hor_record_btn);
        //竖屏录制开启按钮
        ver_record_btn = (Button) layout.findViewById(R.id.ver_record_btn);

        hor_record_btn.setOnClickListener(this);
        ver_record_btn.setOnClickListener(this);

        getScreenBaseInfo();

    }

    /**
     * 获取屏幕相关数据
     */
    private void getScreenBaseInfo() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;
    }

    /*运行时权限的申请,申请一个外部文件读写权限和一个录音权限*/
    private void initPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //checkSelfPermission()一次只能检测一个权限是否授予,二requestPermissions()一次可以申请多个权限
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                hasStoragePermission = false;
            } else {
                hasStoragePermission = true;
            }

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                hasRecordPermisssion = false;
            } else {
                hasRecordPermisssion = true;
            }

            //两个权限全获取了就可以开启录制
            if (hasStoragePermission && hasRecordPermisssion) {
                startScreenRecording();
            } else {
                if (!hasStoragePermission && hasRecordPermisssion) {//单独申请一个权限
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                } else if (!hasRecordPermisssion && hasStoragePermission) {//单独申请一个权限
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
                } else if (!hasStoragePermission && !hasRecordPermisssion) {//同时申请两个权限
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO},
                            TWO_REQUEST_CODE);
                }

            }
        }
    }

    /*运行时权限申请回调*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //TODO 小米手机根本没有回调该方法
        Log.i(TAG, "onRequestPermissionsResult: 222");
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "权限未授予，无法录屏", Toast.LENGTH_SHORT).show();
            } else {
                hasStoragePermission = true;
            }
        } else if (requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "权限未授予，无法录屏", Toast.LENGTH_SHORT).show();
            } else {
                hasRecordPermisssion = true;
            }
        } else if (requestCode == TWO_REQUEST_CODE) {//两个权限同时申请
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: 333");
                hasStoragePermission = true;
                hasRecordPermisssion = true;
            } else {
                Toast.makeText(getContext(), "权限未授予，无法录屏", Toast.LENGTH_SHORT).show();
            }
        }

        if (hasStoragePermission && hasRecordPermisssion) {
            Log.i(TAG, "onRequestPermissionsResult: 111");
            startScreenRecording();
            
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.hor_record_btn) {
            if (isStartRecord) {//已经开启录制了
                stopScreenRecording();//停止录制
            } else {
                isVerticalRecord = false;//横屏录制
                initPermissions();
            }

        } else if (view.getId() == R.id.ver_record_btn) {
            if (isStartRecord) {//已经开启录制了
                stopScreenRecording();
            } else {
                isVerticalRecord = true;//竖屏录制
                initPermissions();
            }
        }
    }


    /*开启录制*/
    private void startScreenRecording() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getActivity()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(permissionIntent, RECORD_REQUEST_CODE);
    }

    /**
     * 关闭屏幕录制，即停止录制Service
     */
    private void stopScreenRecording() {
        Intent service = new Intent(getContext(), ScreenRecordService.class);
        getActivity().stopService(service);
        isStartRecord = !isStartRecord;
        if (!isStartRecord) {
            if (isVerticalRecord) {
                ver_record_btn.setText(R.string.start_record);
            } else {
                hor_record_btn.setText(R.string.start_record);
            }
        }
    }

    //活动页面跳转之后回调
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 获得权限，启动Service开始录制
                Intent service = new Intent(getContext(), ScreenRecordService.class);
                service.putExtra("code", resultCode);
                service.putExtra("data", data);
                service.putExtra("audio", voiceRecordIsOpen);
                service.putExtra("width", mScreenWidth);
                service.putExtra("height", mScreenHeight);
                service.putExtra("density", mScreenDensity);
                //横屏录制还是竖屏录制
                service.putExtra("isVerticalRecord", isVerticalRecord);
                getActivity().startService(service);

                // 已经开始屏幕录制，修改UI状态
                isStartRecord = !isStartRecord;
                Toast.makeText(getContext(), "正在录制...", Toast.LENGTH_SHORT).show();
                simulateHome(); // this.finish();  // 可以直接关闭Activity

                //获取开始时间
                startTime = System.currentTimeMillis();
                updateRecordStatus();

                Log.i(TAG, "Started screen recording");
            } else {
                Toast.makeText(getContext(), "权限未授予，无法录屏", Toast.LENGTH_LONG).show();
                Log.i(TAG, "User cancelled");
            }
        }
    }

    /**
     * 模拟HOME键返回桌面的功能
     */
    private void simulateHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(intent);
    }

    /**
     * @param time 时间 秒
     * @return 时分秒显示
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateRecordStatus();
        }
    };

    private static Handler sHandler = new Handler();

    /**
     * 更新麦克风状态
     */
    private void updateRecordStatus() {

        long time = System.currentTimeMillis() - startTime;
        int second = (int) time / 1000;
        String codeTime = secToTime(second);

        if (isStartRecord) {
            if (isVerticalRecord) {//竖屏录制
                ver_record_btn.setText(getResources().getString(R.string.record_time) + codeTime);
            } else {//横屏录制
                hor_record_btn.setText(getResources().getString(R.string.record_time) + codeTime);
            }
            sHandler.postDelayed(mUpdateMicStatusTimer, interval);//post到主线程中执行
        }
    }
}
