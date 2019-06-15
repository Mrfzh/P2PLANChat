package com.feng.p2planchat.entity.serializable;

import java.io.Serializable;
import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/15
 */
public class OtherUserIp implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> otherUserIpList;

    public OtherUserIp(List<String> otherUserIpList) {
        this.otherUserIpList = otherUserIpList;
    }

    public List<String> getOtherUserIpList() {
        return otherUserIpList;
    }

    public void setOtherUserIpList(List<String> otherUserIpList) {
        this.otherUserIpList = otherUserIpList;
    }
}
