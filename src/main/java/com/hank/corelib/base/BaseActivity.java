package com.hank.corelib.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by renlei on 2016/5/23.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    //布局文件ID
    protected abstract int getContentViewId();
    //view变量与layout对应
    protected abstract void findViews();
    //初始化view的值
    protected abstract void init();
    //为view设置监听
    protected abstract void setListeners();

    protected <T extends View> T $(int id) {
        return (T) super.findViewById(id);
    }

    protected CompositeSubscription mCompositeSubscription;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getContentViewId();
        if(layoutId<=0){
            throw new IllegalArgumentException("the layout resource must be initialized first");
        }
        setContentView(layoutId);
        findViews();
        init();
        setListeners();
        ActivityManager.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onUnsubscribe();
        ActivityManager.getInstance().finishActivity(this);
    }

    //RXjava取消注册，以避免内存泄露
    public void onUnsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }


    public void addSubscription(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
}
