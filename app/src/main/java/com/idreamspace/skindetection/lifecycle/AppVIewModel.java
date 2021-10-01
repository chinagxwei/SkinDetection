package com.idreamspace.skindetection.lifecycle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class AppVIewModel extends ViewModel {
    private MutableLiveData<String> qrcode = new MutableLiveData<>();

    private MutableLiveData<String> openid = new MutableLiveData<>();

    private MutableLiveData<File> photo = new MutableLiveData<>();

    public LiveData<String> getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode.setValue(qrcode);
    }

    public LiveData<String> getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid.setValue(openid);
    }

    public LiveData<File> getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo.setValue(photo);
    }
}
