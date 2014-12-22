package jp.co.akiguchilab.healthcaremanagement.service;

import android.os.Binder;

public class CountBinder extends Binder{
    private static final String TAG = CountBinder.class.getSimpleName();

    CountService service;

    public CountBinder(CountService service) {
        this.service = service;
    }

    public CountService getService() {
        return service;
    }
}
