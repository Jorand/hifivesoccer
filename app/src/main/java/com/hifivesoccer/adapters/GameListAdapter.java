package com.hifivesoccer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hifivesoccer.R;
import com.hifivesoccer.models.Game;

import java.util.List;

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

        TextView serial = (TextView) convertView.findViewById(R.id.user_name);
        TextView title = (TextView) convertView.findViewById(R.id.game_location);

        serial.setText(String.valueOf(gameList.get(position).getInfos().getTitle()));
        title.setText(gameList.get(position).getInfos().getTitle());

        return convertView;
    }
}
