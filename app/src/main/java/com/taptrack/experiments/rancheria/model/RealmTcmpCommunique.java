package com.taptrack.experiments.rancheria.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class RealmTcmpCommunique extends RealmObject {

    @PrimaryKey
    private String communiqueId;
    @Required
    @Index
    private Long messageTime;
    @Required
    private String deviceId;
    @Required
    private String deviceName;
    @Required
    private Boolean isCommand;
    @Required
    private byte[] message;

    public String getCommuniqueId() {
        return communiqueId;
    }

    public void setCommuniqueId(String communiqueId) {
        this.communiqueId = communiqueId;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getCommand() {
        return isCommand;
    }

    public void setCommand(Boolean command) {
        isCommand = command;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
