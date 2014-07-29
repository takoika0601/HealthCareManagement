package jp.co.akiguchilab.healthcaremanagement.training;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.akiguchilab.healthcaremanagement.R;

public class SettingViewActivity extends Activity implements View.OnClickListener {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting_view);

        Button zero = (Button) findViewById(R.id.settingButton0);
        Button one = (Button) findViewById(R.id.settingButton1);
        Button two = (Button) findViewById(R.id.settingButton2);
        Button three = (Button) findViewById(R.id.settingButton3);
        Button four = (Button) findViewById(R.id.settingButton4);
        Button five = (Button) findViewById(R.id.settingButton5);
        Button six = (Button) findViewById(R.id.settingButton6);
        Button seven = (Button) findViewById(R.id.settingButton7);
        Button eight = (Button) findViewById(R.id.settingButton8);
        Button nine = (Button) findViewById(R.id.settingButton9);
        Button clear = (Button) findViewById(R.id.settingButtonClear);
        Button register = (Button) findViewById(R.id.settingButtonRegister);

        zero.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        clear.setOnClickListener(this);
        register.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.settingText1);
        TextView subtitle = (TextView) findViewById(R.id.settingText2);
        TextView counter = (TextView) findViewById(R.id.settingText3);

        title.setText(getIntent().getExtras().getString("title"));
        subtitle.setText(getIntent().getExtras().getString("subtitle"));
        counter.setText(getIntent().getExtras().getString("counter"));
    }

    @Override
    public void onClick(View v) {
        editText = (EditText) findViewById(R.id.settingEdit);
        switch (v.getId()) {
            case R.id.settingButton0:
                if (!(editText.getText().toString().equals("")))   editText.setText(editText.getText() + "0");
                break;
            case R.id.settingButton1:
                editText.setText(editText.getText() + "1");
                break;
            case R.id.settingButton2:
                editText.setText(editText.getText() + "2");
                break;
            case R.id.settingButton3:
                editText.setText(editText.getText() + "3");
                break;
            case R.id.settingButton4:
                editText.setText(editText.getText() + "4");
                break;
            case R.id.settingButton5:
                editText.setText(editText.getText() + "5");
                break;
            case R.id.settingButton6:
                editText.setText(editText.getText() + "6");
                break;
            case R.id.settingButton7:
                editText.setText(editText.getText() + "7");
                break;
            case R.id.settingButton8:
                editText.setText(editText.getText() + "8");
                break;
            case R.id.settingButton9:
                editText.setText(editText.getText() + "9");
                break;
            case R.id.settingButtonClear:
                editText.setText("");
                break;
            case R.id.settingButtonRegister:
                Toast.makeText(this, "Register", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }
}
