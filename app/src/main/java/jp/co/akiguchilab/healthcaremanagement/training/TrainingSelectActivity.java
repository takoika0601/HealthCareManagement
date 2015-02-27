package jp.co.akiguchilab.healthcaremanagement.training;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import jp.co.akiguchilab.healthcaremanagement.R;

public class TrainingSelectActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private int flag = 0;
    private int linage = 0;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trainingselect);

        ImageButton clover = (ImageButton) findViewById(R.id.clover);
        ImageButton apple = (ImageButton) findViewById(R.id.apple);
        ImageButton dog = (ImageButton) findViewById(R.id.dog);
        ImageButton bard = (ImageButton) findViewById(R.id.bard);

        clover.setOnClickListener(this);
        apple.setOnClickListener(this);
        dog.setOnClickListener(this);
        bard.setOnClickListener(this);
    }

    // 補強運動の読み込み
    private void readTraining() {
        ArrayList<ListViewData> objects = new ArrayList<ListViewData>();

        // ファイルから読み込む
        // 運動名,(assets,)アイコンファイルパス,閾値ファイルパス
        try {
            InputStream in = openFileInput("Training.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            String assets = "";

            while ((line = br.readLine()) != null) {
                linage++;

                StringTokenizer st = new StringTokenizer(line, ",");

                ListViewData data = new ListViewData();

                // 運動名の読み込み
                data.setTitle(st.nextToken());

                // アイコンの読み込み
                if ((assets = st.nextToken()).equals("assets")) {
                    data.setBitmap(BitmapFactory.decodeStream(new BufferedInputStream(this.getAssets().open(st.nextToken()))));
                } else {
                    data.setBitmap(BitmapFactory.decodeFile(assets));
                }

                // 閾値の読み込み
                data.setPath(st.nextToken());

                objects.add(data);
            }

            in.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListViewItemAdapter adapter = new ListViewItemAdapter(this, objects);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clover) flag = 1;
        else if (v.getId() == R.id.apple && flag == 1) flag = 2;
        else if (v.getId() == R.id.dog && flag == 2) flag = 3;
        else if (v.getId() == R.id.bard && flag == 3) {
            flag = 0;
            // Toast.makeText(this, "seek", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, TrainingSetting.class);
            intent.putExtra("linage", linage);
            //補強運動設定画面に遷移
            startActivity(intent);
        } else flag = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        readTraining();
        registerForContextMenu(mListView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_main, menu);
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() != R.id.DeleteListItem) {
            return false;
        }

        // MenuItemからContextMenuInfoを取得し、AdapterContextMenuInfoにキャストします
        ContextMenuInfo menuInfo = item.getMenuInfo();
        AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;

        // AdapterContextMenuInfoから長押ししたリストアイテムのpositionを取得します
        int position = adapterInfo.position;

        // ListViewから長押しされたリストアイテムを取得します
        ListViewData data = (ListViewData) mListView.getItemAtPosition(position);
        // ListViewからセットされているAdapterを取得します
        ListViewItemAdapter adapter = (ListViewItemAdapter) mListView.getAdapter();

        if (item.getItemId() == R.id.DeleteListItem) {
            adapter.remove(data);
        }
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView list = (ListView) parent;
        ListViewData data = (ListViewData) list.getItemAtPosition(position);
        Log.d("TrainingSelectActivity", "title : " + data.getTitle() + ", path : " + data.getPath());

        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra("title", data.getTitle());
        intent.putExtra("path", data.getPath());

        startActivity(intent);
    }
}
