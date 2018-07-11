package com.example.q.tabse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ListView;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.*;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static android.content.Context.MODE_PRIVATE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class First extends Fragment {
    private ListView listview;
    public static ListViewAdapter adapter;
    private FloatingActionButton button, button2, button3;
    public static ArrayList<Person> data = new ArrayList<>();
    public static ArrayList<Person> list = new ArrayList<>();
    public static String user_id = MainActivity.user_id;
    private Gson gson = new Gson();
    public static int pos;
    public ProgressBar progressbar;
    StringBuffer response = new StringBuffer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class ListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public ArrayList<Person> data;
        private int layout;

        public ListViewAdapter(Context context, int layout, ArrayList<Person> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = data;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Person getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

            Person person = data.get(position);

            TextView textview = (TextView) convertView.findViewById(R.id.textView);
            textview.setText(person.name);
            TextView textview2 = (TextView) convertView.findViewById(R.id.textView2);
            textview2.setText(format.StringToPhone(person.phone_number1));

            return convertView;
        }

        public void addItem(Person person) {
            data.add(person);
            dataChange();
        }

        public void remove(int position) {
            data.remove(position);
            dataChange();
        }

        public void dataChange() {
            adapter.notifyDataSetChanged();
        }
    }

    public void FileSave() {
        int i;

        for(i=0;i<data.size();i++) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_first, null);

        button = (FloatingActionButton) view.findViewById(R.id.floatingActionButton2);
        button2 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        button3 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton4);
        listview = (ListView) view.findViewById(R.id.listview);
        adapter = new ListViewAdapter(getActivity(), R.layout.item, data);
        listview.setAdapter(adapter);
        progressbar = view.findViewById(R.id.progressBar);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                pos = position;
                Intent intent = new Intent(getActivity(), Main2Activity.class);
                startActivityForResult(intent, 0);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person p = new Person();
                p.owner = new String(user_id);
                adapter.addItem(p);
                pos = adapter.data.size() - 1;
                Intent intent = new Intent(getActivity(), Main2Activity.class);
                startActivityForResult(intent, 0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_id == null) user_id = MainActivity.user_id;
                button3.setClickable(false);
                button2.setClickable(false);
                button.setClickable(false);
                listview.setClickable(false);
                First.data.clear();
                String query = "https://logapps.azurewebsites.net/tables/Person" + "?$filter=owner eq '" + user_id + "'";
                final String finalQuery = query;
                progressbar.setProgress(10);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL obj = new URL(finalQuery);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            progressbar.setProgress(40);

                            // optional default is GET
                            con.setRequestMethod("GET");
                            con.setRequestProperty("Accept", "application/json");
                            con.setRequestProperty("zumo-api-version", "2.0.0");

                            int responseCode = 0;

                            responseCode = con.getResponseCode();
                            progressbar.setProgress(70);

                            System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                            System.out.println("Response Code : " + responseCode);

                            BufferedReader in = null;

                            in = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));

                            String inputLine;
                            response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            con.disconnect();

                            progressbar.setProgress(100);

                            String tmp = response.toString();
                            First.data = gson.fromJson(tmp, new TypeToken<ArrayList<Person>>(){}.getType());
                            //First.adapter.dataChange();
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                            progressbar.setProgress(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        button3.setClickable(true);
                        button2.setClickable(true);
                        button.setClickable(true);
                        listview.setClickable(true);

                    }
                }).start();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_id == null) user_id = MainActivity.user_id;
                button3.setClickable(false);
                button2.setClickable(false);
                button.setClickable(false);
                listview.setClickable(false);
                String query = "https://logapps.azurewebsites.net/tables/Person";
                final String finalQuery = query;
                progressbar.setProgress(10);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL obj = new URL(finalQuery + "?$filter=owner eq '" + user_id + "'");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            progressbar.setProgress(20);

                            // optional default is GET
                            con.setRequestMethod("GET");
                            con.setRequestProperty("Accept", "application/json");
                            con.setRequestProperty("zumo-api-version", "2.0.0");

                            int responseCode = 0;

                            responseCode = con.getResponseCode();
                            progressbar.setProgress(30);

                            System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                            System.out.println("Response Code : " + responseCode);

                            BufferedReader in = null;

                            in = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));

                            String inputLine;
                            response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            con.disconnect();
                            progressbar.setProgress(40);

                            String tmp = response.toString();
                            list = gson.fromJson(tmp, new TypeToken<ArrayList<Person>>(){}.getType());
                            //First.adapter.dataChange();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            int i;
                            for(i=0;i<list.size();i++) {
                                URL obj = new URL(finalQuery+"/"+list.get(i).id);
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                // optional default is GET
                                con.setRequestMethod("DELETE");
                                con.setRequestProperty("Accept", "application/json");
                                con.setRequestProperty("zumo-api-version", "2.0.0");

                                con.setDoOutput(true);

                                OutputStream out = con.getOutputStream();
                                out.write(gson.toJson(list.get(i), Person.class).getBytes());
                                out.close();

                                int responseCode = 0;

                                responseCode = con.getResponseCode();

                                System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                                System.out.println("Response Code : " + responseCode);

                                BufferedReader in = null;

                                if(responseCode!=409) {
                                    in = new BufferedReader(
                                            new InputStreamReader(con.getInputStream()));

                                    String inputLine;
                                    response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                }
                                con.disconnect();
                                progressbar.setProgress(progressbar.getProgress()+30/list.size());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            progressbar.setProgress(70);
                            int i;
                            for(i=0;i<data.size();i++) {
                                data.get(i).id="";
                                URL obj = new URL(finalQuery);
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                // optional default is GET
                                con.setRequestMethod("POST");
                                con.setRequestProperty("Accept", "application/json");
                                con.setRequestProperty("Content-Type", "application/json");
                                con.setRequestProperty("zumo-api-version", "2.0.0");


                                con.setDoOutput(true);

                                OutputStream out = con.getOutputStream();
                                out.write(gson.toJson(data.get(i), Person.class).getBytes());
                                out.close();

                                int responseCode = 0;

                                responseCode = con.getResponseCode();

                                System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                                System.out.println("Response Code : " + responseCode);

                                BufferedReader in = null;

                                if(responseCode!=409) {
                                    in = new BufferedReader(
                                            new InputStreamReader(con.getInputStream()));

                                    String inputLine;
                                    response = new StringBuffer();

                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                }
                                con.disconnect();
                                progressbar.setProgress(progressbar.getProgress()+30/data.size());
                            }
                            progressbar.setProgress(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        button3.setClickable(true);
                        button2.setClickable(true);
                        button.setClickable(true);
                        listview.setClickable(true);
                        progressbar.setProgress(0);
                    }
                }).start();
            }
        });

        return view;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            adapter = new ListViewAdapter(getActivity(), R.layout.item, data);
            listview.setAdapter(adapter);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        data.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            int del = data.getIntExtra("삭제", 0);
            if (del == 1) {
                adapter.data.remove(pos);
                adapter.dataChange();
            }
            adapter.dataChange();
        }

        AscendingObj ascending = new AscendingObj();
        Collections.sort(this.data, ascending);
        adapter.dataChange();

        FileSave();
    }

    class AscendingObj implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}

class Person {
    public String id;
    public String name;
    public String sex;
    public String phone_number1;
    public String phone_number2;
    public String phone_number3;
    public String email;
    public String department;
    public String owner;

    Person() {
        id = "";
        name = "";
        sex = "";
        email = "";
        department = "";
        phone_number1="";
        phone_number2="";
        phone_number3="";
    }

    Person(String name, String sex, String phone_number1, String phone_number2, String phone_number3, String email, String department) {
        this.name = name;
        this.sex = sex;
        this.phone_number1 = phone_number1;
        this.phone_number2 = phone_number2;
        this.phone_number3 = phone_number3;
        this.email = email;
        this.department = department;
    }
}