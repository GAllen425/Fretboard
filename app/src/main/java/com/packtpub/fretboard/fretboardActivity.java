package com.packtpub.fretboard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
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

        // TODO bitmap cuts off at bottom/ drawing is scaled incorrectly
        // see the myfile.png output for example


        Fretboard myFretboard = new Fretboard(tuningNoteArrayList,chosenNotesArrayList);
        myFretboard.mapFretboard();
        Bitmap bitmap = Bitmap.createBitmap(1000,myFretboard.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas fretboardCanvas = new Canvas(bitmap);
        myFretboard.drawFretboard(myFretboard.getBinaryChosenMap(),fretboardCanvas);

        fretboardImageView.setImageBitmap(bitmap);
        saveToFile(bitmap, "myfile.png");
    }

    public class Fretboard {

        private String[] tuningNotesArray;
        private String[] chosenNotesArray;

        Fretboard(ArrayList<String> tuningArrayList, ArrayList<String> chosenArrayList)
        {
            tuningNotesArray = tuningArrayList.toArray(new String[0]);
            chosenNotesArray = chosenArrayList.toArray(new String[0]);
            binaryChosenMap = mapChosenNotesToFretboard(tuningNotesArray,chosenNotesArray);
            numberOfStrings = tuningNotesArray.length;
            stringX = new float[getNumberOfStrings()];
            fretY = new float[getNumberOfFrets()+1];
            Log.d("No of String","" + numberOfStrings);
        }

        private float[] stringX = new float[getNumberOfStrings()];
        private float[] fretY = new float[getNumberOfFrets()+1];

        public float[] getStringX() {
            return stringX;
        }
        public float[] getFretY() {
            return fretY;
        }
        public int getHeight() {
            return (int)(fretY[fretY.length - 1]);
        }

        private int[][] binaryChosenMap;

        private Paint myPaint = new Paint();

        // <TODO>
        // sort out screenheight, need to think of a better method than setting a flat number,
        // then dynamically increasing it
        private float screenWidth = 1000;
        private float screenHeight = 4000;
        private float stringWidth;
        private float startOfScreenX;
        private float stringSeparation = 0;
        private float fretSeparation = 0;

        public void setScreenWidth(float screenWidth) {
            this.screenWidth = screenWidth;
        }

        public void setScreenHeight(float screenHeight) {
            this.screenHeight = screenHeight;
        }

        public void setStringWidth(float stringWidth) {
            this.stringWidth = stringWidth;
        }

        public void setStartOfScreenX(float startOfScreenX) {
            this.startOfScreenX = startOfScreenX;
        }

        public void setStringSeparation(float stringSeparation) {
            this.stringSeparation = stringSeparation;
        }

        public void setFretSeparation(float fretSeparation) {
            this.fretSeparation = fretSeparation;
        }

        public float getFretSeparation() {
            return fretSeparation;
        }

        private int numberOfFrets = 12;
        private int numberOfStrings = 6;

        public void setNumberOfFrets(int numberOfFrets) {
            this.numberOfFrets = numberOfFrets;
        }

        public int getNumberOfStrings() {
            return numberOfStrings;
        }

        public int getNumberOfFrets() {
            return numberOfFrets;
        }

        public int[][] getBinaryChosenMap() {
            return binaryChosenMap;
        }

        public void mapFretboard(){
            float stringX[] = getStringX();
            float fretY[] = getFretY();
            if(getNumberOfFrets() != 0){setNumberOfFrets(12);}

            setStartOfScreenX(1000);
            setStringWidth(screenWidth/64);
            setStartOfScreenX(screenWidth/8);

            Log.d("numberOfString", "" + numberOfStrings);
            Log.d("screenWidth", "" + screenWidth);
            Log.d("screenHeight", "" + screenHeight);

            setStringSeparation((float) (1. / (numberOfStrings - 1) * (1 - 2 * startOfScreenX / screenWidth) * screenWidth));
            setFretSeparation(screenHeight / (numberOfFrets - 1));

            for (int i = 0; i < getNumberOfStrings(); i++) {
                stringX[i] = (startOfScreenX + i * stringSeparation - stringWidth / 2);
                Log.d("stringSeparation", "" + stringSeparation);
                Log.d("stringX", "String[" + i + "]" + stringX[i]);
            }

            for (int i = 1; i <= numberOfFrets + 1; i++) {
                fretY[i - 1] = i * fretSeparation;
                Log.d("fret coordinate", "" + fretY[i - 1] + " " + (i - 1));
                if(fretY[i-1] > 0.875*screenHeight)
                {
                    screenHeight*=1.25;
                }
            }
        }

        public int[][] mapChosenNotesToFretboard(String[] tuningNotes, String[] chosenNotes)
        {
            int chosenNoteBinaryMap[] = new int [12];
            for(int i=0 ; i < chosenNotes.length; i++)
            {
                String noteString = chosenNotes[i];
                int note = note_stringToValue(noteString);
                Log.d("chosen notes","|" + noteString + "|" + note);
                chosenNoteBinaryMap[note] = 1;
            }

            int numberOfGuitarStrings = tuningNotes.length;
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
                fretboardValues[i][0] = note_stringToValue(tuningNotes[i]);
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

        public void drawFretboard (int mappingArray[][], Canvas canvas) {

            float stringX[] = getStringX();
            float fretY[] = getFretY();

            myPaint.setColor(Color.BLACK);

            for (int i = 0; i < stringX.length; i++) {
                canvas.drawRect(stringX[i], screenHeight,
                        stringX[i] + stringWidth, getFretSeparation(), myPaint);
            }

            for (int i = 1; i <= numberOfFrets + 1; i++) {
                canvas.drawRect(startOfScreenX, fretY[i - 1],
                        stringWidth + stringX[numberOfStrings - 1], fretY[i - 1] + stringWidth, myPaint);
            }

            for (int i = 0; i < numberOfStrings; i++) {
                if (mappingArray[i][0] == 1) {
                    canvas.drawCircle(stringX[i] + stringWidth / 2, fretY[0] - getFretSeparation() / 2,
                            fretSeparation / 8, myPaint);
                }
                for (int j = 1; j < 12; j++) {
                    if (mappingArray[i][j] == 1) {
                        canvas.drawCircle(stringX[i] + stringWidth / 2, fretY[j - 1] + getFretSeparation() / 2,
                                fretSeparation / 8, myPaint);
                    }
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


    public void saveToFile(Bitmap bitmap, String filename){
        String path = this.getFilesDir().getAbsolutePath();
        File file = new File(path,  filename);
        FileOutputStream out = null;

        Log.d("file location", file.getAbsolutePath());

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        // GET CURRENT SIZE
        int width = bm.getWidth();
        int height = bm.getHeight();
        // GET SCALE SIZE
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }



}

