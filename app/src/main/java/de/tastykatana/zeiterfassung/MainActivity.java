package de.tastykatana.zeiterfassung;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btnStartStop;
    private Button btnExport;
    private CheckBox chckbxCorrectTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        btnExport = (Button) findViewById(R.id.btnExport);
        chckbxCorrectTimes = (CheckBox) findViewById(R.id.chckbxCorrectTimes);

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clicked export", Toast.LENGTH_SHORT).show();
                // TODO trigger file export
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

    @Override
    protected void onPause() {
        super.onPause();

        // TODO write Zeiterfassungsdata to storage
    }

    // OnClickListener to start the Zeiterfassung, set correct label for btnStartStop and activate StopOnclickListener
    class StartOnClickListener implements View.OnClickListener {

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


