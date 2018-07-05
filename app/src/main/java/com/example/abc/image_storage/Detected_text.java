package com.example.abc.image_storage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Detected_text extends AppCompatActivity {

    TextView txt_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected_text);

        Intent intent = getIntent();
        String textToPutInTextView = intent.getStringExtra("text");



        TextView txt_View = (TextView) findViewById(R.id.Text_Here);
        txt_View.setText(textToPutInTextView);
    }
}
