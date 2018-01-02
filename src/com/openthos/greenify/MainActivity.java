package com.openthos.greenify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

import com.openthos.greenify.adapter.AppLayoutAdapter;
import com.openthos.greenify.entity.AppInfo;
import com.openthos.greenify.entity.AppLayoutInfo;
import com.openthos.greenify.listener.OnListClickListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements OnListClickListener {

    private Toolbar mToolbar;
    private List<AppLayoutInfo> mDatas;
    private ListView mListView;
    private AppLayoutAdapter mAdapter;
    //Storage already dormant application
    private List<AppInfo> mHaveSleeps;
    //Storage of non dormant applications
    private List<AppInfo> mWaitSleeps;
    private View mLastView;
    //Currently selected application package name
    private String mSelectPkgName;
    private ScreenStatusReceiver mScreenStatusReceiver;
    private Handler mHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mListView = (ListView) findViewById(R.id.listview);
    }

    @Override
    public void initData() {
        mHandler = new Handler();
        registSreenStatusReceiver();
        initAppInfos();
        mToolbar.setLogo(R.mipmap.ic_icon);
        mToolbar.setTitle(R.string.app_name);
        setActionBar(mToolbar);

        mDatas = new ArrayList<>();
        mAdapter = new AppLayoutAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        mHaveSleeps = new ArrayList<>();
        mWaitSleeps = new ArrayList<>();
        loadData();
    }

    @Override
    public void initListener() {
        mAdapter.setOnListClickListener(this);
    }

    /**
     * Initialization of data
     */
    public void loadData() {
        mWaitSleeps.clear();
        mHaveSleeps.clear();
        mDatas.clear();

        Map<String, AppInfo> notSystemApps = getNotSystemApps();
        for (String packageName : notSystemApps.keySet()) {
            AppInfo appInfo = getAppInfoByPkgName(packageName);
            if (appInfo != null && appInfo.isAdd()) {
                if (appInfo.isSleep()) {
                    mHaveSleeps.add(appInfo);
                } else {
                    mWaitSleeps.add(appInfo);
                }
            }
        }

        if (mHaveSleeps.size() != 0) {
            mDatas.add(new AppLayoutInfo(getString(R.string.have_sleep), mHaveSleeps));
        }
        if (mWaitSleeps.size() != 0) {
            mDatas.add(new AppLayoutInfo(getString(R.string.wait_sleep), mWaitSleeps));
        }
        mAdapter.refresh();
        MenuItem menuItem = mToolbar.getMenu().findItem(R.id.m_remove);
        if (menuItem != null) {
            menuItem.setVisible(false);
        }
        mSelectPkgName = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_refresh:
                refreshApps();
                break;
            case R.id.m_sleep:
                sleepApp();
                break;
            case R.id.m_start:
                runApp();
                break;
            case R.id.m_add:
                addSleepApp();
                break;
            case R.id.m_remove:
                removeSleepApp();
                break;
            case R.id.m_auto_sleep:
                setAutoSleep();
                break;
            case R.id.m_about:
                aboutApp();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Refurbishing the list of applications for the display
     */
    private void refreshApps() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 500);
    }

    /**
     * The code logic that makes the application dormant
     */
    private void sleepApp() {
        if (mSelectPkgName == null) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                AppInfo appInfo = getAppInfoByPkgName(mSelectPkgName);
                forceStopAPK(appInfo.getPackageName());
                refreshApps();
            }
        }.start();
    }

    /**
     * Kill the application process
     *
     * @param pkgName
     */
    private void forceStopAPK(String pkgName) {
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "am force-stop " + pkgName + "\n";
            os.writeBytes(Command);
            os.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Add applications to the dormancy list
     */
    private void addSleepApp() {
        startActivityForResult(new Intent(MainActivity.this,
                LookAppActivity.class), FROM_MAIN_ACTIVITY);
    }

    /**
     * Remove from the dormancy list
     */
    private void removeSleepApp() {
        addSleepList(mSelectPkgName, false);
        refreshApps();
    }

    /**
     * Start the selected application
     */
    private void runApp() {
        if (mSelectPkgName == null) {
            return;
        }
        AppInfo appInfo = getAppInfoByPkgName(mSelectPkgName);
        startActivity(appInfo.getIntent(this));
        refreshApps();
    }

    /**
     * Set whether to open auto dormancy
     */
    private void setAutoSleep() {
        //TODO
    }

    /**
     * On the application
     */
    private void aboutApp() {
        //TODO
    }

    /**
     * Registered dormant broadcast
     */
    private void registSreenStatusReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        if (mScreenStatusReceiver != null) {
            unregisterReceiver(mScreenStatusReceiver);
            mScreenStatusReceiver = null;
        }
        super.onDestroy();
    }

    /**
     * Data callback after adding application
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FROM_MAIN_ACTIVITY && resultCode == FROM_LOOK_APP_ACTIVITY) {
            loadData();
        }
    }

    /**
     * Clicking callbacks applied in listview
     *
     * @param view
     * @param packageName
     */
    @Override
    public void onListClickListener(View view, String packageName) {
        view.setBackgroundColor(Color.BLUE);
        mSelectPkgName = packageName;
        mToolbar.getMenu().findItem(R.id.m_remove).setVisible(true);
        if (mLastView != null && mLastView != view) {
            mLastView.setBackgroundColor(Color.TRANSPARENT);
        }
        setMenuItemRunOrSleep(getNotSystemApps().get(packageName).isSleep());
        mLastView = view;
    }

    /**
     * Determine the menu item based on whether the application has been dormant
     *
     * @param isSleep
     */
    private void setMenuItemRunOrSleep(boolean isSleep) {
        if (isSleep) {
            mToolbar.getMenu().findItem(R.id.m_start).setVisible(true);
            mToolbar.getMenu().findItem(R.id.m_sleep).setVisible(false);
        } else {
            mToolbar.getMenu().findItem(R.id.m_start).setVisible(false);
            mToolbar.getMenu().findItem(R.id.m_sleep).setVisible(true);
        }
    }

    /**
     * Listening to a dormant broadcast
     * The application of auto dormancy into the dormancy list when dormancy
     * Automatically refresh the screen when you open the screen
     */
    private class ScreenStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:
                    Map<String, AppInfo> notSystemApps = getNotSystemApps();
                    for (String packageName : notSystemApps.keySet()) {
                        AppInfo appInfo = notSystemApps.get(packageName);
                        if (appInfo.isAdd() && !appInfo.isSleep()) {
                            forceStopAPK(packageName);
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_ON:
                    refreshApps();
                    break;
            }
        }
    }
}