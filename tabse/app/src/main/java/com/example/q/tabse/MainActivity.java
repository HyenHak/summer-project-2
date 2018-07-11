package com.example.q.tabse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static String user_id;
    AccessToken user;
    private Fragment[] arrFragments = new Fragment[3];
    private int LOGIN_REQUEST = 1000;
    StringBuffer response2 = new StringBuffer();
    StringBuffer response = new StringBuffer();
    public Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_pager);

        arrFragments[0] = new First();
        arrFragments[1] = new Second();
        arrFragments[2] = new Third();

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), arrFragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode);
        if (resultCode == 1111 || resultCode == 1 || resultCode == RESULT_OK || resultCode == 1112) {
            user = LoginActivity.user;
            user_id = LoginActivity.user_id;

            if (resultCode == 1111 || resultCode == 1112) {
                if (user == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("반갑습니다.");
                    builder.setMessage(user_id + "님\n안녕하세요");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                } else {
                    GraphRequest request;
                    request = GraphRequest.newMeRequest(
                            user,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        System.out.println(object);
                                        user_id = object.getString("email");
                                        First.user_id = user_id;
                                        Second.user_id = user_id;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("반갑습니다.");
                                    builder.setMessage(user_id + "님\n안녕하세요");
                                    builder.setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String query = "https://logapps.azurewebsites.net/tables/Login";
                                                    final String finalQuery = query;
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                URL obj = new URL(finalQuery);
                                                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                                                // optional default is GET
                                                                con.setRequestMethod("POST");
                                                                con.setRequestProperty("Accept", "application/json");
                                                                con.setRequestProperty("Content-Type", "application/json");
                                                                con.setRequestProperty("zumo-api-version", "2.0.0");
                                                                con.setDoOutput(true);

                                                                OutputStream out = con.getOutputStream();
                                                                out.write(("{" + "\"id\":\"" + user_id + "\",\"password\":\"" + "\"}").getBytes("utf-8"));
                                                                out.close();

                                                                int responseCode = 0;

                                                                responseCode = con.getResponseCode();

                                                                System.out.println("\nSending 'GET' request to URL : " + finalQuery);
                                                                System.out.println("Response Code : " + responseCode);

                                                                BufferedReader in = null;

                                                                if (responseCode != 409) {
                                                                    in = new BufferedReader(
                                                                            new InputStreamReader(con.getInputStream()));

                                                                    String inputLine;
                                                                    response2 = new StringBuffer();

                                                                    while ((inputLine = in.readLine()) != null) {
                                                                        response2.append(inputLine);
                                                                    }
                                                                    in.close();
                                                                }
                                                                con.disconnect();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();
                                                }
                                            });
                                    builder.show();
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "email");
                    request.setParameters(parameters);
                    request.executeAsync();

                    System.out.println(user_id);
                }
            }
        }
        else finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] arrFragments;

        //생성자
        public MyPagerAdapter(FragmentManager fm, Fragment[] arrFragments) {
            super(fm);
            this.arrFragments = arrFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return arrFragments[position];
        }

        @Override
        public int getCount() {
            return arrFragments.length;
        }

        //Tab의 타이틀 설정
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "연락처";
                case 1:
                    return "갤러리";
                case 2:
                    return "프로필";
                default:
                    return "";
            }
            //return super.getPageTitle(position);
        }
    }
}
