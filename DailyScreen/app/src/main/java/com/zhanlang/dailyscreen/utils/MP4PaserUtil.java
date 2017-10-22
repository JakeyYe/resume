package com.zhanlang.dailyscreen.utils;

import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 对视频操作的工具类
 * Created by SX on 2017/10/16.
 */

public class MP4PaserUtil {

    private static final String TAG = "mp4PaserUtil";

    /**
     *  对Mp4文件集合进行追加合并(按照顺序一个一个拼接起来)
     * @param mp4PathList 输入 mp4的文件路径集合
     * @param outPutPath  输出文件的整体路径
     * @throws IOException 格式不支持抛出异常
     */

    public static void appendMp4List(List<String> mp4PathList, String outPutPath) throws IOException {
        List<Movie> mp4MovieList = new ArrayList<>();// Movie对象集合[输入]
        for (String mp4Path : mp4PathList) {// 将每个文件路径都构建成一个Movie对象
            mp4MovieList.add(MovieCreator.build(mp4Path));
        }

        List<Track> audioTracks = new LinkedList<>();// 音频通道集合
        List<Track> videoTracks = new LinkedList<>();// 视频通道集合

        for (Movie mp4Movie : mp4MovieList) {// 对Movie对象集合进行循环
            for (Track inMovieTrack : mp4Movie.getTracks()) {
                if ("soun".equals(inMovieTrack.getHandler())) {// 从Movie对象中取出音频通道
                    audioTracks.add(inMovieTrack);
                }
                if ("vide".equals(inMovieTrack.getHandler())) {// 从Movie对象中取出视频通道
                    videoTracks.add(inMovieTrack);
                }
            }
        }

        Movie resultMovie = new Movie();// 结果Movie对象[输出]
        if (!audioTracks.isEmpty()) {// 将所有音频通道追加合并
            resultMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (!videoTracks.isEmpty()) {// 将所有视频通道追加合并
            resultMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container outContainer = new DefaultMp4Builder().build(resultMovie);// 将结果Movie对象封装进容器
        FileChannel fileChannel = new RandomAccessFile(String.format(outPutPath), "rw").getChannel();
        outContainer.writeContainer(fileChannel);// 将容器内容写入磁盘
        fileChannel.close();
    }


    /**
     * 执行MP4的追加合成
     */
    private void doMp4Append() {
        try {
            List<String> mp4PathList = new ArrayList<>();
//            mp4PathList.add(rootPath + "/resource/" + "video1" + ".mp4");
//            mp4PathList.add(rootPath + "/resource/" + "video2" + ".mp4");
            String outPutPath = "";//rootPath + "/output/" + "outVideo" + ".mp4";
            MP4PaserUtil.appendMp4List(mp4PathList, outPutPath);
            Log.d(TAG, "doMp4Append: "+"合并完成");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "doMp4Append: "+"合并失败" );
        }
    }



    /**
     * 将 AAC 和 MP4 进行混合[替换了视频的音轨]
     *
     * @param aacPath .aac
     * @param mp4Path .mp4
     * @param outPath .mp4
     */
    public static boolean muxAacMp4(String aacPath, String mp4Path, String outPath) {
        try {
            AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(aacPath));
            Movie videoMovie = MovieCreator.build(mp4Path);
            Track videoTracks = null;// 获取视频的单纯视频部分
            for (Track videoMovieTrack : videoMovie.getTracks()) {
                if ("vide".equals(videoMovieTrack.getHandler())) {
                    videoTracks = videoMovieTrack;
                }
            }
            Movie resultMovie = new Movie();
            resultMovie.addTrack(videoTracks);// 视频部分
            resultMovie.addTrack(aacTrack);// 音频部分
            Container out = new DefaultMp4Builder().build(resultMovie);
            FileOutputStream fos = new FileOutputStream(new File(outPath));
            out.writeContainer(fos.getChannel());
            fos.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }



    /**
     * 对 Mp4 添加字幕
     *
     * @param mp4Path .mp4 添加字幕之前
     * @param outPath .mp4 添加字幕之后
     */
    public static void addSubtitles(String mp4Path, String outPath) throws IOException {
        Movie videoMovie = MovieCreator.build(mp4Path);

        TextTrackImpl subTitleEng = new TextTrackImpl();// 实例化文本通道对象
        subTitleEng.getTrackMetaData().setLanguage("eng");// 设置元数据(语言)

        subTitleEng.getSubs().add(new TextTrackImpl.Line(0, 1000, "学哥最帅5"));// 参数时间毫秒值
        subTitleEng.getSubs().add(new TextTrackImpl.Line(1000, 2000, "学哥最帅4"));
        subTitleEng.getSubs().add(new TextTrackImpl.Line(2000, 3000, "学哥最帅3"));
        subTitleEng.getSubs().add(new TextTrackImpl.Line(3000, 4000, "学哥最帅2"));
        subTitleEng.getSubs().add(new TextTrackImpl.Line(4000, 5000, "学哥最帅1"));
        subTitleEng.getSubs().add(new TextTrackImpl.Line(5001, 5002, " 学哥最帅空空"));// 省略去测试
        videoMovie.addTrack(subTitleEng);// 将字幕通道添加进视频Movie对象中

        Container out = new DefaultMp4Builder().build(videoMovie);
        FileOutputStream fos = new FileOutputStream(new File(outPath));
        out.writeContainer(fos.getChannel());
        fos.close();
    }



    /**
     * 将 MP4 切割
     *
     * @param mp4Path    .mp4
     * @param fromSample 起始位置
     * @param toSample   结束位置
     * @param outPath    .mp4
     */
    public static void cropMp4(String mp4Path, long fromSample, long toSample, String outPath) throws
            IOException {
        Movie mp4Movie = MovieCreator.build(mp4Path);
        Track videoTracks = null;// 获取视频的单纯视频部分
        for (Track videoMovieTrack : mp4Movie.getTracks()) {
            if ("vide".equals(videoMovieTrack.getHandler())) {
                videoTracks = videoMovieTrack;
            }
        }
        Track audioTracks = null;// 获取视频的单纯音频部分
        for (Track audioMovieTrack : mp4Movie.getTracks()) {
            if ("soun".equals(audioMovieTrack.getHandler())) {
                audioTracks = audioMovieTrack;
            }
        }

        Movie resultMovie = new Movie();
        resultMovie.addTrack(new AppendTrack(new CroppedTrack(videoTracks, fromSample, toSample)));// 视频部分
        resultMovie.addTrack(new AppendTrack(new CroppedTrack(audioTracks, fromSample, toSample)));// 音频部分

        Container out = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream fos = new FileOutputStream(new File(outPath));
        out.writeContainer(fos.getChannel());
        fos.close();

    }
}


