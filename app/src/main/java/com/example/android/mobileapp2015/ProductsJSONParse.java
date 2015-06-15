package com.example.android.mobileapp2015;

/**
 * Created by Hoang on 12/06/2015.
 */
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** A class to parse json data */
public class ProductsJSONParse {

    // Receives a JSONObject and returns a list
    public List<HashMap<String,Object>> parse(JSONObject jObject){

        JSONArray jProducts = null;
        try {
            // Retrieves all the elements in the 'countries' array
            jProducts = jObject.getJSONArray("products");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Invoking getCountries with the array of json object
        // where each json object represent a country
        return getProducts(jProducts);
    }

    private List<HashMap<String, Object>> getProducts(JSONArray jProducts){
        int productCount = jProducts.length();
        List<HashMap<String, Object>> productList = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> product = null;

        // Taking each country, parses and adds to list object
        for(int i=0; i<productCount;i++){
            try {
                // Call getCountry with country JSON object to parse the country
                product = getProduct((JSONObject) jProducts.get(i));
                productList.add(product);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return productList;
    }

    // Parsing the Country JSON object
    private HashMap<String, Object> getProduct(JSONObject jProduct){

        HashMap<String, Object> product = new HashMap<String, Object>();
        String masanpham = "";
        String tensanpham = "";
        String hinhdaidien="";
        String giaban;
        Integer soluong = 0;
        String mota = "";
        String baohanh = "";
        Url url_root = new Url();

        try {
            masanpham = jProduct.getString("masanpham");
            tensanpham = jProduct.getString("tensanpham");
            hinhdaidien = jProduct.getString("hinhdaidien");
            soluong = jProduct.getInt("soluong");
            mota = jProduct.getString("mota");
            baohanh = jProduct.getString("baohanh");

            giaban = NumberFormat.getInstance().format(jProduct.getInt("giaban"))+ " VND";

            product.put("masanpham", masanpham);
            product.put("tensanpham", tensanpham);
            product.put("flag", R.drawable.blank);
            product.put("hinhdaidien", url_root.url+"Image/small/"+hinhdaidien);
            product.put("giaban", giaban);
            product.put("soluong", soluong);
            product.put("mota", mota);
            product.put("baohanh", baohanh);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
    }
}