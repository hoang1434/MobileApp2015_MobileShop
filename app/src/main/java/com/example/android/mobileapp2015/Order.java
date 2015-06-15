package com.example.android.mobileapp2015;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hoang on 13/06/2015.
 */
public class Order extends Activity {

    String tensanpham,masanpham,giaban,hinhlon,hinhdaidien;
    String tendangnhap,hoten,sodienthoai,email,diachi;
    Integer soluong, giaban_int;
    Integer soluongnhap = 0;
    TextView tvTensp,tvGiaban;
    Button btnDathang,btnThongtin;
    EditText edName, edSdt, edEmail, edSoluong, edDiachi;
    Url url_root = new Url();
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);
        tvTensp = (TextView) findViewById(R.id.tv_order_tensp);
        tvGiaban = (TextView) findViewById(R.id.tv_order_giaban);
        btnDathang = (Button) findViewById(R.id.btdathang);
        btnThongtin = (Button) findViewById(R.id.btnthongtin);
        edName = (EditText) findViewById(R.id.ed_order_name);
        edSdt = (EditText) findViewById(R.id.ed_order_sdt);
        edEmail = (EditText) findViewById(R.id.ed_order_email);
        edDiachi = (EditText) findViewById(R.id.ed_order_diachi);
        edSoluong = (EditText) findViewById(R.id.ed_order_soluong);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUserDetails();
        // getting product details from intent
        Intent i = getIntent();
        Log.d("Json","Vào intend");

        // getting product id (pid) from intent
        masanpham = i.getStringExtra("masanpham");
        tensanpham = i.getStringExtra("tensanpham");
        Log.d("Json",masanpham);
        giaban_int = Integer.parseInt(i.getStringExtra("giaban_int"));
        giaban = NumberFormat.getInstance().format(giaban_int)+ " VND";
        hinhdaidien = i.getStringExtra("hinhdaidien");
        soluong = Integer.parseInt(i.getStringExtra("soluong"));

        //giaban = i.getStringExtra("giaban_int");
        //Log.d("Json",giaban);
        //lấy thông tin tài khoản
        tendangnhap = user.get(SessionManager.KEY_TENDANGNHAP);
        hoten = user.get(SessionManager.KEY_HOTEN);
        sodienthoai = user.get(SessionManager.KEY_SODIENTHOAI);
        email = user.get(SessionManager.KEY_EMAIL);
        diachi = user.get(SessionManager.KEY_DIACHI);

        //load hình ảnh sản phẩm
        new DownloadImageTask((ImageView) findViewById(R.id.iv_order_hinhdaidien))
                .execute(url_root.url + "Image/small/" + hinhdaidien);
        tvTensp.setText(tensanpham);
        tvGiaban.setText(giaban);

        btnThongtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edName.setText(hoten);
                edSdt.setText(sodienthoai);
                edEmail.setText(email);
                edDiachi.setText(diachi);
            }
        });

        btnDathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((edName.getText().toString().trim().length() == 0)||(edSoluong.getText().toString().trim().length() == 0)||(edSdt.getText().toString().trim().length() == 0)||(edEmail.getText().toString().trim().length() == 0)||(edDiachi.getText().toString().trim().length() == 0)){
                    alert.showAlertDialog(Order.this, "Đặt hàng thất bại", "Vui lòng nhập đầy đủ thông tin", false);
                }
                else if(soluong < (soluongnhap = Integer.parseInt(edSoluong.getText().toString()))) {
                    alert.showAlertDialog(Order.this, "Đặt hàng thất bại", "Số lượng tồn không đủ", false);
                }
                else{
                    InsertData task1 = new InsertData();
                    task1.execute(new String[]{url_root.url + "insert_order.php"});
                }
            }
        });

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }

    private class InsertData extends AsyncTask<String, Void, Boolean>{
        ProgressDialog dialog = new ProgressDialog(Order.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Inserting data...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            for(String url1 : urls){
                try {
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("txtTendangnhap", tendangnhap));
                    pairs.add(new BasicNameValuePair("txtName", edName.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtSDT", edSdt.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtEmail", edEmail.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtDiachi", edDiachi.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtSoluong", edSoluong.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtMaSP", masanpham));
                    pairs.add(new BasicNameValuePair("txtGiaban", giaban_int.toString()));
                    Log.d("Json", masanpham);
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(pairs));
                    HttpResponse response = client.execute(post);

                } catch (ClientProtocolException e){
                    Toast.makeText(Order.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(Order.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result == true){
                Toast.makeText(Order.this, "Đặt hàng thành công", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(Order.this, MainActivity.class);
                startActivity(intent1);
            }else{
                Toast.makeText(Order.this, "Đặt hàng thất bại", Toast.LENGTH_LONG).show();
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
                Intent dangky = new Intent(Order.this, RegisterActivity.class);
                startActivity(dangky);
                return true;
            case R.id.it_thongtin:
                Intent thongtin = new Intent(Order.this, UserInfoActivity.class);
                startActivity(thongtin);
                return true;
            case R.id.it_donhang:
                Intent donhang = new Intent(Order.this, ViewOrdersActivity.class);
                startActivity(donhang);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
