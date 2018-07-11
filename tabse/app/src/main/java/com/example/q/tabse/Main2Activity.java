package com.example.q.tabse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    public ListView listview;
    public ListViewAdapter adapter;
    public FloatingActionButton button;
    public ArrayList<Item> data = new ArrayList<>();
    public int position;
    public Intent inte = new Intent();
    public Person temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listview = findViewById(R.id.listView3);
        button = (FloatingActionButton) findViewById(R.id.floatingActionButton3);

        Intent intent = getIntent();
        position = First.pos;
        temp = First.data.get(position);

        data.add(new Item("이름", temp.name));
        data.add(new Item("전화번호", temp.phone_number1));
        data.add(new Item("", temp.phone_number2));
        data.add(new Item("", temp.phone_number3));
        data.add(new Item("성별", temp.sex));
        data.add(new Item("이메일", temp.email));
        data.add(new Item("소속", temp.department));

        adapter = new ListViewAdapter(this, R.layout.item, data);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                show1(position);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show3(position);
            }
        });

        setResult(1, inte);
    }

    void show1(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("연락처 수정");
        builder.setMessage("수정하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        show2(position);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    void show2(final int position) {
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String title;

        switch(position){
            case 0: title = "이름"; edittext.setInputType(0x00000001); break;
            case 1: title = "전화번호1"; edittext.setInputType(0x00000003); break;
            case 2: title = "전화번호2"; edittext.setInputType(0x00000003);break;
            case 3: title = "전화번호3"; edittext.setInputType(0x00000003);break;
            case 4: title = "성별"; edittext.setInputType(0x00000001); break;
            case 5: title = "이메일"; edittext.setInputType(0x00000021); break;
            case 6: title = "소속"; edittext.setInputType(0x00000001); break;
            default: title = "";
        }

        builder.setTitle("연락처 수정");
        builder.setMessage(title + "을(를) 입력하십시오");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(position){
                            case 0: temp.name = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            case 1: temp.phone_number1 = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            case 2: temp.phone_number2 = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            case 3: temp.phone_number3 = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            case 4: temp.sex = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            case 5: temp.email = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            case 6: temp.department = edittext.getText().toString(); data.get(position).c2 = edittext.getText().toString(); break;
                            default: break;
                        }
                        adapter.dataChange();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    void show3(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("연락처 삭제");
        builder.setMessage("연락처를 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        inte.putExtra("삭제", 1);
                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    public class ListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public ArrayList<Item> data;
        int layout;

        public ListViewAdapter(Context context, int layout, ArrayList<Item> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data=data;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Item getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null) {
                convertView = inflater.inflate(layout, parent, false);
            }

            Item it = data.get(position);

            TextView textview = (TextView) convertView.findViewById(R.id.textView);
            textview.setText(it.c1);
            TextView textview2 = (TextView) convertView.findViewById(R.id.textView2);
            if(position >= 1 && position <=3) textview2.setText(format.StringToPhone(it.c2));
            else textview2.setText(it.c2);
            return convertView;
        }

        public void addItem(Item p){
            data.add(p);
            dataChange();
        }

        public void dataChange(){
            adapter.notifyDataSetChanged();
        }
    }

    public class Item {
        public String c1;
        public String c2;

        Item(String c1, String c2){
            this.c1 = c1;
            this.c2 = c2;
        }
    }
}
