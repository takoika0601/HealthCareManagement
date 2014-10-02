package jp.co.akiguchilab.healthcaremanagement.training;

import android.graphics.Bitmap;

public class ListViewData {
    private Bitmap bitmap;
    private String string;

    public void setBitmap(Bitmap img) {
        bitmap = img;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setString(String msg) {
        string = msg;
    }

    public String getString() {
        return string;
    }
}
