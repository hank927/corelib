package com.hank.corelib.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;


/**
 * Created by Hank on 2016/5/23.
 */
public abstract class BaseFragment extends Fragment {
    //避免getActivity返回为空
    protected BaseActivity mActivity;
    //获取fragment布局文件ID
    protected abstract int getLayoutId();
    //初始化view的值
    protected abstract void init();
    //为view设置监听
    protected abstract void setListeners();
    //加载数据
    protected abstract void loadData();
    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this,view);
        init();
        setListeners();
        loadData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
