package com.example.android.mobileapp2015;

/**
 * Created by Hoang on 13/06/2015.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;

public class LoginActivity extends Activity {

    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;
    String username;
    String password;
    //linh tinh
    Url url_root = new Url();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Email, Password input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();

                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                    // For testing puspose username, password is checked with sample data
                    // username = test
                    // password = test
                    LoadLogin task1 = new LoadLogin();
                    task1.execute(new String[]{url_root.url + "login.php?tendangnhap=" + username});

                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "Đăng nhập thất bại", "Vui lòng nhập tài khoản hoặc mật khẩu", false);
                }

            }
        });
    }

    private class LoadLogin extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        String text1 = "";
        String tendangnhap = "";
        String matkhau = "";
        String hoten="";
        String sodienthoai;
        String email ;
        String diachi ;


        @Override
        protected void onPreExecute() {
            dialog.setMessage("Đang đăng nhập...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            InputStream is1;
            for (String url1 : urls) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    HttpResponse response = client.execute(post);
                    is1 = response.getEntity().getContent();
                } catch (ClientProtocolException e) {
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                }
                BufferedReader reader;

                try {
                    Log.d("Json", "Vô vòng try");
                    reader = new BufferedReader(new InputStreamReader(is1, "iso-8859-1"), 200);
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        text1 += line + "\n";
                        Log.d("Json", "Vô vòng while");
                    }
                    is1.close();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }


                try {
                    JSONObject jsonObject = new JSONObject(text1);
                    tendangnhap = jsonObject.getString("tendangnhap");
                    matkhau = jsonObject.getString("matkhau");
                    sodienthoai = jsonObject.getString("sodienthoai");
                    email = jsonObject.getString("email");
                    diachi = jsonObject.getString("diachi");
                    hoten = jsonObject.getString("hoten");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if(tendangnhap.length() == 0){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                if(username.equals(tendangnhap) && password.equals(matkhau)){
                    // Creating user login session
                    // For testing i am stroing name, email as follow
                    // Use user real data
                    session.createLoginSession(tendangnhap,hoten,sodienthoai,email,diachi);
                    Toast.makeText(LoginActivity.this,"Đăng nhập thành công", Toast.LENGTH_LONG).show();
                    // Staring MainActivity
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    //finish();

                }else{
                    // username / password doesn't match
                    alert.showAlertDialog(LoginActivity.this, "Đăng nhập thất bại", "Mật khẩu không đúng", false);
                }

            } else {
                alert.showAlertDialog(LoginActivity.this, "Đăng nhập thất bại", "Tài khoản không tồn tại", false);
            }
            dialog.dismiss();
        }
    }
}