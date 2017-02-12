package de.tastykatana.zeiterfassung;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.io.File;
import java.io.FileOutputStream;
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

    /**
     * exports the Stundenzettel to /data/data/de.tastykatana.zeiterfassung/files/test.pdf
     */
    private void exportStundenzettel() {
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        // start a page
        PdfDocument.Page page = doc.startPage(pageInfo);

        // create main layout for document (this is drawn to the page in the end
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // create headline for Document and add to main layout
        TextView headline = new TextView(this);
        headline.setText(getString(R.string.stundenzettel_headline));
        headline.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
        mainLayout.addView(headline);
        headline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        headline.setGravity(View.TEXT_ALIGNMENT_CENTER);

        // TODO add worktimes to mainLayout

        // do some magic and draw the main layout to the page
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(575, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(822, View.MeasureSpec.EXACTLY);
        mainLayout.measure(measuredWidth, measuredHeight);
        mainLayout.layout(0, 0, mainLayout.getMeasuredWidth(), mainLayout.getMeasuredHeight());
        page.getCanvas().translate(20, 20);
        mainLayout.draw(page.getCanvas());

        // finish the page
        doc.finishPage(page);

        // write the document content to storage
        File outFile = new File(this.getFilesDir(), "test.pdf");
        outFile.getParentFile().mkdirs();
        try {
            doc.writeTo(new FileOutputStream(outFile));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.stundenzettelExportFailed, Toast.LENGTH_SHORT).show();
        }

        doc.close();

        SharingHelper.sharePdf(outFile, this);
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


