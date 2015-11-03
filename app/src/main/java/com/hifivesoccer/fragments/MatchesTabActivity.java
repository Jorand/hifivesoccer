package com.hifivesoccer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hifivesoccer.R;
import com.hifivesoccer.utils.ServerHandler;

import org.json.JSONArray;
import org.json.JSONObject;

public class MatchesTabActivity extends Fragment {
    private static final String TAG = MatchesTabActivity.class.getSimpleName();
    private final Context context = getActivity();
    private final ServerHandler server = ServerHandler.getInstance(context);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        View v =inflater.inflate(R.layout.tab_matches, container, false);
        return v;
    }
}