package de.tastykatana.zeiterfassung;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.SortedSet;
import java.util.TreeSet;

public class ShowSessionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sessions);

        TextView txtViewShowSessions = (TextView) findViewById(R.id.txtVwShowSessions);

        SortedSet<WorkSession> sessions = MyApp.zeiterfassung.getAllSessions();

        StringBuilder sessionsStr = new StringBuilder();
        for (WorkSession session : sessions) {
            sessionsStr.append(session.toString());
            sessionsStr.append("\n");
        }

        txtViewShowSessions.setText(sessionsStr.toString());
    }
}
