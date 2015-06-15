package com.example.android.mobileapp2015;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hoang on 14/06/2015.
 */
public class UserInfoActivity extends Activity {

    Url url_root = new Url();
    SessionManager session;
    HashMap<String, String> user;
    EditText edTendangnhap, edHoten, edSodienthoai, edEmail, edDiachi;
    Button btnCapnhat;
    String tendangnhap,hoten,sodienthoai,email,diachi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        session = new SessionManager(getApplicationContext());
        //
        edHoten = (EditText) findViewById(R.id.txtHotenInfo);
        edSodienthoai = (EditText) findViewById(R.id.txtSodienthoaiInfo);
        edEmail = (EditText) findViewById(R.id.txtEmailInfo);
        edDiachi = (EditText) findViewById(R.id.txtDiachilInfo);
        btnCapnhat = (Button) findViewById(R.id.btnChangeInfo);
        // get thông tin tài khoản
        session.checkLogin();
        user = session.getUserDetails();
        tendangnhap = user.get(SessionManager.KEY_TENDANGNHAP);
        hoten = user.get(SessionManager.KEY_HOTEN);
        sodienthoai = user.get(SessionManager.KEY_SODIENTHOAI);
        email = user.get(SessionManager.KEY_EMAIL);
        diachi = user.get(SessionManager.KEY_DIACHI);
        // đưa vào edittext;
        edHoten.setText(hoten);
        edSodienthoai.setText(sodienthoai);
        edEmail.setText(email);
        edDiachi.setText(diachi);
        //
        btnCapnhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateData task1 = new UpdateData();
                task1.execute(new String[]{url_root.url + "update.php"});

            }
        });
    }

    private class UpdateData extends AsyncTask<String, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(UserInfoActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating data...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            for(String url1 : urls){
                try {
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("txtTendangnhap", tendangnhap));
                    pairs.add(new BasicNameValuePair("txtHoten", edHoten.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtSodienthoai", edSodienthoai.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtEmail", edEmail.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtDiachi", edDiachi.getText().toString()));
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(pairs));
                    HttpResponse response = client.execute(post);

                } catch (ClientProtocolException e){
                    Toast.makeText(UserInfoActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(UserInfoActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result == true){
                Toast.makeText(UserInfoActivity.this, "Cập nhật thành công", Toast.LENGTH_LONG).show();
                session.createLoginSession(tendangnhap, edHoten.getText().toString(), edSodienthoai.getText().toString(),edEmail.getText().toString(),edDiachi.getText().toString());
                Intent intent1 = new Intent(UserInfoActivity.this, MainActivity.class);
                startActivity(intent1);
            }else{
                Toast.makeText(UserInfoActivity.this, "Cập nhật thất bại", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(session.isLoggedIn() == true){
            getMenuInflater().inflate(R.menu.loged, menu);
        }else {
            getMenuInflater().inflate(R.menu.activity_main, menu);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_dangnhap:
                session.checkLogin();
                return true;
            case R.id.it_dangxuat:
                session.logoutUser();
                return true;
            case R.id.it_dangky:
                Intent dangky = new Intent(UserInfoActivity.this, RegisterActivity.class);
                startActivity(dangky);
                return true;
            case R.id.it_thongtin:
                Intent thongtin = new Intent(UserInfoActivity.this, UserInfoActivity.class);
                startActivity(thongtin);
                return true;
            case R.id.it_donhang:
                Toast.makeText(getApplicationContext(), "Item 3 Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
