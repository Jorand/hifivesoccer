package com.hifivesoccer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.activities.GameDetailActivity;
import com.hifivesoccer.adapters.MyGameListAdapter;
import com.hifivesoccer.adapters.NotificationListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyGamesTabActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private MyGameListAdapter adapter;
    private List<Game> gameList;

    private static final String TAG = MyGamesTabActivity.class.getSimpleName();
    private final Context context = getActivity();
    private final ServerHandler server = ServerHandler.getInstance(context);

    User myUser;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_my_games,container,false);

        listView = (ListView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(this);

        TextView textView = new TextView(getActivity());
        textView.setText(R.string.act_mygames_label);
        textView.setTextColor(getResources().getColor(R.color.grey_1));
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setPadding(30, 30, 30, 30);

        View headerView = inflater.inflate(R.layout.listview_header, null);

        listView.setEmptyView(v.findViewById(R.id.empty_list_view ));
        listView.addHeaderView(headerView);

        gameList = new ArrayList<>();
        adapter = new MyGameListAdapter(getActivity(), gameList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                updateList();
            }
        });

        return v;
    }

    @Override
    public void onRefresh() {
        updateList();
    }

    private void updateList() {

        swipeRefreshLayout.setRefreshing(true);

        server.getUser(MySelf.getSelf().get_id(), new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

                gameList.clear();

                JSONObject serializedUser = (JSONObject) response;

                ObjectMapper mapper = new ObjectMapper();
                try {
                    final User user = mapper.readValue(serializedUser.toString(), User.class);

                    if(user != null){
                        Log.d(TAG, user.toString());

                        myUser = user;
                        user.initGames(context, new User.initHandler() {
                            @Override
                            public void handle() {

                                for (int i = 0; i < myUser.getGames().size(); i++) {
                                    gameList.add(myUser.getGames().get(i));
                                }

                                Log.d(TAG, "LOL "+gameList.toString());

                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                    toast.show();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
                swipeRefreshLayout.setRefreshing(false);
                //Toast toast = Toast.makeText(context, R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                //toast.show();
            }
        });

    }

    public static void openGame(View v, Activity context, List<Game> gameList, MyGameListAdapter adapter, String game_id) {

        if (game_id != null) {
            Intent intent = new Intent(context, GameDetailActivity.class);
            intent.putExtra("GAME_ID", game_id);
            context.startActivity(intent);
        }
    }

    /*public static void removeToList(View view, final Context context, final List<Game> gameList, final MyGameListAdapter adapter, String game_id) {

        final ServerHandler server = ServerHandler.getInstance(context);

        if (game_id != null) {

            //TODO end game
            Toast toast = Toast.makeText(context, "Game end", Toast.LENGTH_SHORT);
            toast.show();

        }
    }*/
}