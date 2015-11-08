package com.hifivesoccer.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hifivesoccer.R;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.adapters.MyGameListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.utils.ServerHandler;

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

        listView.setEmptyView(v.findViewById(R.id.empty_list_view ));
        listView.addHeaderView(textView);

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

        // ...

        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);

    }
}