package com.packtpub.fretboard;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class fretboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fretboard);

        Intent i = getIntent();
        Bundle extrasBundle = i.getExtras();

        TextView fretboardTuningTextView = (TextView)findViewById(R.id.fretboardTuningTextView);
        TextView chosenTextView = (TextView)findViewById(R.id.chosenTextView);
        if(extrasBundle.containsKey("chosenNotes")){
            chosenTextView.setText(extrasBundle.getString("chosenNotes"));
        } else {
            chosenTextView.setText("Not set");
        }
        if(extrasBundle.containsKey("chosenNotes")){
            fretboardTuningTextView.setText(extrasBundle.getString("tuningString"));
        } else {
            fretboardTuningTextView.setText("Must set a tuning!");
        }



    }
}
