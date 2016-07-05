package com.lean.livebox.core;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by lean on 16/7/5.
 */
public class Live implements Serializable {
    private Platform platform;
    private String roomId;
    private String roomName;
    private String roomLogo;
    private boolean online;
    private long onlineCount;

    private String ownerName;
    private String ownerAvatar;

    private String normalUrl;
    private String hdUrl;
    private String sdUrl;
    private String m3u8Url;

    public Live() {
    }

    public Live(Platform platform) {
        this.platform = platform;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomLogo() {
        return roomLogo;
    }

    public void setRoomLogo(String roomLogo) {
        this.roomLogo = roomLogo;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public String getNormalUrl() {
        return normalUrl;
    }

    public void setNormalUrl(String normalUrl) {
        this.normalUrl = normalUrl;
    }

    public String getHdUrl() {
        return hdUrl;
    }

    public void setHdUrl(String hdUrl) {
        this.hdUrl = hdUrl;
    }

    public String getSdUrl() {
        return sdUrl;
    }

    public void setSdUrl(String sdUrl) {
        this.sdUrl = sdUrl;
    }

    public String getM3u8Url() {
        return m3u8Url;
    }

    public void setM3u8Url(String m3u8Url) {
        this.m3u8Url = m3u8Url;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
