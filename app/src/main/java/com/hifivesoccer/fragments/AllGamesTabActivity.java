package com.hifivesoccer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.activities.GameDetailActivity;
import com.hifivesoccer.activities.NewGameActivity;
import com.hifivesoccer.activities.ProfilActivity;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllGamesTabActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private GameListAdapter adapter;
    private List<Game> gameList;

    private static final String TAG = AllGamesTabActivity.class.getSimpleName();
    private final Context context = getActivity();
    private final ServerHandler server = ServerHandler.getInstance(context);

    private int requestQueue = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v =inflater.inflate(R.layout.tab_all_games,container,false);

        listView = (ListView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(this);

        gameList = new ArrayList<>();
        listView.setEmptyView( v.findViewById( R.id.empty_list_view ) );
        adapter = new GameListAdapter(getActivity(), gameList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                updateList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GameDetailActivity.class);
                intent.putExtra("GAME_ID", gameList.get(position).get_id());
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*
        // Example snackbar
        Snackbar snackbar = Snackbar
            .make(getView(), "Pas de connexion internet !", Snackbar.LENGTH_LONG)
            .setAction("Réessayer", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar1 = Snackbar.make(getView(), "Nouvelle tentative de connexion…", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                }
            });

        // Changing message text color
        //snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        //View sbView = snackbar.getView();
        //TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        //textView.setTextColor(Color.YELLOW);
        //snackbar.show();

        snackbar.show();
        */
    }

    @Override
    public void onRefresh() {
        updateList();
    }

    private void updateList() {

        swipeRefreshLayout.setRefreshing(true);

        server.getAllGames(new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());

                JSONArray serializedGames = (JSONArray) response;

                gameList.clear();

                for (int i = 0; i < serializedGames.length(); i++) {
                    try {
                        JSONObject serializedGame = serializedGames.getJSONObject(i);
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            final Game game = mapper.readValue(serializedGame.toString(), Game.class);
                            game.initPeoples(context, new Game.initHandler() {
                                @Override
                                public void handle() {
                                    gameList.add(game);
                                    adapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast toast = Toast.makeText(getActivity(), R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast toast = Toast.makeText(getActivity(), R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String error){
                Log.e(TAG, error);
                swipeRefreshLayout.setRefreshing(false);
                Toast toast = Toast.makeText(getActivity(), R.string.hifive_generic_error, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    private void updateGameList(Game game) {
        gameList.add(game);
        if(requestQueue < 1){
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void openProfil(View view) {
        Log.d(TAG, "Onclikck");
    }

}