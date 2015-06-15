package com.example.android.mobileapp2015;

/**
 * Created by Hoang on 13/06/2015.
 */
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_HOTEN = "hoten";
    public static final String KEY_TENDANGNHAP = "tendangnhap";
    public static final String KEY_SODIENTHOAI = "sodienthoai";
    public static final String KEY_DIACHI = "diachi";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String tendangnhap,String hoten, String sodienthoai, String email, String diachi){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString("tendangnhap", tendangnhap);

        // Storing email in pref
        editor.putString("hoten", hoten);
        editor.putString("sodienthoai", sodienthoai);
        editor.putString("email", email);
        editor.putString("diachi", diachi);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put("tendangnhap", pref.getString(KEY_TENDANGNHAP, null));
        user.put("hoten", pref.getString(KEY_HOTEN, null));
        user.put("sodienthoai", pref.getString(KEY_SODIENTHOAI, null));
        user.put("email", pref.getString(KEY_EMAIL, null));
        user.put("diachi", pref.getString(KEY_DIACHI, null));


        // user email id


        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}