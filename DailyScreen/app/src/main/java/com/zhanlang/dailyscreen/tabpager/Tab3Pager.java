package com.zhanlang.dailyscreen.tabpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jpeng.jptabbar.JPTabBar;
import com.zhanlang.dailyscreen.MainActivity;
import com.zhanlang.dailyscreen.R;
import com.zhanlang.dailyscreen.widget.ProgressWebView;

/**
 * Tab3Pager界面，就是显示一个网页WebView,TODO 后期在WebView顶部加一个进度条
 * Created by jpeng on 16-11-14.
 */
public class Tab3Pager extends Fragment {
    JPTabBar mTabBar;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab3,null);
        init(layout);
        return layout;
    }

    /**
     * 初始化
     */
    private void init(View layout) {
        mTabBar = ((MainActivity)getActivity()).getTabbar();

        ProgressWebView webView=(ProgressWebView) layout.findViewById(R.id.webview);
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        });
        webView.loadUrl("https://chushou.tv/");
    }


}
