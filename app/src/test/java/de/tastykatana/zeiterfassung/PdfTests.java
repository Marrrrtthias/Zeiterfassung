package de.tastykatana.zeiterfassung;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by matthias on 2/11/17.
 */

public class PdfTests {

    @Test
    public void createPdf_isWorking() throws IOException {
        String filename = "/home/matthias/Documents/test.pdf";
        String message = "hallo ";

        PDDocument doc = new PDDocument();
        try
        {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDFont font = PDType1Font.HELVETICA_BOLD;

            PDPageContentStream contents = new PDPageContentStream(doc, page);
            contents.beginText();
            contents.setFont(font, 12);
            contents.newLineAtOffset(100, 700);
            contents.showText(message);
            contents.newLineAtOffset(0, -12);
            contents.showText("test");
            contents.endText();
            contents.close();

            doc.save(filename);
        }
        finally
        {
            doc.close();
        }
    }
}
