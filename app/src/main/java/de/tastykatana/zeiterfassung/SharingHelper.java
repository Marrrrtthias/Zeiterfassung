package de.tastykatana.zeiterfassung;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

/**
 * Created by matthias on 2/12/17.
 */

public class SharingHelper {

    public static void sharePdf(File pdf, Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");

        Uri uri = Uri.parse("file://" + pdf.getAbsolutePath());
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            context.startActivity(Intent.createChooser(intent, "Share PDF file"));
        } catch (Exception e) {
            Toast.makeText(context, "Error: Cannot open or share created PDF report.", Toast.LENGTH_SHORT).show();
        }
    }
}
