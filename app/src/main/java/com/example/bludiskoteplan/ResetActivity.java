package com.example.bludiskoteplan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ResetActivity extends AppCompatActivity {

    Button newGame;
    TextView body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        newGame = findViewById(R.id.resetBtn);
        body = findViewById(R.id.body);
        body.setText(getIntent().getStringExtra("points"));
        newGame.setOnClickListener(v -> {
            Platno platno = new Platno(ResetActivity.this);
            setContentView(platno);
            platno.spustiSnimanie();
        });
    }
}