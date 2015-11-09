package com.hifivesoccer.fragments;

import android.app.Notification;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hifivesoccer.R;
import com.hifivesoccer.activities.GameDetailActivity;
import com.hifivesoccer.activities.ProfilActivity;
import com.hifivesoccer.adapters.GameListAdapter;
import com.hifivesoccer.adapters.NotificationListAdapter;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;
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

    private User myUser;

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

                                for (int i = 0; i < myUser.getPendings().size(); i++) {
                                    gameList.add(myUser.getPendings().get(i));
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

    public static void removeToList(View view, final Context context, final List<Game> gameList, final NotificationListAdapter adapter, String game_id) {

        final ServerHandler server = ServerHandler.getInstance(context);

        if (game_id != null) {


            for (int i = 0; i < gameList.size(); i++) {
                if (gameList.get(i).get_id().equals(game_id)) {
                    gameList.remove(i);
                }
            }
            adapter.notifyDataSetChanged();

            server.quitGame(MySelf.getSelf().get_id(), game_id);
            server.removeUserInGame(MySelf.getSelf().get_id(), game_id);

        }
    }

}