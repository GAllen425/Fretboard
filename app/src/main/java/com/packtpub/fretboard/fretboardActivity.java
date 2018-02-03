package com.packtpub.fretboard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fretboard);

        ScrollView fretboardScrollView = (ScrollView) findViewById(R.id.fretboardScrollView);
        ImageView fretboardImageView = (ImageView) findViewById(R.id.fretboardImageView);

        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        //int width = fretboardScrollView.getWidth();
        //int height = fretboardScrollView.getHeight();

        Log.d("onCreate", "width: " + width);
        Log.d("onCreate", "height: " + height);


        Bitmap bitmap = Bitmap.createBitmap(100,300, Bitmap.Config.ARGB_8888);
        Canvas fretboardCanvas = new Canvas(bitmap);

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
        drawFretboard(tuningNoteArrayList, chosenNotesArrayList,bitmap,fretboardCanvas);
        Bitmap fretboardBitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
        fretboardImageView.setImageBitmap(fretboardBitmap);
    }

    public void drawFretboard (ArrayList tuningNoteArrayList,  ArrayList chosenNotesArrayList, Bitmap bitmap, Canvas fretboardCanvas)
    {
        Paint myPaint = new Paint();
        myPaint.setColor(Color.BLACK);

        int screenWidth = bitmap.getWidth();
        int screenHeight = bitmap.getHeight();

        float stringWidth = screenWidth/64;
        float startOfScreenX = screenWidth/8;

        int numberOfStrings = tuningNoteArrayList.size();
        int numberOfChosenNotes = chosenNotesArrayList.size();

        float stringX[] = new float[numberOfStrings];
        Log.d("numberOfString", "" + numberOfStrings);
        Log.d("screenWidth", "" + screenWidth);

        float stringSeparation = 0;
        for(int i=0; i < numberOfStrings ; i++) {
            stringSeparation = (float)(1./(numberOfStrings-1)*(1-2*startOfScreenX/screenWidth)*screenWidth);
            stringX[i] = (startOfScreenX + i*stringSeparation - stringWidth/2);
            Log.d("stringSeparation", "" + stringSeparation);
            Log.d("stringX", "String[" +i+ "]" + stringX[i]);
            fretboardCanvas.drawRect(stringX[i], screenHeight, stringX[i] + stringWidth, 0, myPaint);
        }
        int numberOfFrets = 12;
        float stringY[] = new float[numberOfFrets];
        for(int i=0; i<numberOfFrets; i++){
            stringY[i] = i*(screenHeight/(numberOfFrets-1));
            fretboardCanvas.drawRect(startOfScreenX, stringY[i],stringWidth + stringX[numberOfStrings-1], stringY[i] + stringWidth,myPaint);
        }
    }

}

