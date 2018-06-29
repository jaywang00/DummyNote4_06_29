package com.example.dummynote4;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    private DB mDbHelper;
    private long rowId;
    private EditText editText1;
    private String editString1;
    private int mNoteNumber = 1;
    protected static final int MENU_INSERT = Menu.FIRST;
    protected static final int MENU_DELETE = Menu.FIRST + 1;
    protected static final int MENU_UPDATE = Menu.FIRST + 2;
    protected static final int MENU_INSERT_WITH_SPECIFIC_ID = Menu.FIRST + 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDB();
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnItemClickListener(this);
        setAdapter();

    }
    private void loadDB() {
        String DB_PATH = "/data/data/com.example.dummynote4/databases/";
        String DB_NAME = "mydb.sqlite";
        if ((new File(DB_PATH + DB_NAME)).exists() == false) {
            File f = new File(DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            try {
                InputStream is = getBaseContext().getAssets().open(DB_NAME);
                OutputStream os = new FileOutputStream(DB_PATH + DB_NAME);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void setAdapter() {
        mDbHelper = new DB(this).open();
        fillData();
    }

    void fillData() {
        Cursor c = mDbHelper.getAll();
        startManagingCursor(c);
        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(
            this,
            R.layout.simple_list_items,
            c,
            new String[]{"機構名稱","地址","電話"},
            new int[]{R.id.tvName,R.id.tvAddress,R.id.tvPhone},
            SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(scAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        rowId = id;
        System.out.println("rowId = " + rowId);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("AlertDialog：" + (position + 1));
        builder.setMessage("這裡可以用來顯示Alert訊息，要按[回上頁]鍵或是「確認」鈕才會關閉");
        builder.setPositiveButton("確認", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.add(0, MENU_INSERT, 0, "新增記事")
            .setIcon(android.R.drawable.ic_menu_add)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_DELETE, 0, "刪除記事")
            .setIcon(android.R.drawable.ic_menu_delete)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_UPDATE, 0, "修改記事")
            .setIcon(android.R.drawable.ic_menu_edit)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_INSERT:
                mNoteNumber = listView.getCount() + 1;
                String noteName = (mNoteNumber) + ". Note";
                mDbHelper.create(noteName);
                fillData();
                break;
            case MENU_DELETE:
                mDbHelper.delete(rowId);
                fillData();
                break;
            case MENU_UPDATE:
                editText1 = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("修改項目名稱")
                    .setMessage("請輸入您想修改的項目名稱")
                    .setView(editText1)
                    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editString1 = editText1.getText().toString();
                            if (!editString1.equals("")) {
                                mDbHelper.update(rowId, editText1.getText().toString());
                                fillData();
                            }
                        }
                    })
                    .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
