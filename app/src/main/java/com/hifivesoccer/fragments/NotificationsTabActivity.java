package com.hifivesoccer.fragments;

import android.app.Notification;
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
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.adapters.NotificationListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.utils.MySelf;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationsTabActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    private static final String TAG = NotificationsTabActivity.class.getSimpleName();
    private final Context context = getActivity();
    private ArrayList<Game> gameList = new ArrayList<>();
    private NotificationListAdapter adapter;

    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_notifications,container,false);

        listView = (ListView) v.findViewById(R.id.listView);
        listView.setEmptyView(v.findViewById(R.id.empty_list_view ));
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new NotificationListAdapter(getActivity(), gameList);

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

        swipeRefreshLayout.setRefreshing(false);

    }
}