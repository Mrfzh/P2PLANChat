package com.feng.p2planchat.contract;

import android.content.Context;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/14
 */
public interface IModifyNameContract {
    interface View {
        void modifyNameSuccess();
        void modifyNameError(String errorMsg);
    }
    interface Presenter {
        void modifyNameSuccess();
        void modifyNameError(String errorMsg);
        void modifyName(List<String> otherUserIpList, String oldName, String newName, Context context);
    }
    interface Model {
        void modifyName(List<String> otherUserIpList, String oldName, String newName, Context context);
    }
}
