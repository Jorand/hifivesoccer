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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hifivesoccer.R;
import com.hifivesoccer.fragments.MyGamesTabActivity;
import com.hifivesoccer.fragments.NotificationsTabActivity;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.base64ToBitmap;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGameListAdapter extends BaseAdapter {

    private Activity context;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Game> gameList;

    public MyGameListAdapter(Activity activity, List<Game> gameList) {
        this.activity = activity;
        this.gameList = gameList;
        this.context = activity;
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

        final MyGameListAdapter adapter = this;

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_my_games, null);

        TextView organizerName = (TextView) convertView.findViewById(R.id.list_game_organizer_name);
        TextView location = (TextView) convertView.findViewById(R.id.list_game_location);
        CircleImageView organizerAvatar = (CircleImageView) convertView.findViewById(R.id.list_game_organizer_avatar);
        TextView date = (TextView) convertView.findViewById(R.id.list_game_date);
        TextView time = (TextView) convertView.findViewById(R.id.list_game_time);
        TextView textIntro = (TextView) convertView.findViewById(R.id.list_game_msg);

        TextView playersText = (TextView) convertView.findViewById(R.id.list_game_players);

        organizerName.setText(String.valueOf(gameList.get(position).getOrganizer().getUsername()));
        location.setText(String.valueOf(gameList.get(position).getPlace()));
        date.setText(String.valueOf(gameList.get(position).getDate()));
        time.setText(String.valueOf(gameList.get(position).getTime()));

        if (gameList.get(position).getOrganizer().getPicture() != null) {

            Bitmap bm = base64ToBitmap.getBitmap(gameList.get(position).getOrganizer().getPicture());
            organizerAvatar.setImageBitmap(bm);

        }

        List<User> players = gameList.get(position).getTeamA();
        List<User> playersB = gameList.get(position).getTeamB();
        players.addAll(playersB);
        String playerString = "";

        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).get_id().equals(MySelf.getSelf().get_id())) {
                players.remove(i);
            }
        }

        if (players.size() > 0) {
            playerString += "Avec ";
            playerString += players.get(0).getUsername();

            if (players.size() == 2 ) {
                playerString += " et ";
                playerString += players.get(1).getUsername();
            }
            else if (players.size() > 2) {
                playerString += " et ";
                playerString += (players.size()-1);
                playerString += " autres personnes";
            }

            playersText.setText(playerString);
        }
        else {
            playersText.setVisibility(View.GONE);
        }

        TextView gameEnd = (TextView) convertView.findViewById(R.id.list_game_end);


        final String game_id = gameList.get(position).get_id();

        RelativeLayout winner_wrap = (RelativeLayout) convertView.findViewById(R.id.list_game_winner_text);
        TextView winnerTeam = (TextView) convertView.findViewById(R.id.list_game_winner);

        if (gameList.get(position).isDone()) {
            gameEnd.setVisibility(View.VISIBLE);
            winner_wrap.setVisibility(View.VISIBLE);
            if (gameList.get(position).getWinner().equals("A"))
                winnerTeam.setText(R.string.act_game_detail_team_a);
            if (gameList.get(position).getWinner().equals("B"))
                winnerTeam.setText(R.string.act_game_detail_team_b);
            textIntro.setText(R.string.act_mygame_organizer_done);
        }
        else {
            convertView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    MyGamesTabActivity.openGame(v, context, gameList, adapter, game_id);
                }

            });
        }

        return convertView;
    }
}
