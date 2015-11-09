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
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hifivesoccer.R;
import com.hifivesoccer.activities.AppActivity;
import com.hifivesoccer.models.Game;
import com.hifivesoccer.models.User;
import com.hifivesoccer.utils.base64ToBitmap;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FiendsListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<User> userList;
    private List<User> userListAdded;
    private Bitmap storedBitmap = null;

    //ImageLoader imageLoader = AppActivity.getInstance().getImageLoader();

    public FiendsListAdapter(Activity activity, List<User> userList, List<User> userListAdded) {
        this.activity = activity;
        this.userList = userList;
        this.userListAdded = userListAdded;
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
        name.setTag(userList.get(position).get_id());

        ImageButton addButton = (ImageButton) convertView.findViewById(R.id.addToList);
        ImageButton removeButton = (ImageButton) convertView.findViewById(R.id.removeToList);

        if (userListAdded.contains(userList.get(position))) {
            addButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.VISIBLE);
        }
        else {
            addButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.GONE);
        }

        CircleImageView userPicture = (CircleImageView) convertView.findViewById(R.id.user_avatar);
        userPicture.setTag(userList.get(position).get_id());

        if (userList.get(position).getPicture() != null) {

            Bitmap bm = base64ToBitmap.getBitmap(userList.get(position).getPicture(), 3);
            userPicture.setImageBitmap(bm);

        }

        name.setText(String.valueOf(userList.get(position).getUsername()));


        return convertView;
    }
}
