package com.example.android.mobileapp2015;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;


public class MainActivity extends Activity {

    Url url_root = new Url();
    ListView mListView;
    Context context;
    SessionManager session;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
        // URL to the JSON data
        String strUrl = url_root.url+"get_all_products.php";

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);

        // Getting a reference to ListView of activity_main
        mListView = (ListView) findViewById(R.id.lv_products);



    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Error", e.toString());
        }finally{
            iStream.close();
        }

        return data;
    }



    /** AsyncTask to download json data */
    private class DownloadTask extends AsyncTask<String, Integer, String>{
        String data = null;
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading data...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);

            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, "Load success", Toast.LENGTH_LONG).show();
            // The parsing of the xml data is done in a non-ui thread
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

            // Start parsing xml data
            listViewLoaderTask.execute(result);
            dialog.dismiss();
        }
    }

    /** AsyncTask to parse json data and load ListView */
    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter>{

        JSONObject jObject;
        // Doing the parsing of xml data in a non-ui thread
        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try{
                jObject = new JSONObject(strJson[0]);
                ProductsJSONParse productJsonParser = new ProductsJSONParse();
                productJsonParser.parse(jObject);
            }catch(Exception e){
                Log.d("JSON Exception1",e.toString());
            }

            // Instantiating json parser class
            ProductsJSONParse productJsonParser = new ProductsJSONParse();

            // A list object to store the parsed countries list
            List<HashMap<String, Object>> products = null;

            try{
                // Getting the parsed data as a List construct
                products = productJsonParser.parse(jObject);
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }

            // Keys used in Hashmap
            String[] from = { "tensanpham","flag","giaban"};

            // Ids of views in listview_layout
            int[] to = { R.id.tv_product,R.id.iv_hinhdaidien,R.id.tv_mota};

            // Instantiating an adapter to store each items
            // R.layout.listview_layout defines the layout of each item
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), products, R.layout.lv_layout, from, to);

            return adapter;
        }

        /** Invoked by the Android on "doInBackground" is executed */
        @Override
        protected void onPostExecute(final SimpleAdapter adapter) {

            // Setting adapter for the listview
            mListView.setAdapter(adapter);

            for(int i=0;i<adapter.getCount();i++){
                final HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                String imgUrl = (String) hm.get("hinhdaidien");

                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

                HashMap<String, Object> hmDownload = new HashMap<String, Object>();
                hm.put("hinhdaidien",imgUrl);
                hm.put("position", i);

                // Starting ImageLoaderTask to download and populate image in the listview
                imageLoaderTask.execute(hm);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, Object> hm1 = (HashMap<String, Object>) adapter.getItem(position);
                        final String masanpham = (String) hm1.get("masanpham");
                        Intent intent1 = new Intent(MainActivity.this,ProductDetail.class);
                        Bundle bdTruyenDL = new Bundle();

                        bdTruyenDL.putString("masanpham",masanpham);
                        intent1.putExtra("goiDL",bdTruyenDL);
                        startActivity(intent1);
                    }
                });
            }
        }
    }

    /** AsyncTask to download and load an image in ListView */
    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>>{

        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {

            InputStream iStream=null;
            String imgUrl = (String) hm[0].get("hinhdaidien");
            int position = (Integer) hm[0].get("position");

            URL url;
            try {
                url = new URL(imgUrl);

                // Creating an http connection to communicate with url
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                // Getting Caching directory
                File cacheDirectory = getBaseContext().getCacheDir();

                // Temporary file to store the downloaded image
                File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"+position+".png");

                // The FileOutputStream to the temporary file
                FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                // Creating a bitmap from the downloaded inputstream
                Bitmap b = BitmapFactory.decodeStream(iStream);

                // Writing the bitmap to the temporary file as png file
                b.compress(Bitmap.CompressFormat.PNG,100, fOutStream);

                // Flush the FileOutputStream
                fOutStream.flush();

                //Close the FileOutputStream
                fOutStream.close();

                // Create a hashmap object to store image path and its position in the listview
                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();

                // Storing the path to the temporary image file
                hmBitmap.put("flag",tmpFile.getPath());

                // Storing the position of the image in the listview
                hmBitmap.put("position",position);

                // Returning the HashMap object containing the image path and position
                return hmBitmap;


            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final HashMap<String, Object> result) {
            // Getting the path to the downloaded image
            String path = (String) result.get("flag");

            // Getting the position of the downloaded image
            int position = (Integer) result.get("position");

            // Getting adapter of the listview
            final SimpleAdapter adapter = (SimpleAdapter ) mListView.getAdapter();

            // Getting the hashmap object at the specified position of the listview
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);

            // Overwriting the existing path in the adapter
            hm.put("flag", path);

            // Noticing listview about the dataset changes
            adapter.notifyDataSetChanged();

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
                Intent dangky = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(dangky);
                return true;
            case R.id.it_thongtin:
                Intent thongtin = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(thongtin);
                return true;
            case R.id.it_donhang:
                Intent donhang = new Intent(MainActivity.this, ViewOrdersActivity.class);
                startActivity(donhang);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
