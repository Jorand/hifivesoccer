package com.hifivesoccer.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Game> gameList;

    private Bitmap storedBitmap = null;

    public GameListAdapter(Activity activity, List<Game> gameList) {
        this.activity = activity;
        this.gameList = gameList;
    }

    @Override
    public int getCount() {
        return gameList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_game, null);

        RelativeLayout profile = (RelativeLayout) convertView.findViewById(R.id.list_game_profil);
        profile.setTag(gameList.get(position).getOrganizerID());

        TextView organizerName = (TextView) convertView.findViewById(R.id.list_game_organizer_name);
        TextView location = (TextView) convertView.findViewById(R.id.list_game_location);
        CircleImageView organizerAvatar = (CircleImageView) convertView.findViewById(R.id.list_game_organizer_avatar);
        TextView date = (TextView) convertView.findViewById(R.id.list_game_date);
        TextView time = (TextView) convertView.findViewById(R.id.list_game_time);

        organizerName.setText(String.valueOf(gameList.get(position).getOrganizer().getUsername()));
        location.setText(String.valueOf(gameList.get(position).getPlace()));
        date.setText(String.valueOf(gameList.get(position).getDate()));
        time.setText(String.valueOf(gameList.get(position).getTime()));

        if (gameList.get(position).getOrganizer().getPicture() != null) {
            byte[] decodedString = Base64.decode(gameList.get(position).getOrganizer().getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            organizerAvatar.setImageBitmap(decodedByte);

            if(storedBitmap != null){
                storedBitmap.recycle();
                storedBitmap = null;
            }
            storedBitmap = decodedByte;
        }

        // player list
        ArrayList<User> teamA = gameList.get(position).getTeamA();
        ArrayList<User> teamB = gameList.get(position).getTeamB();

        GridLayout gridTeamA = (GridLayout) convertView.findViewById(R.id.list_player_team_a);
        GridLayout gridTeamB = (GridLayout) convertView.findViewById(R.id.list_player_team_b);

        gridTeamA.removeAllViewsInLayout();
        gridTeamB.removeAllViewsInLayout();

        for (int i = 0; i < teamA.size(); i++) {

            View userView = inflater.inflate(R.layout.list_player_mini, null);

            CircleImageView userPicture = (CircleImageView) userView.findViewById(R.id.user_picture);

            if (teamA.get(i).getPicture() != null && !teamA.get(i).get_id().equals(gameList.get(position).getOrganizerID())) {

                byte[] decodedString = Base64.decode(teamA.get(i).getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                userPicture.setImageBitmap(decodedByte);

                userView.setTag(teamA.get(i).get_id());

                gridTeamA.addView(userView);
            }

        }

        for (int i = 0; i < teamB.size(); i++) {

            View userView = inflater.inflate(R.layout.list_player_mini, null);

            CircleImageView userPicture = (CircleImageView) userView.findViewById(R.id.user_picture);

            if (teamB.get(i).getPicture() != null && !teamB.get(i).get_id().equals(gameList.get(position).getOrganizerID())) {

                byte[] decodedString = Base64.decode(teamB.get(i).getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                userPicture.setImageBitmap(decodedByte);

                userView.setTag(teamB.get(i).get_id());

                gridTeamB.addView(userView);
            }

        }

        return convertView;
    }
}
