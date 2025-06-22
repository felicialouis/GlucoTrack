package edu.uph.m23si3.glucotrack.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Model.Notification;
import edu.uph.m23si3.glucotrack.R;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notificationArrayList;
    Context context;

    public NotificationAdapter(ArrayList<Notification> notificationArrayList, Context context) {
        super(context, R.layout.notification_item, notificationArrayList);
        this.notificationArrayList = notificationArrayList;
        this.context = context;
    }

    private static class MyViewHolder{
        ImageView imgType;
        TextView txvTitle, txvDesc, txvTime;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Notification notification = getItem(position);
        final View result;

        MyViewHolder myViewHolder;

        if (convertView == null) {
            myViewHolder = new MyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_item, parent, false);

            myViewHolder.imgType = (ImageView) convertView.findViewById(R.id.imgType);
            myViewHolder.txvTitle = (TextView) convertView.findViewById(R.id.txvTitle);
            myViewHolder.txvDesc = (TextView) convertView.findViewById(R.id.txvDesc);
            myViewHolder.txvTime = (TextView) convertView.findViewById(R.id.txvTime);

            convertView.setTag(myViewHolder);
        } else{
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        result = convertView;
        myViewHolder.imgType.setImageResource(notification.getImgType());
        myViewHolder.txvTitle.setText(notification.getTitle());
        myViewHolder.txvDesc.setText(notification.getDesc());
        myViewHolder.txvTime.setText(notification.getTime());
        return result;
    }
}
