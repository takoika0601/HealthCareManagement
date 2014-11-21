package jp.co.akiguchilab.healthcaremanagement.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CountReceiver extends BroadcastReceiver {
    private static final String TAG = CountReceiver.class.getSimpleName();

    public CountReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO 自動生成されたメソッド・スタブ
        //サービスからブロードキャストされたintentを受け取った時に呼び出される
        String action = intent.getAction();

		/*
        //電源オン時に呼び出される
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			context = context.getApplicationContext();
			Intent service = new Intent(context, CountService.class);
			context.startService(service);
		}
		*/
    }
}
