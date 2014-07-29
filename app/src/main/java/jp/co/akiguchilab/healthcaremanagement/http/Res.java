package jp.co.akiguchilab.healthcaremanagement.http;

import android.os.Bundle;

public class Res {
    private static final String TAG = Res.class.getSimpleName();

    int id;
    boolean success;
    int status;
    String text;
    Bundle bundle;

    public Res(int id, boolean success, int status, String text, Bundle bundle) {
        this.id = id;
        this.success = success;
        this.status = status;
        this.text = text;
        this.bundle = bundle;
    }
}
