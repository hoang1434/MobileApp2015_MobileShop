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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hoang on 14/06/2015.
 */
public class ViewOrdersActivity extends Activity {
    Url url_root = new Url();
    SessionManager session;
    String tendangnhap;
    HashMap<String, String> user;
    ListView lvDonhang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        lvDonhang = (ListView) findViewById(R.id.lvOrders);
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        tendangnhap = user.get(SessionManager.KEY_TENDANGNHAP);

        LoadOrders task1 = new LoadOrders();
        task1.execute(new String[]{url_root.url + "get_list_orders.php?tendangnhap=" + tendangnhap});

    }

    private class LoadOrders extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(ViewOrdersActivity.this);
        String text1 = "";
        String masanpham = "";
        String tensanpham = "";
        String madonhang = "";
        String giaban,tongtien;
        Integer giaban_int = 0;
        Integer soluong = 0;
        Integer tongtien_int = 0;
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
                    Toast.makeText(ViewOrdersActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(ViewOrdersActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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

                list1 = new ArrayList();
                try {

                    JSONObject jsonObject = new JSONObject(text1);
                    JSONArray jOrders = null;
                    jOrders = jsonObject.getJSONArray("orders");
                    for(int i=0; i<jOrders.length();i++) {
                        JSONObject jsonData = jOrders.getJSONObject(i);
                        list1.add(jsonData.getString("madonhang"));
                        //Log.d("Json",jsonData.getString("madonhang"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        ViewOrdersActivity.this,
                        android.R.layout.simple_list_item_1,
                        list1
                );
                lvDonhang.setAdapter(adapter);
                lvDonhang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent in = new Intent(ViewOrdersActivity.this, ViewOrderDetailActivity.class);
                        // sending pid to next activity
                        //Toast.makeText(ViewOrdersActivity.this, list1.get(position), Toast.LENGTH_LONG).show();
                        in.putExtra("madonhang",list1.get(position).toString());

                        //starting new activity and expecting some response back
                        startActivityForResult(in, 100);
                    }
                });
            } else {
                Toast.makeText(ViewOrdersActivity.this, "Load false", Toast.LENGTH_LONG).show();
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
                Intent dangky = new Intent(ViewOrdersActivity.this, RegisterActivity.class);
                startActivity(dangky);
                return true;
            case R.id.it_thongtin:
                Intent thongtin = new Intent(ViewOrdersActivity.this, UserInfoActivity.class);
                startActivity(thongtin);
                return true;
            case R.id.it_donhang:
                Intent donhang = new Intent(ViewOrdersActivity.this, ViewOrdersActivity.class);
                startActivity(donhang);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
