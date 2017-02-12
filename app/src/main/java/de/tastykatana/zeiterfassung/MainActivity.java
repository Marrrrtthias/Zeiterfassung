package de.tastykatana.zeiterfassung;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button btnStartStop;
    private Button btnExport;
    private CheckBox chckbxCorrectTimes;
    private TextView txtViewLicences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnExport = (Button) findViewById(R.id.btnExport);
        chckbxCorrectTimes = (CheckBox) findViewById(R.id.chckbxCorrectTimes);
        txtViewLicences = (TextView) findViewById(R.id.txtViewLicenses);

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowSessionsActivity.class);
                startActivity(intent);
                exportStundenzettel();
            }
        });

        txtViewLicences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LibsBuilder().withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .start(getApplicationContext());
            }
        });

        // check if Zeiterfassung is running and set appropriate OnClickListener for btnStartStop
        if (MyApp.zeiterfassung.isRunning()) {
            btnStartStop.setOnClickListener(new StopOnClickListener());
            btnStartStop.setText(getString(R.string.btnStopLbl));
        } else {
            btnStartStop.setOnClickListener(new StartOnClickListener());
            btnStartStop.setText(getString(R.string.btnStartLbl));
        }
    }

    private void exportStundenzettel() {
        PdfDocument doc = new PdfDocument();
        doc.close();

        try {
            File cacheDir = this.getCacheDir();
            File file = File.createTempFile("Stundenzettel", "pdf", cacheDir);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.stundenzettelExportFailed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // OnClickListener to start the Zeiterfassung, set correct label for btnStartStop and activate StopOnclickListener
    private class StartOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MyApp.zeiterfassung.start();
            btnStartStop.setText(getString(R.string.btnStopLbl));
            btnStartStop.setOnClickListener(new StopOnClickListener());
        }
    }

    // OnClickListener to stop the Zeiterfassung, set correct label for btnStartStop and activate StartOnclickListener
    class StopOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MyApp.zeiterfassung.stop();
            btnStartStop.setText(getString(R.string.btnStartLbl));
            btnStartStop.setOnClickListener(new StartOnClickListener());
        }
    }
}


