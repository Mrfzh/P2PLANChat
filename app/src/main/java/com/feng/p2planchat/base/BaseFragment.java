package com.feng.p2planchat.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feng.p2planchat.util.EventBusUtil;
import com.feng.p2planchat.util.ToastUtil;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/9
 */
public abstract class BaseFragment<V extends BasePresenter> extends Fragment {

    protected V mPresenter;   //该Fragment对应的Presenter

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = getPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }

        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }

        initData();
        doInOnCreate();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }

        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 在onCreate方法中执行的操作
     */
    protected abstract void doInOnCreate();

    /**
     * 获取fragment布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 获取Presenter实例
     *
     * @return 相应的Presenter实例
     */
    protected abstract V getPresenter();

    /**
     * 显示短toast
     *
     * @param content
     */
    protected void showShortToast(String content) {
        ToastUtil.showToast(getContext(), content);
    }

    /**
     * 跳转活动
     *
     * @param activity
     */
    protected void jump2Activity(Class activity) {
        startActivity(new Intent(getContext(), activity));
    }

    /**
     * 带Bundle的跳转活动
     *
     * @param activity 新活动.class
     * @param bundle
     */
    protected void jump2ActivityWithBundle(Class activity, Bundle bundle) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent, bundle);
    }

    /**
     * 是否注册EventBus，注册后才可以订阅事件
     *
     * @return true表示绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

}
