package com.packtpub.fretboard;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class fretboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView fretboardImageView = (ImageView) findViewById(R.id.fretboardImageView);
        Bitmap fretboardBitmap = Bitmap.createBitmap(400, 800, Bitmap.Config.ARGB_8888);
        Paint myStringPaint = new Paint();
        Canvas fretboardCanvas = new Canvas(fretboardBitmap);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fretboard);

        Intent i = getIntent();
        Bundle extrasBundle = i.getExtras();

        TextView fretboardTuningTextView = (TextView) findViewById(R.id.fretboardTuningTextView);
        TextView chosenTextView = (TextView) findViewById(R.id.chosenTextView);

        String tuningStringFinal = "E A D G B E";
        if (extrasBundle.containsKey("chosenNotes")) {
            tuningStringFinal = extrasBundle.getString("tuningString");
            fretboardTuningTextView.setText(tuningStringFinal);
        } else {
            fretboardTuningTextView.setText("Must set a tuning!");
        }

        StringTokenizer st = new StringTokenizer(tuningStringFinal, " ");
        ArrayList tuningNoteArrayList = new ArrayList(st.countTokens());
        while (st.hasMoreTokens()) {
            tuningNoteArrayList.add(st.nextToken());
        }

        String chosenNotesFinal = "" ;
        if (extrasBundle.containsKey("chosenNotes")) {
            chosenNotesFinal = extrasBundle.getString("chosenNotes");
            chosenTextView.setText(chosenNotesFinal);
        } else {
            chosenTextView.setText("Not set");
        }

        StringTokenizer st2 = new StringTokenizer(chosenNotesFinal, " ");
        ArrayList chosenNotesArrayList = new ArrayList(st2.countTokens());
        while (st2.hasMoreTokens()) {
            tuningNoteArrayList.add(st2.nextToken());
        }
        // TODO decide whether to pass canvas, bitmap or imageview?
        // TODO write function that draws strings then focus on frets after
        drawFretboard(tuningNoteArrayList, chosenNotesArrayList);
    }

    public void drawFretboard (ArrayList tuningNoteArrayList,  ArrayList chosenNotesArrayList)
    {

    }

}

