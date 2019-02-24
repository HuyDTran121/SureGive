package com.name.filler.suregive;

import android.app.Application;

public class MyApplication extends Application {

    private byte[] imgArr;

    public byte[] getProfile() {
        return imgArr;
    }

    public void setProfile(byte[] imgArr) {
        this.imgArr = imgArr;
    }
}