package com.idreamspace.skindetection.lifecycle;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppVIewModel extends ViewModel {
    private MutableLiveData<String> qrcode;

    private MutableLiveData<String> openid;

    public MutableLiveData<String> getQrcode() {
        if (qrcode == null) {
            qrcode = new MutableLiveData<>();
        }
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode.setValue(qrcode);
    }

    public MutableLiveData<String> getOpenid() {
        if (openid == null) {
            openid = new MutableLiveData<>();
        }
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid.setValue(openid);
    }

}
