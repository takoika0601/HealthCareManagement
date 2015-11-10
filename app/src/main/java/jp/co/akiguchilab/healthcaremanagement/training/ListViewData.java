package jp.co.akiguchilab.healthcaremanagement.training;

import android.graphics.Bitmap;

public class ListViewData {
    private Bitmap bitmap;
    private String title;
    private String path;

    public void setBitmap(Bitmap img) {
        bitmap = img;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setTitle(String msg) {
        title = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
