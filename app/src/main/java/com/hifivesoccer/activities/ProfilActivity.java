package com.hifivesoccer.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppActivity {

    private static final String TAG = ProfilActivity.class.getSimpleName();
    private final Context context = this;
    private final ServerHandler server = ServerHandler.getInstance(context);

    private Toolbar toolbar;

    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String userId = getIntent().getStringExtra("USERS_ID");

        getUser(userId);
    }

    private void getUser(String id) {

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage(context.getString(R.string.loading));
        progress.show();

        server.getUser(id, new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

                JSONObject serializedUser = (JSONObject) response;

                ObjectMapper mapper = new ObjectMapper();
                try {
                    final User user = mapper.readValue(serializedUser.toString(), User.class);

                    if(user != null){
                        Log.d(TAG, user.toString());

                        myUser = user;
                        updateProfil();
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
                progress.dismiss();
                Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void updateProfil() {

        CircleImageView profilPicture = (CircleImageView) findViewById(R.id.act_profil_picture);

        if (myUser != null) {

            byte[] decodedString = Base64.decode(myUser.getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            profilPicture.setImageBitmap(decodedByte);
        }

        TextView profilUsername = (TextView) findViewById(R.id.act_profil_username);
        profilUsername.setText(myUser.getUsername());

        TextView profilPosition = (TextView) findViewById(R.id.act_profil_position);
        profilPosition.setText(myUser.getPosition());

        TextView profilPayed = (TextView) findViewById(R.id.act_profil_played);
        profilPayed.setText(myUser.getPlayed());

        TextView profilWins = (TextView) findViewById(R.id.act_profil_wins);
        profilWins.setText(myUser.getWins());

    }

}
