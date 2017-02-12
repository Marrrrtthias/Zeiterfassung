package de.tastykatana.zeiterfassung;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.io.Files;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("de.tastykatana.zeiterfassung", appContext.getPackageName());
    }
    @Test
    public void createPdf_isWorking() throws IOException {
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 100, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
        TextView content = new TextView(getContext());
        content.setText("hallo");
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);

        // write the document content
        // TODO share to another app to view the created pdf
        document.writeTo(new FileOutputStream(outFile));

        // close the document
        document.close();
    }
}
