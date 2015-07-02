package com.yukidev.ammocan.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yukidev.ammocan.R;

import java.util.Date;
import java.util.List;

import com.yukidev.ammocan.R;
import com.yukidev.ammocan.utils.ParseConstants;

/**
 * Created by James on 5/14/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item,messages );

        mContext = context;
        mMessages = messages;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        // the if statement recycles the view (like in recyclerview)
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            holder.bulletTitleLabel = (TextView) convertView.findViewById(R.id.bulletTitleLabel);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseObject message = mMessages.get(position);

        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.
                getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();

        holder.timeLabel.setText(convertedDate);
        holder.iconImageView.setImageResource(R.drawable.bullets);
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        holder.bulletTitleLabel.setText(message.getString(ParseConstants.KEY_BULLET_TITLE));
        return convertView;
    }
    public static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
        TextView bulletTitleLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
