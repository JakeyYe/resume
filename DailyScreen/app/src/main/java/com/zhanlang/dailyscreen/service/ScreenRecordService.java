package com.zhanlang.dailyscreen.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Log;

import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/*ScreenRecordService录制视频和音频的后台服务Service*/
public class ScreenRecordService extends Service {

    private static final String TAG = "ScreenRecordingService";

    private FileUtil fileUtil;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;
    /** 横屏录制还是竖屏录制，true表示竖屏，false为横屏 */
	private boolean isVerticalRecord;
    /**
     * 是否开启音频录制
     */
    private boolean isAudio;

    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate() is called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand() is called");

        mResultCode = intent.getIntExtra("code", -1);
        mResultData = intent.getParcelableExtra("data");
        mScreenWidth = intent.getIntExtra("width", 720);
        mScreenHeight = intent.getIntExtra("height", 1280);
        mScreenDensity = intent.getIntExtra("density", 1);
		isVerticalRecord = intent.getBooleanExtra("isVerticalRecord", true);
        isAudio = intent.getBooleanExtra("audio", true);

        mMediaProjection = createMediaProjection();
        mMediaRecorder = createMediaRecorder();
        mVirtualDisplay = createVirtualDisplay();
        // 必须在mediaRecorder.prepare() 之后调用，否则报错"fail to get surface"

        mMediaRecorder.start();//正式开启录制

        return Service.START_NOT_STICKY;
    }

    private MediaProjection createMediaProjection() {
        Log.i(TAG, "Create MediaProjection");
        return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).getMediaProjection(mResultCode, mResultData);
    }

    private MediaRecorder createMediaRecorder() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = formatter.format(curDate);
        String videoQuality = "HD";
//		if(isVideoSd) videoQuality = "SD";

        Log.i(TAG, "Create MediaRecorder");
        MediaRecorder mediaRecorder = new MediaRecorder();
        if (isAudio) mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        if(!isVerticalRecord){
            //TODO 后期检测手机横向方向，是从上往下，还是从下往上
            mediaRecorder.setOrientationHint(270);//横屏从上往下录制
        }
        if(fileUtil==null){
            fileUtil=new FileUtil();
        }

        //视频保存文件   .../HD2017-10-19 22:40:27.mp4
        File file=fileUtil.createAnFile(getResources()
                        .getString(R.string.package_name),
                                           videoQuality+curTime,".mp4");

        mediaRecorder.setOutputFile(file.getAbsolutePath());

        mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);  //after setVideoSource(), setOutFormat()
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //after setOutputFormat()
        if (isAudio)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  //after setOutputFormat()
        int bitRate;
//		if(isVideoSd) {
//			mediaRecorder.setVideoEncodingBitRate(mScreenWidth * mScreenHeight);
//			mediaRecorder.setVideoFrameRate(30);
//			bitRate = mScreenWidth * mScreenHeight / 1000;
//		} else {
        //全部录制高清HD视频
        mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);
        mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
        bitRate = 5 * mScreenWidth * mScreenHeight / 1000;
//		}
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Audio: " + isAudio + ", HD video: " + ", BitRate: " + bitRate + "kbps");

        return mediaRecorder;
    }

    private VirtualDisplay createVirtualDisplay() {
        Log.i(TAG, "Create VirtualDisplay");
        return mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaProjection.stop();
            mMediaRecorder.reset();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
