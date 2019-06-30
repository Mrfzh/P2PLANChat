package com.feng.p2planchat.entity.eventbus;

import com.feng.p2planchat.entity.serializable.ChatData;

/**
 * @author Feng Zhaohao
 * Created on 2019/6/30
 */
public class FileEvent {
    private String fileName;    //当前传输的文件名
    private int process;        //当前传输进度
    private String ip;          //文件的源IP地址
    private ChatData chatData;  //当前传输的文件信息
    private boolean isUpdateProcess;    //是否更新进度

    //更新进度
    public FileEvent(String ip, String fileName, int process) {
        this.ip = ip;
        this.fileName = fileName;
        this.process = process;
        isUpdateProcess = true;
    }

    //聊天界面新建传输信息item
    public FileEvent(ChatData chatData) {
        this.chatData = chatData;
        isUpdateProcess = false;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public ChatData getChatData() {
        return chatData;
    }

    public void setChatData(ChatData chatData) {
        this.chatData = chatData;
    }

    public boolean isUpdateProcess() {
        return isUpdateProcess;
    }

    public void setUpdateProcess(boolean updateProcess) {
        isUpdateProcess = updateProcess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
