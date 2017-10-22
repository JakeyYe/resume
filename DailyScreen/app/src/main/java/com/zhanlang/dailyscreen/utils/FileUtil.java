package com.zhanlang.dailyscreen.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.bean.Video;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * FileUtil操作File文件的工具类
 * Created by SX on 2017/10/16.
 */

public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * 获取指定文件大小
     *
     * @throws Exception
     */
    public long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        } else {
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小 ,换算为不同单位的数据
     */
    public String FormentFileSize(File file) throws Exception {

        long fileS = getFileSize(file);

        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 创建一个路径文件
     *
     * @param folder 文件夹名称
     * @param type   文件类型   音频或者视频
     */
    public File createAnFile(String folder, String specific, String type) {
        //获取外部存储中的公有目录路径

        String pathStr = Environment.getExternalStorageDirectory() + File.separator + folder;

        File folderPath = new File(pathStr);//视频文件目录

        if (!folderPath.exists()) {
            folderPath.mkdirs();//File.mkdirs()是创建文件夹的方法
        }
        //存储当前单个视频的文件，new File()已经直接创建文件了，或者使用File.createNewFile()
        File file = new File(folderPath, specific + type);
        return file;
    }


    /**
     * 从sd对应的文件夹卡获取所有的视频对象
     */
    public List<Video> getMp4VideosPathFromFolder(Context context) throws IOException {
        //弱引用
        WeakReference<Context> mContext = new WeakReference<>(context);
        // MP4列表
        List<Video> mp4List = new ArrayList<>();
        ArrayList<String> fileList = new ArrayList<>();  //所有的路径

        ArrayList<String> urlList = new ArrayList<>();//所有路径的 URl

        ArrayList<String> videoTitles = new ArrayList<>();

        // 得到sd卡内mp4列表File.separator(/)
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + mContext.get().getResources().getString(R.string.package_name);

        Log.i(TAG, "getMp4VideosPathFromFolder: filePath " + filePath);
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所mp4文件
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];


                if (checkIsMp4File(file.getPath())) {
                    urlList.add(file.getPath());
                    fileList.add(file.getAbsolutePath());

                    String title = file.getPath().substring(file.getPath()
                                    .lastIndexOf("/") + 1,
                            file.getPath().length());
                    videoTitles.add(title);
                }
            }
            //遍历获取所有.........
            for (int p = 0; p < urlList.size(); p++) {
                VideoUtil videoUtil = new VideoUtil(fileList.get(p));
                int dur = videoUtil.getDurrection();
                mp4List.add(new Video(videoTitles.get(p), dur, fileList.get(p)));
            }
            Log.i(TAG, "getMp4VideosPathFromFolder: mp4List size " + mp4List.size());
        }
        return mp4List;
    }

    /**
     * 检查扩展名，得到mp4格式的文件
     *
     * @param fName 文件名
     */
    @SuppressLint("DefaultLocale")
    private boolean checkIsMp4File(String fName) {
        boolean isMp4File = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("mp4")) {
            isMp4File = true;
        } else {
            isMp4File = false;
        }
        return isMp4File;
    }

    /*删除单个文件*/
    public static boolean deleteAnFile(String fileUrl){

        File file=new File(fileUrl);
        //检测文件是否存在，并且是一个文件，则直接删除
        if(file.exists()&&file.isFile()){
            if(file.delete()){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * @param fileUrl
     * @param newName
     * @return 0 表明新名称和旧名称相同，1，表明命名操作成功，-1，表明命名操作失败
     */
    /*重命名单个文件*/
    public static int reNameFile(String fileUrl,String newName){
        Log.i(TAG, "reNameFile: "+fileUrl+" "+newName);
        //获取原来的文件名
        int index=fileUrl.lastIndexOf(File.separator);
        String oldName=fileUrl.substring(index+1,fileUrl.length()-".mp4".length());
        Log.i(TAG, "reNameFile: oldName "+oldName);
        if(oldName.equals(newName)){
            return 0;//旧名称和新名称相同操作失败
        }else {
            File oldFile=new File(fileUrl);
            //新文件
            File newFile=new File(fileUrl.substring(0,index+1)+newName+".mp4");
            Log.i(TAG, "reNameFile: newFile "+newFile.toString());
            if(oldFile.exists()){
                if(oldFile.renameTo(newFile)){
                    return 1;
                }else {
                    return -1;
                }
            }else {
                return -1;
            }
        }
    }
}
