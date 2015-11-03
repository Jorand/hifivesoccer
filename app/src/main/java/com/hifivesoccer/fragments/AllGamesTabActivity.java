package com.hifivesoccer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hifivesoccer.R;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.utils.ServerHandler;

import java.util.ArrayList;
import java.util.List;

public class AllGamesTabActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private GameListAdapter adapter;
    private List<Game> gameList;

    private String URL = "http://localhost:8080/api/matchs";
    private int offSet = 0;

    private static final String TAG = MatchesTabActivity.class.getSimpleName();
    private final Context context = getActivity();
    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_home,container,false);

        server.getAllGames(new ServerHandler.ResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                Log.d(TAG, response.toString());
            }

            @Override
            public void onError(String error){
                Log.e(TAG, error);
            }
        });

        listView = (ListView) v.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        gameList = new ArrayList<>();
        adapter = new GameListAdapter(getActivity(), gameList);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {

                    fetchMovies();
                }
            }
        );

        return v;
    }

    @Override
    public void onRefresh() {
        fetchMovies();
    }

    private void fetchMovies() {

        swipeRefreshLayout.setRefreshing(true);

        //String url = URL + offSet;

        Game myGame = new Game();
        Game.Infos myGameInfos = new Game().getInfos();
        myGameInfos.setTitle("Test");
        myGameInfos.setDescription("Description");
        myGame.setInfos(myGameInfos);

        Game myGame2 = new Game();
        Game.Infos myGame2Infos = new Game().getInfos();
        myGame2Infos.setTitle("Test 2");
        myGame2Infos.setDescription("Description 2");
        myGame2.setInfos(myGame2Infos);

        gameList.add(0, myGame);
        gameList.add(0, myGame2);

        offSet = 2;

        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);

    }

}