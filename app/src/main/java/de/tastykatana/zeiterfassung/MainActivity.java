package de.tastykatana.zeiterfassung;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

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

        // check for storage permission
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder explanation = new AlertDialog.Builder(this);
            explanation.setTitle(R.string.permission_explanation_title);
            explanation.setMessage(R.string.permissions_explanation_content);
            explanation.setCancelable(true);
            explanation.setPositiveButton("OK", null);
            explanation.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            });
            explanation.show();
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

        // assemble Layout for Stundenzettel
        ViewGroup stundezettel = MyApp.zeiterfassung.buildStundenzettel(this);

        // do some magic and draw the main layout to the page
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(575, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(822, View.MeasureSpec.EXACTLY);
        stundezettel.measure(measuredWidth, measuredHeight);
        stundezettel.layout(0, 0, stundezettel.getMeasuredWidth(), stundezettel.getMeasuredHeight());
        page.getCanvas().translate(20, 20);
        stundezettel.draw(page.getCanvas());

        // finish the page
        doc.finishPage(page);

        // write the document content to storage
        File sdCard = Environment.getExternalStorageDirectory();
        File outFile = new File(sdCard.getAbsolutePath() + File.separator + "Documents" + File.separator + "Stundenzettel", "test.pdf");
        outFile.getParentFile().mkdirs();
        try {
            doc.writeTo(new FileOutputStream(outFile));
            Log.d("export", "file '" + outFile.getName() + "' saved in '" + outFile.getAbsolutePath() + "'");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_database:
                // create AlertDialog to ask if user really wants to delete everything
                AlertDialog.Builder dbDeleteDialogBuilder = new AlertDialog.Builder(this);
                dbDeleteDialogBuilder.setTitle(R.string.delete_data_dialog_title)
                        .setMessage(R.string.delete_data_dialog_message);
                // add buttons to dialog
                dbDeleteDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button, delete all data from sessions table
                        MyApp.zeiterfassung.deleteAll();
                    }
                });
                dbDeleteDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog, do nothing
                    }
                });

                // show AlertDialog
                dbDeleteDialogBuilder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


