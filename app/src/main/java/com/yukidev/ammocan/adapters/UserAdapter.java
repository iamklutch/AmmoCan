package com.yukidev.ammocan.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.MD5Util;
import com.yukidev.ammocan.utils.ParseConstants;

import java.util.List;

/**
 * Created by YukiDev on 5/14/2015.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> messages) {
        super(context, R.layout.message_item,messages );

        mContext = context;
        mUsers = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // the if statement recycles the view (like in recyclerview)
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.lastNameLabel = (TextView) convertView.findViewById(R.id.lastNameLabel);
            holder.squadronLabel = (TextView) convertView.findViewById(R.id.squadronLabel);
            holder.checkImageView = (ImageView)convertView.findViewById(R.id.checkImageView);
            holder.supervisorImageView = (ImageView)convertView.findViewById(R.id.supervisorImageView);
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();
        String supervisor = user.getObjectId();

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";

            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);
        }
        try {
            if (ParseUser.getCurrentUser().getString(ParseConstants.KEY_SUPERVISOR_ID).equals(supervisor)) {
                holder.supervisorImageView.setImageResource(R.drawable.avatar_supervisor);
                holder.supervisorImageView.setVisibility(View.VISIBLE);
            } else {
                holder.supervisorImageView.setVisibility(View.INVISIBLE);
            }
        } catch (NullPointerException e) {
            holder.supervisorImageView.setVisibility(View.INVISIBLE);
        }
        holder.nameLabel.setText(user.getString(ParseConstants.KEY_DISPLAY_NAME));
        holder.lastNameLabel.setText(user.getString(ParseConstants.KEY_LASTNAME));
        holder.squadronLabel.setText(user.getString(ParseConstants.KEY_SQUADRON));

        GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)) {
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
    public static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
        TextView lastNameLabel;
        TextView squadronLabel;
        ImageView supervisorImageView;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }


}
