package jp.co.akiguchilab.healthcaremanagement.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.akiguchilab.healthcaremanagement.R;

public class CameraActivity extends Activity implements View.OnClickListener {
    private static final String TAG = CameraActivity.class.getSimpleName();

    private static final int MEDIA_TYPE_IMAGE = 100;
    private static final int IMAGE_CAPTURE = 50;
    private static final int REQUEST_GALLARY = 60;
    private static final String KEY_IMAGE_URI = "KEY";

    private Uri mImageUri;
    private SharedPreferences sharedPreferences;
    private static ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = (ImageView) findViewById(R.id.camera_image);

        Button breakfast = (Button) findViewById(R.id.camera_breakfast_button);
        Button lunch = (Button) findViewById(R.id.camera_lunch_button);
        Button dinner = (Button) findViewById(R.id.camera_dinner_button);

        breakfast.setOnClickListener(this);
        lunch.setOnClickListener(this);
        dinner.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {
            case R.id.camera_breakfast_button:
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLARY);
                break;

            case R.id.camera_lunch_button:
                mImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                if (sharedPreferences == null) {
                    sharedPreferences = getPreferences(MODE_PRIVATE);
                }
                Editor editor = sharedPreferences.edit();
                editor.putString("mImageUri", mImageUri.toString());
                editor.commit();

                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, IMAGE_CAPTURE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode != RESULT_OK) {
                if (sharedPreferences == null) {
                    sharedPreferences = getPreferences(MODE_PRIVATE);
                }
                Uri tmpUri = Uri.parse(sharedPreferences.getString("mImageUri", ""));

                if (tmpUri != null) {
                    try {
                        getContentResolver().delete(tmpUri, null, null);
                    } catch (Exception e) {
                        Log.d(TAG, "FEEEEEE");
                    }
                    sharedPreferences.edit().remove("mImageUri");
                }
            } else if (resultCode == RESULT_OK) {
                Uri resultUri = null;

                if (sharedPreferences == null) {
                    sharedPreferences = getPreferences(MODE_PRIVATE);
                }

                if (data != null && data.getData() != null) {
                    resultUri = data.getData();
                } else {
                    // sharedPreferenceに保存しておいたファイルの位置からUriを生成する方法
                    resultUri = Uri.parse(sharedPreferences.getString("mImageUri", ""));

                    //端末内の画像を日付順に並べ、一番新しい画像を取得する方法
                    /*
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = Images.Media.query(contentResolver, Images.Media.EXTERNAL_CONTENT_URI, null, null, Images.ImageColumns.DATE_TAKEN + " DESC");
                    cursor.moveToFirst();

                    String id = cursor.getTitle(cursor.getColumnIndexOrThrow(BaseColumns._ID));
                    mImageUri = Uri.withAppendedPath(Images.Media.EXTERNAL_CONTENT_URI, id);
                    imageView.setImageURI(mImageUri);
                    */
                }

                mImageUri = resultUri;
                imageView.setImageURI(mImageUri);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "RESULT CANCELLED. Back to the CameraActivity");
            } else {
                Log.d(TAG, "Unkown error. Faild");
            }
        } else if (requestCode == REQUEST_GALLARY) {
            try {
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);

                imageView.setImageBitmap(img);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "foooo");
            } catch (NullPointerException e) {
                Log.d(TAG, "No Select");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putParcelable(KEY_IMAGE_URI, mImageUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        mImageUri = (Uri) savedInstanceState.get(KEY_IMAGE_URI);
        imageView.setImageURI(mImageUri);
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HealthCareManagement");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
