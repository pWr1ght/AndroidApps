package com.example.connectthree;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    boolean activePlay= true;
    int activePlayer=0;
    int[] state = {2,2,2,2,2,2,2,2,2,2};
    int[][] winPos = {{0,1,2}, {0,4,8},{0,3,6},{3,4,5},{6,7,8},{1,4,7},{2,5,8},{6,4,2}};
    public void dropIn(View view) {
        ImageView counter = (ImageView) view;
        Log.i("Tag", counter.getTag().toString());
        int tapCount = Integer.parseInt(counter.getTag().toString());
        if (state[tapCount] == 2 && activePlay) {
            state[tapCount] = activePlayer;
            counter.setTranslationY(-1500);
            if (activePlayer == 0) {
                counter.setImageResource(R.drawable.yellow);
                activePlayer = 1;
            } else {
                counter.setImageResource(R.drawable.red);
                activePlayer = 0;
            }
            counter.animate().translationYBy(1500).rotation(420).setDuration(600);
        }
        for (int[] winPosition : winPos) {
            String win = "";
            if (state[winPosition[0]] == state[winPosition[1]] && state[winPosition[0]] == state[winPosition[2]] && state[winPosition[1]] != 2) {
                if (activePlayer == 1) {
                    win = "Yellow";
                } else {
                    win = "Red";
                }
                activePlay = false;
                Toast.makeText(this, win + " is the Winner!", Toast.LENGTH_LONG).show();
                Button playAgain = (Button) findViewById(R.id.buttonWin);
                TextView winText = (TextView) findViewById(R.id.textView);
                winText.setText(win + " is the Winner!");
                playAgain.setVisibility(View.VISIBLE);
                winText.setVisibility(View.VISIBLE);
            }

        }
        int twoCount=0;
        for (int count = 0; count < state.length; count++) {
            if (state[count] != 2) {
                twoCount +=1;
            }
        }

        if (twoCount == 9) {
            TextView winText = (TextView) findViewById(R.id.textView);
            Button playAgain = (Button) findViewById(R.id.buttonWin);
            winText.setText("Cats Game! Play Again!");
            winText.setVisibility(View.VISIBLE);
            playAgain.setVisibility(View.VISIBLE);


        }
    }

    public void playAgains(View view) {
        Log.i("info", "pressed");
        Button playAgainButton = (Button) findViewById(R.id.buttonWin);
        TextView winText = (TextView) findViewById(R.id.textView);
        playAgainButton.setVisibility(View.INVISIBLE);
        winText.setVisibility(View.INVISIBLE);
        androidx.gridlayout.widget.GridLayout gridLayout = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridValue);
        for(int x = 0; x< gridLayout.getChildCount(); x++) {
            ImageView child = (ImageView) gridLayout.getChildAt(x);
            child.setImageDrawable(null);
        }
        activePlay= true;
        activePlayer=0;
        for (int i=0; i < state.length; i++) {
            state[i] = 2;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
