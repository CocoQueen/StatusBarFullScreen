package com.snxyfresh.app.snxyfresh.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;
import com.snxyfresh.app.snxyfresh.R;
import com.snxyfresh.app.snxyfresh.utils.ToastUtils;
import com.snxyfresh.app.snxyfresh.utils.statusbar.StatusBarUtils;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    //获取TAG的activity名称
    protected final String TAG = this.getClass().getSimpleName();
    MyActivityManager activityManager;
    private View decorView;
    private FrameLayout rootview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityManager = MyActivityManager.getInstance();
        activityManager.pushActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        Logger.d(TAG);
        Logger.d(TAG + "onCreate");
        setContentView(R.layout.activity_base);
        View view = LayoutInflater.from(this).inflate(getLayoutId(), null, false);
        //获取底层视图
        decorView = getWindow().getDecorView();
        rootview = findViewById(R.id.root_view);
        rootview.addView(view);
        Logger.d("push activity" + activityManager.toString());
        StatusBarUtils.setStatusBar(this, false, false);
        //初始化控件
        initView();
        // 添加监听器
        initListener();


    }
    public FrameLayout getRootView() {
        return rootview;
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化布局
     */
    public abstract void initView();

    // 添加监听器
    protected abstract void initListener();

    /**
     * 显示短toast
     *
     * @param msg
     */
    public void ToastShort(String msg) {
        ToastUtils.show(getApplicationContext(), msg, ToastUtils.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(TAG + "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(TAG + "Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d(TAG + "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d(TAG + "onStop");
    }

    @Override
    protected void onDestroy() {
        Logger.d(TAG + "onDestroy");
        activityManager.removeActivity(this);
        Logger.d("onDestroy: push activity  activitys:" + activityManager.toString());
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
