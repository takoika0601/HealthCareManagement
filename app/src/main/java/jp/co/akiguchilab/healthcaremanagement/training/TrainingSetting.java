package jp.co.akiguchilab.healthcaremanagement.training;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import jp.co.akiguchilab.healthcaremanagement.R;
import jp.co.akiguchilab.healthcaremanagement.geneticalgorithm.GA;

public class TrainingSetting extends Activity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.trainingsetting);

        Button setting = (Button) findViewById(R.id.setting);
        Button counter = (Button) findViewById(R.id.counter);
        Button add = (Button) findViewById(R.id.add);
        setting.setOnClickListener(this);
        counter.setOnClickListener(this);
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.setting:
                break;
            case R.id.counter:
                intent.setClass(this, SettingViewActivity.class);
                intent.putExtra("title", "目標回数を入力してください");
                intent.putExtra("subtitle", "目標回数");
                intent.putExtra("counter", "回");
                startActivity(intent);
            case R.id.add:
                intent.setClass(this, GA.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE:
                break;

            default:
                break;
        }
    }
}

