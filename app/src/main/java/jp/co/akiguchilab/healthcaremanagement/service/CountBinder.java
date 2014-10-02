package jp.co.akiguchilab.healthcaremanagement.service;

import android.os.Binder;

public class CountBinder extends Binder{
    private static final String TAG = CountBinder.class.getSimpleName();

    CountService s;

    public CountBinder(CountService s) {
        this.s = s;
    }

    public CountService getService() {
        return s;
    }
}
