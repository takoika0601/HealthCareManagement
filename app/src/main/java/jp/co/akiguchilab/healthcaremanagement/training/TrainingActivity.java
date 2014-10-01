package jp.co.akiguchilab.healthcaremanagement.training;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.akiguchilab.healthcaremanagement.R;

public class TrainingActivity extends Activity implements View.OnClickListener {

        private int flag = 0;
        private ListView mListView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_training);

            ImageButton clover = (ImageButton) findViewById(R.id.clover);
            ImageButton apple = (ImageButton) findViewById(R.id.apple);
            ImageButton dog = (ImageButton) findViewById(R.id.dog);
            ImageButton bard = (ImageButton) findViewById(R.id.bard);

            clover.setOnClickListener(this);
            apple.setOnClickListener(this);
            dog.setOnClickListener(this);
            bard.setOnClickListener(this);

            Integer[] imgList = {R.drawable.nawatobi, R.drawable.danberu};
            String[] msgList = {"なわとび", "ダンベル"};

            ArrayList<ListViewData> objects = new ArrayList<ListViewData>();

            for(int i = 0; i < 2; i++) {
                ListViewData data = new ListViewData();
                data.setString(msgList[i]);
                data.setBitmap(BitmapFactory.decodeResource(getResources(), imgList[i]));
                objects.add(data);
            }

            ListViewItemAdapter adapter = new ListViewItemAdapter(this, objects);

            mListView = (ListView) findViewById(R.id.listView);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // item を選択したときの遷移実装
                }
            });
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.clover)                    flag = 1;
            else if(v.getId() == R.id.apple && flag == 1)   flag = 2;
            else if(v.getId() == R.id.dog && flag == 2)     flag = 3;
            else if(v.getId() == R.id.bard && flag == 3) {
                flag = 0;
                // Toast.makeText(this, "seek", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, TrainingSetting.class);
                startActivity(intent);
                //補強運動設定画面に遷移
            } else                                          flag = 0;
        }

        @Override
        protected void onResume() {
            super.onResume();
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
    }
