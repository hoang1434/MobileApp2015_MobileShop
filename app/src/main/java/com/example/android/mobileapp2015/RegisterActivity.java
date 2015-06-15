package com.example.android.mobileapp2015;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hoang on 14/06/2015.
 */
public class RegisterActivity extends Activity {

    EditText tvTendangnhap, tvMatkhau, tvMatkhauConf, tvHoten, tvSDT, tvEmail, tvDiachi;
    Button btnDangky;
    String tendangnhap,matkhau,matkhauxacnhan,hoten,sodienthoai,email,diachi;
    Url url_root = new Url();
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        //
        tvTendangnhap = (EditText) findViewById(R.id.txtUsernameReg);
        tvMatkhau = (EditText) findViewById(R.id.txtPasswordReg);
        tvMatkhauConf = (EditText) findViewById(R.id.txtPasswordRegConf);
        tvHoten = (EditText) findViewById(R.id.txtHotenReg);
        tvSDT = (EditText) findViewById(R.id.txtSodienthoaiReg);
        tvEmail = (EditText) findViewById(R.id.txtEmailReg);
        tvDiachi = (EditText) findViewById(R.id.txtDiachilReg);
        btnDangky = (Button) findViewById(R.id.btnRegister);
        //
        //
        btnDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tendangnhap = tvTendangnhap.getText().toString();
                hoten = tvHoten.getText().toString();
                matkhau = tvMatkhau.getText().toString();
                matkhauxacnhan = tvMatkhauConf.getText().toString();
                sodienthoai = tvSDT.getText().toString();
                email = tvEmail.getText().toString();
                diachi = tvDiachi.getText().toString();
                // Check if username, password is filled
                if(tendangnhap.trim().length() == 0 || matkhau.trim().length() == 0 || matkhauxacnhan.trim().length() == 0 || hoten.trim().length() == 0 || sodienthoai.trim().length() == 0 || email.trim().length() == 0 || diachi.trim().length() == 0){
                    alert.showAlertDialog(RegisterActivity.this, "Đăng ký thất bại", "Vui lòng nhập đầy đủ thông tin", false);

                }else if(!matkhau.equals(matkhauxacnhan)){
                    // user didn't entered username or password
                    alert.showAlertDialog(RegisterActivity.this, "Đăng ký thất bại", "Mật khẩu xác nhận không khớp", false);
                } else {
                    LoadRegister task1 = new LoadRegister();
                    task1.execute(new String[]{url_root.url + "login.php?tendangnhap=" + tendangnhap});
                }
            }
        });
    }

    private class LoadRegister extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
        String text1="";
        String tendangnhapResponse="";

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Đang kiểm tra...");
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
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
                    tendangnhapResponse = jsonObject.getString("tendangnhap");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            if(tendangnhapResponse.length() == 0){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if (result == true) {
                    alert.showAlertDialog(RegisterActivity.this, "Đăng ký thất bại", "Tên đăng nhập đã tồn tại", false);
            } else {
                RegisterUser task2 = new RegisterUser();
                task2.execute(new String[]{url_root.url + "register.php"});
        }

        }
    }

    private class RegisterUser extends AsyncTask<String, Void, Boolean>{
        ProgressDialog dialog1 = new ProgressDialog(RegisterActivity.this);
        @Override
        protected void onPreExecute() {
            dialog1.setMessage("Đang đăng ký...");
            dialog1.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            for(String url1 : urls){
                try {
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("txtTendangnhap", tvTendangnhap.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtMatkhau", tvMatkhau.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtHoten", tvHoten.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtSodienthoai", tvSDT.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtEmail", tvEmail.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtDiachi", tvDiachi.getText().toString()));
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(pairs));
                    HttpResponse response = client.execute(post);

                } catch (ClientProtocolException e){
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result == true){
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent1);
            }else{
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_LONG).show();
            }
            dialog1.dismiss();
        }
    }
}
