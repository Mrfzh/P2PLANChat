package com.feng.p2planchat.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.feng.p2planchat.util.ToastUtil;



/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

//    private Unbinder unBinder;
    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isHideTitleBar()) {
            //隐藏标题栏
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        doBeforeSetContentView();
        setContentView(getLayoutId());
//        unBinder = ButterKnife.bind(this);  //绑定ButterKnife

        mPresenter = getPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }

//        if (isRegisterEventBus()) {
//            EventBusUtil.register(this);
//        }

        initData();
//        initToolbar();
        initView();
        doInOnCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (unBinder != null && unBinder != Unbinder.EMPTY) {
//            unBinder.unbind();
//            unBinder = null;
//        }

        if (mPresenter != null) {
            mPresenter.detachView();
        }

//        if (isRegisterEventBus()) {
//            EventBusUtil.unregister(this);
//        }
    }

    /**
     * 在setContentView方法前的操作
     */
    protected abstract void doBeforeSetContentView();

    /**
     *  获取当前活动的布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 获取当前活动的Presenter
     *
     * @return Presenter实例
     */
    protected abstract T getPresenter();


    /**
     * 初始化数据
     */
    protected abstract void initData();
//
//    /**
//     * 获取toolbar实例
//     *
//     * @return toolbar实例
//     */
//    protected abstract Toolbar getToolbar();
//
//    /**
//     * 是否设置toolbar左上角含返回按钮，没有toolbar请返回false
//     *
//     * @return
//     */
//    protected abstract boolean setToolbarBackIcon();
//
//    protected void initToolbar() {
//        Toolbar toolbar = getToolbar();
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
////            toolbar.setTitleTextAppearance(this, R.style.ToolbarTitle);    //设置标题字体样式
//            if (setToolbarBackIcon()) {
//                toolbar.setNavigationIcon(R.mipmap.back);
//                toolbar.setNavigationOnClickListener(v -> finish());
//            }
//        }
//    }

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化数据和视图后再OnCreate方法中的操作
     */
    protected abstract void doInOnCreate();

    /**
     * 是否隐藏标题栏
     *
     * @return
     */
    protected abstract boolean isHideTitleBar();

    /**
     * 弹出Toast
     *
     * @param content 弹出的内容
     */
    protected void showShortToast(String content) {
        ToastUtil.showToast(this, content);
    }

//    /**
//     * 设置toolbar的标题
//     *
//     * @param title 标题
//     */
//    protected void setToolbarTitle(String title) {
//        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
//    }

//    /**
//     * 设置状态栏的颜色
//     *
//     * @param color 颜色(R.color.xxx)
//     */
//    protected void setStateBarColor(int color) {
//        BaseUtils.setStatusBarColor(this, color);
//    }

//    /**
//     * 设置toolbar的颜色
//     *
//     * @param toolbar 要设置的toolbar
//     * @param color 颜色(R.color.xxx)
//     */
//    protected void setToolbarColor(Toolbar toolbar, int color) {
//        toolbar.setBackgroundColor(getResources().getColor(color));
//    }

    /**
     * 跳转到另一活动
     *
     * @param activity 新活动.class
     */
    protected void jumpToNewActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    /**
     * 带Bundle的跳转活动
     *
     * @param activity 新活动.class
     * @param bundle
     */
    protected void jump2ActivityWithBundle(Class activity, Bundle bundle) {
        Intent intent = new Intent(this, activity);
        startActivity(intent, bundle);
    }


//    /**
//     * 是否注册EventBus，注册后才可以订阅事件
//     *
//     * @return true表示绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
//     */
//    protected boolean isRegisterEventBus() {
//        return false;
//    }
}
