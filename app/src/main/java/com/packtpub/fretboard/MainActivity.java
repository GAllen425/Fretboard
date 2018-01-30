package com.packtpub.fretboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goButton = (Button)findViewById(R.id.goButton);
        goButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.goButton)
        {
            TextView tuningTextView = (TextView)findViewById(R.id.tuningTextView);
            String tuningString = tuningTextView.getText().toString();
            TextView myNotesTextView = (TextView)findViewById(R.id.myNotesTextView);
            String chosenNotes = myNotesTextView.getText().toString();
            Intent i = new Intent(this,fretboardActivity.class);
            i.putExtra("tuningString", tuningString);
            i.putExtra("chosenNotes", chosenNotes);
            startActivity(i);

        }

    }
}
