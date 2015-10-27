package com.hifivesoccer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Auto-complete suggestions user account emails
        ArrayList<String> accountsEmails = getUserAccountEmail();
        setAutoCompleteText(R.id.act_login_email, accountsEmails);

        EditText passwordField = (EditText) findViewById(R.id.act_login_password);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleVisibility(View view) {

        EditText editText = (EditText) findViewById(R.id.act_login_password);
        CheckBox checkBox = (CheckBox) findViewById(R.id.togglePasswordVisibility);

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (checkBox.isChecked()) {
            editText.setTransformationMethod(null);
        }
        else {
            editText.setTransformationMethod(new PasswordTransformationMethod());
        }

        editText.setSelection(start, end);
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

    public void onClickLogin(View view) {

        final String email = getEditTextValue(R.id.act_login_email);
        final String password = getEditTextValue(R.id.act_login_password);

        if (!email.isEmpty() && !password.isEmpty()) {

            // login

        } else {
            Toast toast = Toast.makeText(this, R.string.act_login_from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onClickRegister(View view) {

        Toast toast = Toast.makeText(this, "Register", Toast.LENGTH_SHORT);
        toast.show();

    }
}
