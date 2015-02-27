package jp.co.akiguchilab.healthcaremanagement.training;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by i09324 on 2015/02/27.
 */
public class TrainingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView();
    }
}
