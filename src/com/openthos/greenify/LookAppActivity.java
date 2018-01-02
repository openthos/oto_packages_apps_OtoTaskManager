package com.openthos.greenify;

import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

import com.openthos.greenify.adapter.AppLayoutAdapter;
import com.openthos.greenify.entity.AppInfo;
import com.openthos.greenify.entity.AppLayoutInfo;
import com.openthos.greenify.listener.OnListClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookAppActivity extends BaseActivity
        implements Toolbar.OnMenuItemClickListener, OnListClickListener {

    private ListView mListView;
    private AppLayoutAdapter mAdapter;
    private Toolbar mToolbar;
    //A collection used to store selected applications
    private Map<View, String> mSelectMap;
    private List<AppLayoutInfo> mDatas;
    //A collection for storing more applications
    private List<AppInfo> mMoreAppInfos;
    //A collection used to store running applications
    private List<AppInfo> mRunAppInfos;
    //Set to store a set that may slow down the response speed of the device
    private List<AppInfo> mAffectAppInfos;

    @Override
    public int getLayoutId() {
        return R.layout.activity_look_app;
    }

    @Override
    public void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mListView = (ListView) findViewById(R.id.listview);
    }

    @Override
    public void initData() {
        mToolbar.setLogo(R.mipmap.ic_icon);
        mToolbar.setTitle(R.string.app_look_adviser);
        mToolbar.setNavigationIcon(R.mipmap.back);
        setActionBar(mToolbar);
        mDatas = new ArrayList<>();
        mAdapter = new AppLayoutAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);
        mSelectMap = new HashMap<>();

        mMoreAppInfos = new ArrayList<>();
        mRunAppInfos = new ArrayList<>();
        mAffectAppInfos = new ArrayList<>();
        loadDatas();
    }

    @Override
    public void initListener() {
        mAdapter.setOnListClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(FROM_LOOK_APP_ACTIVITY,
                        new Intent(LookAppActivity.this, MainActivity.class));
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(this);
    }

    /**
     * Initialization of the data required to display
     */
    private void loadDatas() {
        mDatas.clear();
        mMoreAppInfos.clear();
        mRunAppInfos.clear();
        mAffectAppInfos.clear();
        Map<String, AppInfo> notSystemApps = getNotSystemApps();
        for (String packageName : notSystemApps.keySet()) {
            AppInfo appInfo = getAppInfoByPkgName(packageName);
            if (appInfo != null && !appInfo.isAdd()) {
                mMoreAppInfos.add(appInfo);
                if (appInfo.isRun()) {
                    mRunAppInfos.add(appInfo);
                }
                if (appInfo.isAffect()) {
                    mAffectAppInfos.add(appInfo);
                }
            }
        }

        if (mRunAppInfos.size() != 0) {
            mDatas.add(new AppLayoutInfo(getString(R.string.runnig_application), mRunAppInfos));
        }
        if (mAffectAppInfos.size() != 0) {
            mDatas.add(new AppLayoutInfo(getString(R.string.affect_application), mMoreAppInfos));
        }
        if (mMoreAppInfos.size() != 0) {
            mDatas.add(new AppLayoutInfo(getString(R.string.more_application), mMoreAppInfos));
        }
        mAdapter.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.look_menu_unselect, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_confirm:
                for (View view : mSelectMap.keySet()) {
                    addSleepList(mSelectMap.get(view), true);
                }
                setResult(FROM_LOOK_APP_ACTIVITY,
                        new Intent(LookAppActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.l_show_all:
                loadDatas();
                break;
        }
        return false;
    }

    /**
     * custom interface   Get the clicked view information
     *
     * @param view
     * @param packageName
     */
    @Override
    public void onListClickListener(View view, String packageName) {
        if (mSelectMap.containsKey(view)) {
            view.setBackgroundColor(Color.TRANSPARENT);
            mSelectMap.remove(view);
        } else {
            view.setBackgroundColor(Color.BLUE);
            mSelectMap.put(view, packageName);
        }
        if (mSelectMap.size() == 1) {
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(R.menu.menu_confirm_select);
        } else if (mSelectMap.isEmpty()) {
            mToolbar.getMenu().clear();
            mToolbar.inflateMenu(R.menu.look_menu_unselect);
        }
    }
}
