package com.snxy.app.merchant_manager.module.view.indoormodule.mvp.view.activity;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.snxy.app.merchant_manager.R;
import com.snxy.freshfood.common.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderInfoActivity2 extends BaseActivity {
    private View statusBarView;
    //
//
//    @BindView(R.id.finsh)
//    ImageView finsh;
//    @BindView(R.id.rr)
//    RelativeLayout rr;
//    @BindView(R.id.t)
//    TextView t;
//    @BindView(R.id.type)
//    TextView type;
//    @BindView(R.id.numer)
//    TextView numer;
//    @BindView(R.id.car_type)
//    TextView carType;
//    @BindView(R.id.car_id)
//    TextView carId;
//    @BindView(R.id.shoufei)
//    TextView shoufei;
//    @BindView(R.id.jinmenfei)
//    TextView jinmenfei;
//    @BindView(R.id.yajin)
//    TextView yajin;
//    @BindView(R.id.zongjine)
//    TextView zongjine;
//    @BindView(R.id.jname)
//    TextView jname;
//    @BindView(R.id.phone)
//    TextView phone;
//    @BindView(R.id.men)
//    TextView men;
//    @BindView(R.id.chedao)
//    TextView chedao;
//    @BindView(R.id.jindate)
//    TextView jindate;
//    @BindView(R.id.jianceimage)
//    ImageView jianceimage;
//    @BindView(R.id.ll1)
//    LinearLayout ll1;
//    @BindView(R.id.chandimage)
//    ImageView chandimage;
//    @BindView(R.id.ll2)
//    LinearLayout ll2;
//    @BindView(R.id.huopinimage)
//    ImageView huopinimage;
//    @BindView(R.id.ll3)
//    LinearLayout ll3;
//    @BindView(R.id.zhuangtai)
//    TextView zhuangtai;
//    @BindView(R.id.beizhu)
//    TextView beizhu;
//    @BindView(R.id.sc)
//    ScrollView sc;

    @Override
    public String initActionBar() {
        return null;
    }

    @Override
    public int getRootView() {
        return R.layout.activity_order_info3;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (isAllScreenDevice(this)) {
            //设置状态栏颜色
            getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    initStatusBar();
                    getWindow().getDecorView().removeOnLayoutChangeListener(this);
                }
            });
        } else {
            ImmersionBar.with(this).init();
        }

    }

    private void initStatusBar() {
        if (statusBarView == null) {
            //利用反射机制修改状态栏背景
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);
        }
        if (statusBarView != null) {
            statusBarView.setBackgroundResource(R.drawable.mine_head_green);
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 判断是否是全面屏
     */
    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;

    public static boolean isAllScreenDevice(Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isAllScreenDevice(this)) {
            ImmersionBar.with(this).destroy();
        }
    }
}
