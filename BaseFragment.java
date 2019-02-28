package com.snxyfresh.app.snxyfresh.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

public abstract class BaseFragment extends Fragment {
    public final String TAG = getClass().getSimpleName();
    private View rootView;
    private FragmentActivity fragmentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(setContentView(), container, false);
        // TODO: 2018/11/7 在布局绑定之后进行一些必要的绑定操作
        initFragmentOK();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("onResume" + TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d("onPause" + TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // TODO: 2018/11/7 这里进行一些必要的解绑操作
    }

    public abstract void initFragmentOK();

    public abstract int setContentView();

    public abstract void onReceiveLoginState(boolean isLogin);//判断登录状态  已登录or未登录

}
