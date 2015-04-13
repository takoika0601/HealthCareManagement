package jp.co.akiguchilab.healthcaremanagement;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.todddavies.components.progressbar.ProgressWheel;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import jp.co.akiguchilab.healthcaremanagement.calendar.CalendarActivity;
import jp.co.akiguchilab.healthcaremanagement.camera.CameraActivity;
import jp.co.akiguchilab.healthcaremanagement.http.HttpExec;
import jp.co.akiguchilab.healthcaremanagement.service.CountBinder;
import jp.co.akiguchilab.healthcaremanagement.service.CountReceiver;
import jp.co.akiguchilab.healthcaremanagement.service.CountService;
import jp.co.akiguchilab.healthcaremanagement.training.TrainingSelectActivity;
import jp.co.akiguchilab.healthcaremanagement.util.ParseUserInfoFromJSON;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    private String TAG = getPackageName().getClass().getSimpleName();

    boolean inService = false;
    boolean isAuth = false;

    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    private static final int MENU_A = 0;
    private static final int MENU_B = 1;

    class Whoami {
        int id;
        String name;
        String number;
        int pHour;
        int pLong;
        Date authAt;
        boolean useVib = false;
        boolean useCamera = false;

        public Whoami() {
            this.id = 0;
            this.name = "";
            this.number = "";
            this.pHour = 0;
            this.pLong = 0;
            this.authAt = new Date();
            this.useVib = true;
            this.useCamera = true;
        }

        public void setParam(int id, String name, String number, int pHour, int pLong,
                             boolean useVib, Date authAt, boolean useCamera) {
            this.id = id;
            this.name = name;
            this.number = number;
            this.pHour = pHour;
            this.pLong = pLong;
            this.authAt = new Date();
            this.useVib = useVib;
            this.useCamera = useCamera;
            this.authAt = authAt;
        }

        public boolean inTime(int hour) {
            return (hour >= pHour && hour < (pHour + pLong));
        }

        public String infoString() {
            String[] info = new String[]{
                    String.format("number:name = %s:%s", this.number, this.name),
                    String.format("start at %s", DateFormat.format("yyyy-MM-dd(E) kk:mm:ss", this.authAt).toString()),
                    String.format("collection time range %02d:00 - %02d:00", this.pHour, this.pHour + this.pLong),
                    String.format("use Vibrator on update = %s", this.useVib),
            };
            return StringUtils.join(info, "\n");
        }
    }

    Whoami whoami = null;

    ParseUserInfoFromJSON mrJSON = null;

    Context context;
    String url = "";
    SharedPreferences thisPref = null;

    // class型のCountServiceオブジェクトを取得
    Class<CountService> thisService = CountService.class;

    // サービスがアクティブかどうかを判定する関数 ： 返り値boolean型
    private boolean isServiceActive(Class<CountService> which) {
        boolean retValue = false;
        // mSericeName : パッケージ名を除いた単純名を指定
        String mServiceName = which.getSimpleName();

        // ActivityManagerオブジェクトの取得 ： システムサービスを取得
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // Listインタフェースの取得 ： システムが実行中のサービスを取得
        List<RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        // 実行中のサービスがある場合
        if (services != null) {
            // 拡張for文 servicesが続く限り、繰り返し実行
            for (RunningServiceInfo info : services) {
                // サービス名を取得し、後方一致検索をかけ、一致した場合
                if (info.service.getClassName().endsWith(mServiceName)) {
                    retValue = true;
                    break;
                }
            }
        }
        return retValue;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        // サービスが接続されたときに呼び出される
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            countService = ((CountBinder) service).getService();
        }

        // サービスが切断されたときに呼び出される
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO 自動生成されたメソッド・スタブ
            countService = null;
        }
    };

    // SE再生用
    private SoundPool mSoundPool;
    private int mSoundId;

    // 画面更新
    ViewRefresh thread;

    private CountService countService;
    private final CountReceiver receiver = new CountReceiver();
    private HttpExec httpExec = null;
    private HttpExec.callBack atAfterAuth = null;

    // SDカードの有無判定
    String status = Environment.getExternalStorageState();

    public boolean isSdCardMounted() {
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ProgressWheel progressWheel = (ProgressWheel) findViewById(R.id.main_spinner);
        progressWheel.spin();

        LinearLayout cardLinear = (LinearLayout) findViewById(R.id.cardLinear);
        cardLinear.removeAllViews();

        String[] texts = {"補強運動", "カレンダー", "カメラ"};
        int[] images = {R.id.main_danberu, R.id.main_calendar, R.id.main_camera};

        for (int i = 0; i < 3; i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.card_item, null);

            CardView cardView = (CardView) layout.findViewById(R.id.cardview);
            ImageView imageView = (ImageView) layout.findViewById(R.id.cardImage);
            TextView textView = (TextView) layout.findViewById(R.id.cardText);

            imageView.setImageResource(R.drawable.danberu);
            textView.setText(texts[i]);
            cardView.setTag(i);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    switch ((int)v.getTag()) {
                        case 0:
                            intent = new Intent(getApplicationContext(), TrainingSelectActivity.class);
                            startActivity(intent);
                            break;

                        case 1:
                            intent = new Intent(getApplicationContext(), CalendarActivity.class);
                            startActivity(intent);
                            break;

                        case 2:
                            intent = new Intent(getApplicationContext(), CameraActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            });
            cardLinear.addView(layout, i);
        }
        //toolbar.setNavigationIcon(R.attr.navigationIcon);

        ImageButton exasize = (ImageButton) findViewById(R.id.main_danberu);
        ImageButton calendar = (ImageButton) findViewById(R.id.main_calendar);
        ImageButton camera = (ImageButton) findViewById(R.id.main_camera);

        exasize.setOnClickListener(this);
        calendar.setOnClickListener(this);
        camera.setOnClickListener(this);

        context = this;
        whoami = new Whoami();
        thisPref = PreferenceManager.getDefaultSharedPreferences(this);

        inService = false;
        isAuth = false;

        // 初期状態におけるボタンの表示状態の設定
        //    weight.setVisibility(View.INVISIBLE);

        atAfterAuth = new HttpExec.callBack() {
            @Override
            public void onFinished(int id, final boolean success, int status,
                                   final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            if (!text.equals("")) {
                                if (mrJSON.Get(text)) {
                                    Editor editor = thisPref.edit();
                                    editor.putString("JSON", text);
                                    editor.commit();

                                    boolean v = thisPref.getBoolean("useVib", true);
                                    boolean c = thisPref.getBoolean("useCamera", true);

                                    whoami.setParam(mrJSON.id, mrJSON.fullname,
                                            mrJSON.number, mrJSON.pHour, mrJSON.pLong, v, new Date(), c);
                                    isAuth = true;
                                    /*
                                     * Intent start = new Intent();
									 * start.setClass(getApplicationContext(),
									 * thisService); start.putExtra("whoami",
									 * whoami.id); // notice!
									 * start.putExtra("sInterval", 5); //
									 * notice! start.putExtra("phour",
									 * whoami.pHour); start.putExtra("plong",
									 * whoami.pLong); start.putExtra("vibrator",
									 * whoami.useVib);
									 */

                                    if (inService == false) {
                                        serviceController(true);
                                    }

                                    Toast.makeText(
                                            context,
                                            String.format("Welcome %s !",
                                                    whoami.name),
                                            Toast.LENGTH_LONG
                                    ).show();

                                } else {
                                    Toast.makeText(context, "JSON error !?",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(context, "auth failed",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "network error !?",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        httpExec = new HttpExec(this, getLoaderManager(), atAfterAuth);

        mrJSON = new ParseUserInfoFromJSON();

        // 初回起動時の処理
        if (!thisPref.getBoolean("Launched", false)) {
            startUp();

            Editor editor = thisPref.edit();
            editor.putBoolean("Launched", true);
            editor.apply();
        }

        // View decor = this.getWindow().getDecorView();
        // decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
        // View.SYSTEM_UI_FLAG_FULLSCREEN );
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void startUp() {
        //Log.d(TAG, "start_up");

        if (isSdCardMounted()) {
            File directory = Environment.getExternalStorageDirectory();
            //sdcardに書き込めるかどうか
            if (directory.exists()) {
                if (directory.canWrite()) {
                    //書き込める場合、フォルダを作成
                    File file = new File(directory.getAbsolutePath() + "/" + "HealthCare");
                    file.mkdir();
                }
            }

            //補強運動において使用される閾値などを格納するファイルdum.csvを作成する
            String filepath = directory.getAbsolutePath() + "/HealthCare" + "/dum.csv";
            File filecheck = new File(filepath);
            try {
                //ファイルが存在するかどうか
                if (!filecheck.exists()) {
                    BufferedWriter bw = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(filepath, false), "UTF-8")
                    );
                    int filevalue = 0;
                    //filevalue : 使用する軸の設定
                    bw.write("0");
                    bw.newLine();
                    double valueXmin = -1.0;
                    double valueXmax = -0.4;
                    //valueXmax, valueXmin : x軸の最大、最小値の設定
                    bw.write(valueXmax + "," + valueXmin);
                    bw.newLine();
                    double valueY = 0;
                    //valueYmax, valueYmin : y軸の最大、最小値の設定
                    bw.write(valueY + "," + valueY);
                    bw.newLine();
                    double valueZ = 0;
                    //valueZmax, valueZmin : z軸の最大、最小値の設定
                    bw.write(valueZ + "," + valueZ);
                    bw.flush();
                    bw.close();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated
                e.printStackTrace();
            }

            // 補強運動リスト Training.csvの作成
            try {
                OutputStream out = openFileOutput("Training.csv", MODE_PRIVATE);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                bw.write("ダンベル,assets,danberu.png,dum.csv" + filepath);
                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //SDカードがない場合
            // TODO
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.main_danberu:
                intent = new Intent(this, TrainingSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.main_calendar:
                intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.main_camera:
                intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void startAuth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String defaultA = thisPref.getString("account", "i09324");
        String defaultP = thisPref.getString("password", "i09324");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText account = new EditText(this);
        account.setHint("Account");
        account.setText(defaultA);
        layout.addView(account, new LinearLayout.LayoutParams(MP, MP));

        final EditText password = new EditText(this);
        password.setHint("Password");
        password.setText(defaultP);
        layout.addView(password, new LinearLayout.LayoutParams(MP, MP));

        builder.setTitle("ようこそ！");
        builder.setView(layout);

        builder.setPositiveButton("ログイン",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        execAuth(account.getText().toString(), password
                                .getText().toString(), false);
                    }
                }
        );
        builder.setNegativeButton("キャンセル",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
        builder.create().show();
    }

    public void execAuth(String account, String password, boolean fresh) {
        String thisManufacturer = Build.MANUFACTURER;
        String thisModel = Build.MODEL;
        String thisVersionR = Build.VERSION.RELEASE;

        if (fresh) {
            Editor editor = thisPref.edit();
            editor.putString("account", account);
            editor.putString("password", password);
            editor.commit();
        }

        String device = null;
        try {
            device = URLEncoder.encode(String.format("%s %s %s",
                    thisManufacturer, thisModel, thisVersionR), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String uri = "http://hsys.klabo.co.jp/postit/auth.php";
        url = String.format("%s?mode=auth&account=%s&password=%s&device=%s",
                uri, account, password, device);

        if (httpExec.GET(0, url)) {
        } else {
            Toast.makeText(this, "no internet connection", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void serviceController(boolean start) {
        Intent mode = new Intent();
        mode.setClass(this, CountService.class);

        if (start) {
            mode.putExtra("whoami", whoami.id);
            mode.putExtra("sInterval", 5);
            mode.putExtra("phour", whoami.pHour);
            mode.putExtra("plong", whoami.pLong);
            mode.putExtra("vibrator", whoami.useVib);

            startService(mode);
            inService = true;

            Intent intent = new Intent(this, CountService.class);

            IntentFilter intentfilter = new IntentFilter(CountService.ACTION);
            registerReceiver(receiver, intentfilter);

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

            thread = new ViewRefresh();
            thread.start();
        } else {
            stopService(mode);
            inService = false;

            unbindService(serviceConnection);
            thread.close();
        }
    }

    /*    private void playFromSoundPool() {
            // 音声データを再生する
            mSoundPool.play(mSoundId, 1.0F, 1.0F, 0, 0, 1.0F);
        }
    */
    // 非同期の画面更新クラス
    class ViewRefresh extends Thread {
        Handler handler = new Handler();

        TextView counter;
        boolean runflg = true;
        long count;

        public ViewRefresh() {
            counter = (TextView) findViewById(R.id.counter);
        }

        public void close() {
            runflg = false;
        }

        @Override
        public void run() {
            while (runflg) {
                // メインスレッドに画面更新の処理を依頼する
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 歩数を取得して表示する
                            if ((count = countService.getCounter()) != -1) {
                                counter.setText("" + countService.getCounter());
                            } else {
                                counter.setText("0");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    // Backボタンの挙動制御
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode != KeyEvent.KEYCODE_BACK) {
//            // Backボタン押下時のActivity終了を禁止する
//            return super.onKeyDown(keyCode, event);
//        } else {
//            return false;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_A, 0, "歩数計の開始/停止").setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_B, 0, "設定").setIcon(android.R.drawable.ic_menu_preferences);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case MENU_A:
                if (!inService) {
                    if (!isAuth) {
                        startAuth();
                    } else {
                        serviceController(true);
                    }
                } else {

                    builder.setTitle("歩数計を停止してもよろしいですか？");
                    builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            serviceController(false);
                            isAuth = false;
                        }
                    });

                    builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                        }
                    });

                    builder.create().show();
                }
                break;
            case MENU_B:
                if (isAuth) {
                    final String[] chkItenms = {"バイブレーターを使用する", "カメラを使用する"};
                    final boolean[] chkSts = {whoami.useVib, whoami.useCamera};

                    builder.setTitle("サービスの設定");
                    builder.setMultiChoiceItems(chkItenms, chkSts, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean flag) {
                            switch (which) {
                                case 0:
                                    whoami.useVib = flag;
                                    break;
                                case 1:
                                    whoami.useCamera = flag;
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Editor editor = thisPref.edit();
                            editor.putBoolean("useVib", whoami.useVib);
                            editor.putBoolean("useCamera", whoami.useCamera);
                            editor.commit();
                            serviceController(false);
                            serviceController(true);
                        }
                    });
                    builder.create().show();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * //音声データを読み込み mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,
		 * 0); mSoundId = mSoundPool.load(getApplicationContext(), R.raw.a, 0);
		 */
        inService = isServiceActive(thisService);

        if (!inService) {
            startAuth();
        } else if (!isAuth) {
            boolean v = thisPref.getBoolean("useVib", true);
            boolean c = thisPref.getBoolean("useCamera", true);

            mrJSON.Get(thisPref.getString("JSON", ""));
            whoami.setParam(mrJSON.id, mrJSON.fullname, mrJSON.number,
                    mrJSON.pHour, mrJSON.pLong, v, new Date(), c);
            isAuth = true;
        }

        if (inService) {
            Intent intent = new Intent(this, CountService.class);

            IntentFilter intentfilter = new IntentFilter(CountService.ACTION);
            registerReceiver(receiver, intentfilter);

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

            thread = new ViewRefresh();
            thread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
		/*
		 * //音声データをリリースする mSoundPool.release();
		 */

        ViewRefresh refresh = new ViewRefresh();
        refresh.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // リーク時の処理
		/*
		 * mSoundPool.release();
		 */
    }
}
