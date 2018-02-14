package com.packtpub.fretboard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.ContactsContract;
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

import java.lang.reflect.Array;
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


        Bitmap bitmap = Bitmap.createBitmap(200,500, Bitmap.Config.ARGB_8888);
        Canvas fretboardCanvas = new Canvas(bitmap);

        Intent i = getIntent();
        Bundle extrasBundle = i.getExtras();

        TextView fretboardTuningTextView = (TextView) findViewById(R.id.fretboardTuningTextView);
        TextView chosenTextView = (TextView) findViewById(R.id.chosenTextView);

        String tuningStringFinal = "E A D G B E";
        if (extrasBundle.containsKey("tuningString")) {
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
            chosenNotesArrayList.add(st2.nextToken());
        }
        // TODO decide whether to pass canvas, bitmap or imageview?
        // TODO write function that draws strings then focus on frets after
        int Array[][] = mapChosenNotesToFretboard(tuningNoteArrayList,chosenNotesArrayList);
        drawFretboard(tuningNoteArrayList, chosenNotesArrayList,Array,bitmap,fretboardCanvas);
        Bitmap fretboardBitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
        fretboardImageView.setImageBitmap(fretboardBitmap);
    }

    //<TODO> draws incorrect number of strings i.e. 9 strings if there are 3 chosen notes and 6 tuning notes
    public void drawFretboard (ArrayList tuningNoteArrayList, ArrayList chosenNotesArrayList, int mappingArray[][], Bitmap bitmap, Canvas fretboardCanvas)
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

        for(int i=0; i<numberOfStrings; i++)
        {
            for(int j=0; j<12; j++)
            {
                if(mappingArray[i][j] == 1)
                {
                    fretboardCanvas.drawCircle(stringX[i]+stringWidth/2, stringY[j]+stringSeparation/2, stringSeparation/4, myPaint);
                }
            }
        }

    }

    public int note_stringToValue(String note)
    {
        int noteValue = 0;
        //defaults to C
        if (note.equals("C")) { noteValue = 0; }
        else if (note.equals("C#")) { noteValue = 1; }
        else if (note.equals("D")) { noteValue = 2; }
        else if (note.equals("D#")) { noteValue = 3; }
        else if (note.equals("E")) { noteValue = 4; }
        else if (note.equals("F")) { noteValue = 5; }
        else if (note.equals("F#")) { noteValue = 6; }
        else if (note.equals("G")) { noteValue = 7; }
        else if (note.equals("G#")) { noteValue = 8; }
        else if (note.equals("A")) { noteValue = 9; }
        else if (note.equals("A#")) { noteValue = 10; }
        else if (note.equals("B")) { noteValue = 11; }
        return noteValue;
    }

    public String note_valueToString(int number) {
        String letter = new String();
        if (number == 0) { letter = "C"; }
        else if (number == 1) { letter = "C#"; }
        else if (number == 2) { letter = "D"; }
        else if (number == 3) { letter = "D#"; }
        else if (number == 4) { letter = "E"; }
        else if (number == 5) { letter = "F"; }
        else if (number == 6) { letter = "F#"; }
        else if (number == 7) { letter = "G"; }
        else if (number == 8) { letter = "G#"; }
        else if (number == 9) { letter = "A"; }
        else if (number == 10) { letter = "A#"; }
        else if (number == 11) { letter = "B"; }
        return letter;
    }

    public int[][] mapChosenNotesToFretboard(ArrayList tuningNotes, ArrayList chosenNotes)
    {
        int chosenNoteBinaryMap[] = new int [12];
        for(int i=0 ; i < chosenNotes.size(); i++)
        {
            String noteString = chosenNotes.get(i).toString();
            int note = note_stringToValue(noteString);
            Log.d("chosen notes","|" + noteString + "|" + note);
            chosenNoteBinaryMap[note] = 1;
        }

        int numberOfGuitarStrings = tuningNotes.size();
        int [][] fretboardValues = new int [numberOfGuitarStrings][13];
        int[][] mapping = new int [numberOfGuitarStrings][13];
        // 0 | 1 2 3 4 5 6 7 8 9 10 12
        // 0 | 1 2 3 4 5 6 7 8 9 10 12
        // 0 | 1 2 3 4 5 6 7 8 9 10 12
        // 0 | 1 2 3 4 5 6 7 8 9 10 12
        // 0 | 1 2 3 4 5 6 7 8 9 10 12
        // 0 | 1 2 3 4 5 6 7 8 9 10 12

        for(int i=0; i < numberOfGuitarStrings; i++)
        {
            fretboardValues[i][0] = note_stringToValue(tuningNotes.get(i).toString());
            if ( chosenNoteBinaryMap[fretboardValues[i][0]] == 1)
            {
                mapping[i][0]=1;
            }
            else
            {
                mapping[i][0]=0;
            }
            Log.d("binaryMap", "" + i + ": " + mapping[i][0]);

            for(int j=1; j<13; j++)
            {
                fretboardValues[i][j] = (fretboardValues[i][j-1] + 1) % 12;
                if ( chosenNoteBinaryMap[fretboardValues[i][j]] == 1 )
                {
                    mapping[i][j] = 1;
                }
                else
                {
                    mapping[i][j] = 0;
                }
                Log.d("binaryMap", "" + i + ": " + mapping[i][j]);
            }
            Log.d("binaryMap", "NEXT STRING");
        }
        return mapping;
    }

}

