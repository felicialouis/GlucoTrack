package edu.uph.m23si3.glucotrack;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import edu.uph.m23si3.glucotrack.Adapter.NotificationAdapter;
import edu.uph.m23si3.glucotrack.Model.Notification;

public class NotificationActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Notification> notificationArrayList;
    private static NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = (ListView) findViewById(R.id.listView);
        notificationArrayList = Notification.notifications;

        adapter = new NotificationAdapter(notificationArrayList, getApplicationContext());
        listView.setAdapter(adapter);
    }
}