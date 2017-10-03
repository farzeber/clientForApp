package example.com.classappp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import example.com.classappp.forceLoginOut.BaseActivity;
import example.com.classappp.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";
    private Button Login;
    private EditText User;
    private EditText Password;
    private ProgressDialog progressDialog;
    private String sjson=null;
    private String address="http://192.168.1.231:8080/myhttp/servlet/LoginAction";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private CheckBox loginAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        //隐去标题栏部分（电池等图标和一切修饰部分）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        pref=this.getSharedPreferences("LoginMessage",0);
        User=(EditText) findViewById(R.id.user);
        Password=(EditText) findViewById(R.id.Password);
        TextInputLayout s=(TextInputLayout) findViewById(R.id.discrible_password);
      /*  s.setPasswordVisibilityToggleTintList(R.drawable.visibility_selector);*/
        loginAuto=(CheckBox) findViewById(R.id.login_auto);
        rememberPass=(CheckBox) findViewById(R.id.remember_pass);
        Login=(Button) findViewById(R.id.login_in);
        Login.setOnClickListener(this);
        loginAuto.setOnClickListener(this);
        rememberPass.setOnClickListener(this);
        setLogin();

    }

   private void setLogin(){
       Log.d(TAG, "setLogin: ");
       String accout=pref.getString("acount",null);
       String password=pref.getString("password",null);
       boolean isPassword=pref.getBoolean("remember_password",false);
       boolean isLoginAuto=pref.getBoolean("login_auto",false);
       if(accout!=null)
           User.setText(accout);
       if(isPassword) {
           rememberPass.setChecked(true);
           if(password!=null)
           Password.setText(password);
       }
       if(isLoginAuto)
       {
           Log.d(TAG, "isLoginAuto");
           loginAuto.setChecked(true);
           sjson="{  \"username\": \""+accout+"\",\"password\":\""+password+"\"}";
           querFromServer(address);
       }
   }
    @Override
    public void onClick(View v) {

        editor=pref.edit();
        switch (v.getId()){
            case R.id.login_in:
                String accout=User.getText().toString();
                String password=Password.getText().toString();

                editor.putString("acount",accout);
                if(loginAuto.isChecked())
                    editor.putBoolean("login_auto",true);
                else {
                    editor.putBoolean("login_auto",false);
                }
                if(rememberPass.isChecked()) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("password",password);
                }
                else {
                    editor.putBoolean("remember_password",false);
                    editor.putString("password",null);
                }

                sjson="{  \"username\": \""+accout+"\",\"password\":\""+password+"\"}";
                Log.d("MainActivity", sjson);

                querFromServer(address);
                break;
            case R.id.login_auto:
                Log.d(TAG, "login_auto");
                  if(loginAuto.isChecked())
                  {
                      rememberPass.setChecked(true);
                      editor.putBoolean("login_auto",true);
                      editor.putBoolean("remember_password", true);
                      Log.d(TAG, "onClick:");
                  }
                else {
                      editor.putBoolean("login_auto",false);
                  }
                break;
            case R.id.remember_pass:
                Log.d(TAG, "onClick: llllll");
                if(rememberPass.isChecked()) {
                    editor.putBoolean("remember_password", true);
                }
                else {
                    Log.d(TAG, "onClick: llllll");
                    editor.putBoolean("remember_password",false);
                    editor.putString("password",null);
                }
                break;
            default:break;
        }
        editor.apply();
    }

    private void querFromServer(String address){
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, sjson,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("MainActivity", "onFailure: " + e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();

                        Toast.makeText(LoginActivity.this ,"failure",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: "+response.code());
                if(response.code()==200){
                    closeProgressDialog();
                String responseText=response.body().string();
                   try{
                       JSONObject jsonObject=new JSONObject(responseText);
                       String msg=jsonObject.getString("msg");
                       Log.d(TAG, "onResponse: "+msg);
                       if(msg.equals("success")){
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {

                                   Toast.makeText(LoginActivity.this,"succeed",Toast.LENGTH_SHORT).show();
                                   Intent intent=new Intent(LoginActivity.this,Main2Activity.class);
                                   startActivity(intent);
                                   finish();
                               }
                           });
                       }
                       else {
                        Message mssg=Message.obtain();
                           mssg.what=1;
                           handler.sendMessage(mssg);

                       }
                   }
                   catch (JSONException e){
                       e.printStackTrace();
                   }
                  }
                else {
                  Message msg=Message.obtain();
                    msg.what=1;
                    handler.sendMessage(msg);
                }

            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在登录...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    private Handler handler=new Handler(){
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    closeProgressDialog();
                    Toast.makeText(LoginActivity.this,"错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
