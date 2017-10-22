package com.zhanlang.dailyscreen;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.zhanlang.dailyscreen.tabpager.Tab1Pager;
import com.zhanlang.dailyscreen.tabpager.Tab2Pager;
import com.zhanlang.dailyscreen.tabpager.Tab3Pager;

import java.util.ArrayList;
import java.util.List;


/*进入主界面，无Splash界面，TODO 将状态栏StatusBar的背景颜色设置一下*/
public class MainActivity extends AppCompatActivity implements BadgeDismissListener, OnTabSelectListener {

//    @Titles
//    private static final String[] mTitles = {"主页","我的视频","发现"};//去掉发现，10、18

    @SeleIcons
    private static final int[] mSeleIcons = {R.drawable.ic_tab_home_selected,R.drawable.ic_tab_video_selected,R.drawable.ic_tab_find_selected};

    @NorIcons
    private static final int[] mNormalIcons = {R.drawable.ic_tab_home_nor, R.drawable.ic_tab_video_nor, R.drawable.ic_tab_find_nor};

    private List<Fragment> list = new ArrayList<>();

    private ViewPager mPager;

    private JPTabBar mTabbar;

    private Tab1Pager mainTab;

    private Tab2Pager mineVideoTab;

    private Tab3Pager findTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSubViews();
    }

    private void initSubViews (){

        setStatusBarColor();
//        底部导航栏
        mTabbar = (JPTabBar) findViewById(R.id.tabbar);
        mPager = (ViewPager) findViewById(R.id.view_pager);
//        mTabbar.setTitles("qwe","asd","qwe","asdsa").setNormalIcons(R.mipmap.tab1_normal,R.mipmap.tab2_normal,R.mipmap.tab3_normal,R.mipmap.tab4_normal)
//                .setSelectedIcons(R.mipmap.tab1_selected,R.mipmap.tab2_selected,R.mipmap.tab3_selected,R.mipmap.tab4_selected).generate();

        mainTab = new Tab1Pager();//主页界面
        mineVideoTab = new Tab2Pager();//我的视频界面
        findTab = new Tab3Pager();//发现界面
        mTabbar.setTabListener(this);

        list.add(mainTab);
        list.add(mineVideoTab);
        list.add(findTab);
        mPager.setAdapter(new Adapter(getSupportFragmentManager(),list));
        mTabbar.setContainer(mPager);
        mTabbar.setDismissListener(this);

        //设置点击回调者
        mTabbar.setTabListener(this);
        mTabbar.setUseScrollAnimate(true);

    }

    @Override
    public void onDismiss(int position) {

    }


    @Override
    public void onTabSelect(int index) {
        mTabbar.hideBadge(index);
    }


    public JPTabBar getTabbar() {
        return mTabbar;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(){
        Window window = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.title_bar_color));
    }


}
