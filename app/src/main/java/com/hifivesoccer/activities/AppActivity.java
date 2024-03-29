package com.hifivesoccer.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.hifivesoccer.R;
import com.hifivesoccer.utils.base64ToBitmap;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.SharedPref;
import com.hifivesoccer.utils.Token;

import java.util.ArrayList;


public class AppActivity extends AppCompatActivity {

    public static final String TAG = AppActivity.class.getSimpleName();

    private final Context context = this;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    private static AppActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Get user accounts email
     * See setAutoCompleteText for set this on text field
     * @return List of emails or empty array
     */
    public ArrayList<String> getUserAccountEmail() {

        ArrayList<String> stringArrayList = new ArrayList<>();

        try{
            AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account[] accounts = manager.getAccountsByType("com.google");
            //Pattern emailPattern = Patterns.EMAIL_ADDRESS;

            for (Account account : accounts) {
                stringArrayList.add(account.name);
            }
        }
        catch(Exception e)
        {
            Log.i("Exception", "Exception:" + e) ;
        }

        return stringArrayList;
    }

    /**
     * Add auto-complete suggestions list on text field
     * Use AutoCompleteTextView text field
     * See getUserAccountEmail for example list
     * @param AutoCompleteTextViewId Id of AutoCompleteTextView
     * @param stringArrayList Suggestions list
     */
    public void setAutoCompleteText(int AutoCompleteTextViewId, ArrayList<String> stringArrayList) {

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(AutoCompleteTextViewId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);
        textView.setAdapter(adapter);
    }

    /**
     * Get value of EditText
     * @param EditTextId EditText Id
     * @return Value of field
     */
    public String getEditTextValue(int EditTextId) {

        EditText editTextEmail = (EditText) findViewById(EditTextId);
        return editTextEmail.getText().toString();
    }

    /**
     * Check if device is connected on internet
     * @return boolean
     */
    public boolean deviceIsConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Dialog alert to confirm exit
     * Remove ACTIVE_USER and user token
     */
    public void confirmExit() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.dialog_exit_msg)
                .setTitle(R.string.dialog_exit_title);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Logout user
     * Remove user in SharedPref and token
     */
    public void logout() {
        MySelf.setSelf(this, null);
        SharedPref.deleteMyself((Activity) context);
        Token.deleteToken((Activity) this);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public static synchronized AppActivity getInstance() {
        return mInstance;
    }

}
