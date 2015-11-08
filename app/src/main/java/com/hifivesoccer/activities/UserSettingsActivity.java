package com.hifivesoccer.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class UserSettingsActivity extends AppActivity {

    private static final String TAG = FriendsListActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;

    private static int RESULT_LOAD_IMAGE = 1;
    String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        EditText username = (EditText) findViewById(R.id.act_settings_username);
        EditText position = (EditText) findViewById(R.id.act_settings_position);

        if(MySelf.getSelf() != null){
            Log.d(TAG, MySelf.getSelf().get_id());
            username.setText(MySelf.getSelf().getUsername());
            position.setText(MySelf.getSelf().getPosition());

            if (MySelf.getSelf().getPicture() != null) {

                byte[] decodedString = Base64.decode(MySelf.getSelf().getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                ImageView imgView = (ImageView) findViewById(R.id.act_settings_picture);
                imgView.setImageBitmap(decodedByte);
            }

        } else {
            Log.d(TAG, "MySelf is null");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                updateProfile();
                return true;
            default:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {

        String usernameText = getEditTextValue(R.id.act_settings_username);
        String positionText = getEditTextValue(R.id.act_settings_position);

        if (!usernameText.isEmpty()) {

            JSONObject json = new JSONObject();
            try {
                json.put("_id", MySelf.getSelf().get_id());
                json.put("username", usernameText);
                json.put("position", positionText);

                final ProgressDialog progress = new ProgressDialog(context);
                progress.setMessage(context.getString(R.string.loading));
                progress.show();

                Log.d(TAG, json.toString());

                server.putDatas("user", json, new ServerHandler.ResponseHandler() {
                    @Override
                    public void onSuccess(Object response) {
                        Log.d(TAG, response.toString());

                        JSONObject serializedUser = (JSONObject) response;
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            final User user = mapper.readValue(serializedUser.toString(), User.class);

                            if(user != null){
                                Log.d(TAG, user.toString());
                                if (user.get_id().equals(MySelf.getSelf().get_id())) {

                                    MySelf.setSelf(user);

                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        progress.dismiss();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, error);
                        Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                        toast.show();
                        progress.dismiss();
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, e.toString());
                Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Toast toast = Toast.makeText(context, R.string.from_empty, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickPicture(View view) {
        // TODO update profile picture

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);

        //updateProfile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                String encodedImageData = getEncoded64ImageStringFromBitmap(BitmapFactory.decodeFile(picturePath));

                updateUserPicture(encodedImageData);

            } else {
                Toast.makeText(this, R.string.act_user_settings_picture_empty, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.hifive_generic_error, Toast.LENGTH_SHORT).show();
        }

    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string

        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    public void updateUserPicture(final String encodedImage) {

        JSONObject json = new JSONObject();
        try {
            json.put("_id", MySelf.getSelf().get_id());
            json.put("picture", encodedImage);

            final ProgressDialog progress = new ProgressDialog(context);
            progress.setMessage(context.getString(R.string.loading));
            progress.show();

            Log.d(TAG, json.toString());

            server.putDatas("user", json, new ServerHandler.ResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    Log.d(TAG, response.toString());

                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ImageView imgView = (ImageView) findViewById(R.id.act_settings_picture);
                    imgView.setImageBitmap(decodedByte);

                    JSONObject serializedUser = (JSONObject) response;
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        final User user = mapper.readValue(serializedUser.toString(), User.class);

                        if(user != null){
                            Log.d(TAG, user.toString());
                            if (user.get_id().equals(MySelf.getSelf().get_id())) {

                                MySelf.setSelf(user);

                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    progress.dismiss();
                    Toast toast = Toast.makeText(context, R.string.act_user_settings_picture_done, Toast.LENGTH_SHORT);
                    toast.show();
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, error);
                    Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                    toast.show();
                    progress.dismiss();
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
