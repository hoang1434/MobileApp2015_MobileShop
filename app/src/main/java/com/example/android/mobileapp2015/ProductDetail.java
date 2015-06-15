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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Hoang on 12/06/2015.
 */
public class ProductDetail extends Activity {

    Url url_root = new Url();
    TextView tvProductName,tvGiaban,tvTinhtrang, tvMota, tvBaohanh;
    Button btnMua;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_layout);
        tvProductName = (TextView) findViewById(R.id.tv_productName);
        tvGiaban = (TextView) findViewById(R.id.tv_giaban);
        tvTinhtrang = (TextView) findViewById(R.id.tv_tinhtrang);
        tvMota = (TextView) findViewById(R.id.tv_mota);
        tvBaohanh = (TextView) findViewById(R.id.tv_baohanh);
        btnMua = (Button)findViewById(R.id.btn_mua);
        Bundle bdLaydl = new Bundle();
        bdLaydl = getIntent().getBundleExtra("goiDL");
        final String masanpham = bdLaydl.getString("masanpham");
        String strUrl = url_root.url + "get_product_detail.php";
        session = new SessionManager(getApplicationContext());
        //tvProductName.setText(masanpham);
        LoadProduct task1 = new LoadProduct();
        task1.execute(new String[]{url_root.url + "get_product_detail.php?pid=" + masanpham});


    }

    private class LoadProduct extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(ProductDetail.this);
        String text1 = "";
        String masanpham = "";
        String tensanpham = "";
        String hinhdaidien="";
        String giaban;
        Integer giaban_int = 0;
        Integer soluong = 0;
        String mota = "";
        String baohanh = "";
        String hinhlon = "";
        ArrayList<String> list1;

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
                    Toast.makeText(ProductDetail.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(ProductDetail.this, e.toString(), Toast.LENGTH_LONG).show();
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
                    masanpham = jsonObject.getString("masanpham");
                    tensanpham = jsonObject.getString("tensanpham");
                    hinhlon = jsonObject.getString("hinhlon");
                    hinhdaidien = jsonObject.getString("hinhdaidien");
                    baohanh = jsonObject.getString("baohanh");
                    mota = jsonObject.getString("mota");
                    giaban_int = jsonObject.getInt("giaban");
                    giaban = NumberFormat.getInstance().format(jsonObject.getInt("giaban"))+ " VND";
                    soluong = jsonObject.getInt("soluong");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                Toast.makeText(ProductDetail.this, "Load success", Toast.LENGTH_LONG).show();
                tvProductName.setText(tensanpham);
                tvBaohanh.setText(baohanh);
                tvGiaban.setText(giaban);
                tvMota.setText(mota);
                if(soluong > 0){
                    tvTinhtrang.setText("Còn hàng");
                }else {
                    tvTinhtrang.setText("Hết hàng");
                }
                new DownloadImageTask((ImageView) findViewById(R.id.iv_hinhlon))
                        .execute(url_root.url + "Image/large/" + hinhlon);

                btnMua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent in = new Intent(ProductDetail.this, Order.class);
                        // sending pid to next activity
                        in.putExtra("masanpham", masanpham);
                        in.putExtra("tensanpham", tensanpham);
                        in.putExtra("giaban_int", giaban_int.toString());
                        in.putExtra("hinhlon", hinhlon);
                        in.putExtra("soluong", soluong.toString());
                        in.putExtra("hinhdaidien",hinhdaidien);

                        // starting new activity and expecting some response back
                        startActivityForResult(in, 100);
                    }
                });

            } else {
                Toast.makeText(ProductDetail.this, "Load false", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
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
                Intent dangky = new Intent(ProductDetail.this, RegisterActivity.class);
                startActivity(dangky);
                return true;
            case R.id.it_thongtin:
                Intent thongtin = new Intent(ProductDetail.this, UserInfoActivity.class);
                startActivity(thongtin);
                return true;
            case R.id.it_donhang:
                Intent donhang = new Intent(ProductDetail.this, ViewOrdersActivity.class);
                startActivity(donhang);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
