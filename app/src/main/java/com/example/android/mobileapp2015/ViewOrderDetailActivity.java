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
import android.widget.ImageView;
import android.widget.TextView;
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

/**
 * Created by Hoang on 14/06/2015.
 */
public class ViewOrderDetailActivity extends Activity {
    String madonhang;
    Url url_root = new Url();
    TextView tvTensanpham, tvMadonhang, tvSoluong, tvDongia, tvTongtien;
    Button btnHome;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_detail);
        Intent i = getIntent();
        madonhang = i.getStringExtra("madonhang");
        tvMadonhang = (TextView) findViewById(R.id.txtMadonhangCT);
        tvTensanpham = (TextView) findViewById(R.id.txtTensanphamCT);
        tvSoluong = (TextView) findViewById(R.id.txtSoluongCT);
        tvDongia = (TextView) findViewById(R.id.txtDongiaCT);
        tvTongtien = (TextView) findViewById(R.id.txtTongtienCT);
        btnHome = (Button) findViewById(R.id.btnHome);
        session = new SessionManager(getApplicationContext());
        //
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewOrderDetailActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //
        LoadOrder task1 = new LoadOrder();
        task1.execute(new String[]{url_root.url + "get_order_detail.php?madonhang=" + madonhang});
    }

    private class LoadOrder extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(ViewOrderDetailActivity.this);
        String text1 = "";
        String tensanpham = "";
        String giaban,tongtien;
        Integer giaban_int = 0;
        Integer soluong = 0;
        Integer tongtien_int = 0;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading data...");
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
                    Toast.makeText(ViewOrderDetailActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(ViewOrderDetailActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    JSONObject jsonObject = new JSONObject(text1);
                    tensanpham = jsonObject.getString("tensanpham");
                    giaban_int = jsonObject.getInt("giaban");
                    giaban = NumberFormat.getInstance().format(jsonObject.getInt("giaban"))+ " VND";
                    soluong = jsonObject.getInt("soluong");
                    tongtien_int = jsonObject.getInt("tongtien");
                    tongtien = NumberFormat.getInstance().format(jsonObject.getInt("tongtien")) + " VND";
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                tvMadonhang.setText(madonhang);
                tvTensanpham.setText(tensanpham);
                tvSoluong.setText(soluong.toString());
                tvDongia.setText(giaban);
                tvTongtien.setText(tongtien);
            } else {
                Toast.makeText(ViewOrderDetailActivity.this, "Load false", Toast.LENGTH_LONG).show();
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
                Intent dangky = new Intent(ViewOrderDetailActivity.this, RegisterActivity.class);
                startActivity(dangky);
                return true;
            case R.id.it_thongtin:
                Intent thongtin = new Intent(ViewOrderDetailActivity.this, UserInfoActivity.class);
                startActivity(thongtin);
                return true;
            case R.id.it_donhang:
                Intent donhang = new Intent(ViewOrderDetailActivity.this, ViewOrdersActivity.class);
                startActivity(donhang);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
