package com.example.q.tabse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity {

    CallbackManager callbackManager;
    static AccessToken user;
    EditText id_text, password_text;
    static String user_id;
    Button login_button, sign_button;
    LoginButton b1;
    StringBuffer response = new StringBuffer();
    Gson gson = new Gson();
    ArrayList<Login> list = new ArrayList<>();
    boolean ret = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

       b1 = findViewById(R.id.login_button);
       b1.setReadPermissions(Arrays.asList("email"));

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        user=loginResult.getAccessToken();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

        id_text = findViewById(R.id.editText);
        password_text = findViewById(R.id.editText2);
        login_button = findViewById(R.id.button2);
        sign_button = findViewById(R.id.button3);
    }

    public void onClick(View v) {
        login_button.setClickable(false);
        user_id = id_text.getText().toString();
        Login p = new Login(id_text.getText().toString(), password_text.getText().toString());
        if(p.getId().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("아이디 오류");
            builder.setMessage("아이디를 입력하십시오");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
        else if(p.getPassword().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("비밀번호 오류");
            builder.setMessage("비밀번호를 입력하십시오");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        }
        else check(p.getId(), p.getPassword());
        login_button.setClickable(true);
    }

    public void onClick2(View v) {
        sign_button.setClickable(false);
        Login p = new Login(id_text.getText().toString(), password_text.getText().toString());
        if(p.getId().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("아이디 오류");
            builder.setMessage("아이디를 입력하십시오");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
        else if(p.getPassword().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("비밀번호 오류");
            builder.setMessage("비밀번호를 입력하십시오");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
        else make_account(p);
        sign_button.setClickable(true);
    }

    public void make_account(final Login p) {
        String query = "https://logapps.azurewebsites.net/tables/Login"+"?$filter=id eq '" + p.getId() + "'";
        final String finalQuery = query;
        list.clear();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    URL obj = new URL(finalQuery);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");


                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("zumo-api-version", "2.0.0");
                    //add request header
                    //con.setRequestProperty("User-Agent", USER_AGENT);

                    int responseCode = 0;

                    responseCode = con.getResponseCode();

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

                    //print result
                    System.out.println(response);

                    String tmp = response.toString();
                    list = gson.fromJson(tmp, new TypeToken<ArrayList<Login>>() {
                    }.getType());

                    if (list.size() != 0) {
                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                    else {
                        Message msg = handler4.obtainMessage();
                        handler4.sendMessage(msg);

                        String query = "https://logapps.azurewebsites.net/tables/Login";
                        URL obj2 = new URL(query);
                        HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();

                        // optional default is GET
                        con2.setRequestMethod("POST");
                        con2.setRequestProperty("Accept", "application/json");
                        con2.setRequestProperty("Content-Type", "application/json");
                        con2.setRequestProperty("zumo-api-version", "2.0.0");
                        con2.setDoOutput(true);

                        OutputStream out = con2.getOutputStream();
                        out.write(("{" + "\"id\":\"" + p.getId() + "\",\"password\":\"" + p.getPassword() + "\"}").getBytes("utf-8"));
                        out.close();
                        //add request header
                        //con.setRequestProperty("User-Agent", USER_AGENT);

                        int responseCode2 = 0;

                        responseCode2 = con2.getResponseCode();

                        System.out.println("\nSending 'GET' request to URL : " + query);
                        System.out.println("Response Code : " + responseCode2);

                        in = new BufferedReader(
                                new InputStreamReader(con2.getInputStream()));

                        response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        con2.disconnect();

                        //print result
                        System.out.println(response);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }}).start();
    }

    void check(String id, final String password) {
        String query = "https://logapps.azurewebsites.net/tables/Login"+"?$filter=id eq '" + id + "'";
        final String finalQuery = query;
        list.clear();
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    URL obj = new URL(finalQuery);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");


                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("zumo-api-version", "2.0.0");
                    //add request header
                    //con.setRequestProperty("User-Agent", USER_AGENT);

                    int responseCode = 0;

                    responseCode = con.getResponseCode();

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

                    //print result
                    System.out.println(response);

                    String tmp = response.toString();
                    list = gson.fromJson(tmp, new TypeToken<ArrayList<Login>>(){}.getType());

                    if(list.size() == 0) {
                        Message msg = handler2.obtainMessage();
                        handler2.sendMessage(msg);
                        ret = false;
                    }
                    else if(list.get(0).getPassword().equals(password)){
                        System.out.println("success");
                        Message msg = handler5.obtainMessage();
                        handler5.sendMessage(msg);
                        ret = true;
                    }
                    else {
                        Message msg = handler3.obtainMessage();
                        handler3.sendMessage(msg);
                        ret = false;
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("아이디 오류");
            builder.setMessage("입력하신 아이디는 이미 존재하는 아이디 입니다.");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
    };

    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("아이디 오류");
            builder.setMessage("입력하신 아이디는 없는 아이디 입니다.");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
    };

    private Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("비밀번호 오류");
            builder.setMessage("입력하신 비밀번호는 틀렸습니다.");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
    };

    private Handler handler4 = new Handler() {
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("회원가입 성공");
            builder.setMessage("회원가입하신 아이디로 로그인 하세요");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }
    };

    private Handler handler5 = new Handler() {
        public void handleMessage(Message msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("로그인 성공");
            builder.setMessage("");
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.this.setResult(1112);
                            LoginActivity.this.finish();
                        }
                    });
            builder.show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println(resultCode);
        if(resultCode == -1) {
            setResult(1111);
            this.finish();
        }
    }
}

class Login
{
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }

    @com.google.gson.annotations.SerializedName("password")
    private String mPassword;
    public String getPassword() { return mPassword; }
    public final void setPassword(String Password) { mPassword = Password; }

    @com.google.gson.annotations.SerializedName("createdAt")
    private String mCreatedAt;
    public String getCreatedAt() { return mCreatedAt; }
    protected void setCreatedAt(String createdAt) { mCreatedAt = createdAt; }

    @com.google.gson.annotations.SerializedName("updatedAt")
    private String mUpdatedAt;
    public String getUpdatedAt() { return mUpdatedAt; }
    protected void setUpdatedAt(String updatedAt) { mUpdatedAt = updatedAt; }

    @com.google.gson.annotations.SerializedName("version")
    private String mVersion;
    public String getVersion() { return mVersion; }
    protected void setVersion(String Version) { mVersion = Version; }

    @com.google.gson.annotations.SerializedName("deleted")
    private Boolean mDeleted;
    public Boolean getDeleted() { return mDeleted; }
    protected void setDeleted(Boolean Deleted) { mDeleted = Deleted; }

    public Login() { }

    public Login(String id, String Password) {
        this.setId(id);
        this.setPassword(Password);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Login && ((Login) o).mId == mId;
    }
}

