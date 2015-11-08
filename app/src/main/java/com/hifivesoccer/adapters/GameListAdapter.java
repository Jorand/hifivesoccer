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
import android.widget.ImageView;
import android.widget.TextView;

import com.hifivesoccer.R;
import com.hifivesoccer.models.Game;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jorand on 03/11/2015.
 */
public class GameListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Game> gameList;

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

        TextView organizerName = (TextView) convertView.findViewById(R.id.list_game_organizer_name);
        TextView location = (TextView) convertView.findViewById(R.id.list_game_location);
        CircleImageView organizerAvatar = (CircleImageView) convertView.findViewById(R.id.list_game_organizer_avatar);
        TextView date = (TextView) convertView.findViewById(R.id.list_game_date);

        organizerName.setText(String.valueOf(gameList.get(position).getOrganizer().getUsername()));
        location.setText(String.valueOf(gameList.get(position).getPlace()));
        date.setText(String.valueOf(gameList.get(position).getDate()));

//        Log.d("TEST", gameList.get(position).getOrganizer().getPicture());

        if (gameList.get(position).getOrganizer().getPicture() != null) {
            byte[] decodedString = Base64.decode(gameList.get(position).getOrganizer().getPicture(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            organizerAvatar.setImageBitmap(decodedByte);
        }

        // For the avatar: https://developer.android.com/intl/en-us/training/volley/request.html

        return convertView;
    }
}
