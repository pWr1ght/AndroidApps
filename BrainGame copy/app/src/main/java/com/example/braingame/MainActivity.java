package com.example.braingame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button goButton;
    ArrayList<Integer> answers = new ArrayList<Integer>();
    int location;
    TextView result;
    int score = 0;
    int numberQuestion = 0;
    TextView scoreView;
    TextView sumScore;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    TextView timerText;
    Button playAgainButton;
    ConstraintLayout gameLayout;
    ConstraintLayout boardLayout;

    public void playAgain(View view) {
        score = 0;
        numberQuestion = 0;
        timerText.setText("30s");


        newQuestion();
        scoreView.setText(Integer.toString(score) + "/" + Integer.toString(numberQuestion));
        playAgainButton.setVisibility(View.INVISIBLE);

        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long l) {
                timerText.setText(String.valueOf(l/1000) + "S");
            }

            @Override
            public void onFinish() {
                result.setText("Done!");
                playAgainButton.setVisibility(View.VISIBLE);

            }
        }.start();

    }

    public void newQuestion() {
        Random  rand = new Random();

        int a = rand.nextInt(21);
        int b = rand.nextInt(21);


        sumScore.setText(Integer.toString(a) + " + " + Integer.toString(b));

        location = rand.nextInt(4);

        answers.clear();
        for (int i = 0; i < 4; i++) {
            if(i ==  location) {
                answers.add(a+b);
            } else {
                int wrong = rand.nextInt(41);

                while(wrong == a+b) {
                    wrong = rand.nextInt(41);
                }
                answers.add(wrong);
            }

        }
        button0.setText(Integer.toString(answers.get(0)));
        button1.setText(Integer.toString(answers.get(1)));
        button2.setText(Integer.toString(answers.get(2)));
        button3.setText(Integer.toString(answers.get(3)));
    }




    public void clickAns(View view) {
        if (Integer.toString(location).equals(view.getTag().toString())) {
            result.setText("Correct!");
            score++;
        } else {
            result.setText("Wrong!");
        }
        numberQuestion++;
        scoreView.setText(Integer.toString(score) + "/" + Integer.toString(numberQuestion));
        newQuestion();
    }

    public void start(View view) {
        goButton.setVisibility(View.INVISIBLE);
        playAgain(findViewById(R.id.timeView));
        gameLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goButton = (Button) findViewById(R.id.buttonGo);
        sumScore = (TextView) findViewById(R.id.problemView);

        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        result = findViewById(R.id.result);
        scoreView = findViewById(R.id.scoreView);
        timerText =findViewById(R.id.timeView);
        playAgainButton = findViewById(R.id.playAgainButton);

//        newQuestion();
        gameLayout = findViewById(R.id.gameLayout);

        goButton.setVisibility(View.VISIBLE);

        gameLayout.setVisibility(View.VISIBLE);
    }
}
