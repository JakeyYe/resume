package com.zhanlang.dailyscreen.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

/**
 * Created by SX on 2017/10/16.
 */

public class VideoUtil {

    private static final String TAG="VideoUtil";
    private  String  videoPath;

    public VideoUtil(String videoPath){
        this.videoPath = videoPath;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public int getDurrection() throws IOException {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
        int dur = Integer.parseInt(duration);
        return dur;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public Bitmap getSHotScreen(){

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);

//        Bitmap newBitMap = rotateBitmap(bitmap,90);

        return media.getFrameAtTime();
    }

    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

}
