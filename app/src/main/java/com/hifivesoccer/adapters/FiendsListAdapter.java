package com.hifivesoccer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hifivesoccer.R;
import com.hifivesoccer.activities.AppActivity;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jorand on 06/11/2015.
 */
public class FiendsListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<User> userList;

    //ImageLoader imageLoader = AppActivity.getInstance().getImageLoader();

    public FiendsListAdapter(Activity activity, List<User> userList) {
        this.activity = activity;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
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
            convertView = inflater.inflate(R.layout.list_friends, null);

        TextView name = (TextView) convertView.findViewById(R.id.user_fullname);

        //if (imageLoader == null)
        //    imageLoader = AppActivity.getInstance().getImageLoader();
        //NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.user_avatar);

        //thumbNail.setImageUrl(userList.get(position).getProfile().getPicture(), imageLoader);

        name.setText(String.valueOf(userList.get(position).getProfile().getUsername()));


        return convertView;
    }
}
