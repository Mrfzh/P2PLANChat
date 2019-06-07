package com.feng.p2planchat.base;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/7
 */
public class BasePresenter<V> {
    private V view;

    public void attachView(V view){
        this.view = view;
    }

    //不需要传参数
    public void detachView(){
        this.view = null;
    }

    protected  boolean isAttachView(){
        return view != null;
    }

    protected V getMvpView(){
        return view;
    }
}
