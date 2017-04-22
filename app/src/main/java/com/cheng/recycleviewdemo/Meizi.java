package com.cheng.recycleviewdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2017-03-09.
 */

public class Meizi implements Parcelable {
    private String url;
    private int page;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(page);
    }

    public static final Parcelable.Creator<Meizi> CREATOR = new Parcelable.Creator<Meizi>() {
        @Override
        public Meizi createFromParcel(Parcel source) {
            Meizi meizi = new Meizi();
            meizi.setUrl(source.readString());
            meizi.setPage(source.readInt());
            return meizi;
        }

        @Override
        public Meizi[] newArray(int size) {
            return new Meizi[size];
        }
    };
}
