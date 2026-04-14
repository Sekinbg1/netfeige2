package org.teleal.cling.support.shared;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import org.teleal.cling.model.ModelUtil;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TextExpandDialog {
    private static final String TAG = "TextExpandDialog";

    public static void show(Context context, String text) {
        try {
            if (text.startsWith("<") && text.endsWith(">")) {
                text = formatXml(text);
            } else if (text.startsWith("http-get")) {
                text = ModelUtil.commaToNewline(text);
            }
            
            TextView textView = new TextView(context);
            textView.setText(text);
            textView.setTextSize(12);
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setPadding(32, 32, 32, 32);
            
            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(textView);
            
            new AlertDialog.Builder(context)
                    .setTitle("Details")
                    .setView(scrollView)
                    .setPositiveButton("OK", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error displaying: " + e.toString());
        }
    }

    private static String formatXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            return org.teleal.common.xml.Util.print(document, 2);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting XML: " + e.toString());
            return xml;
        }
    }
}

