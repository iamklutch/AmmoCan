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
 * Created by James on 5/14/2015.
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
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Log.d("TEST:  ", gravatarUrl);

            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.drawable.avatar_empty)
                    .into(holder.userImageView);
        }

        holder.nameLabel.setText(user.getUsername());
        holder.lastNameLabel.setText(user.getString(ParseConstants.KEY_LASTNAME));
        holder.squadronLabel.setText(user.getString(ParseConstants.KEY_SQUADRON));

        GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)) {
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_LONG).show();
//                ParseUser clickedUser =  mUsers.get(position);
//                String clickedId = clickedUser.getObjectId();
//                Intent intent = new Intent();
//                intent.putExtra("objectId", clickedId);
//            }
//        });

        return convertView;
    }
    public static class ViewHolder {
        ImageView userImageView;
        ImageView checkImageView;
        TextView nameLabel;
        TextView lastNameLabel;
        TextView squadronLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }


}
