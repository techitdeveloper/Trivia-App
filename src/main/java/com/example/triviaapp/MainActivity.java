package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import data.AnswerListAsyncResponse;
import model.Question;
import model.QuestionBank;
import model.Score;
import util.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvCounter, tvQuestion;
    private Button btnTrue, btnFalse;
    private ImageView btnPrev, btnNext;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private TextView tvScore, tvHighScore;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score(); //new score object
        prefs = new Prefs(MainActivity.this);

        tvCounter = findViewById(R.id.tvCounter);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvHighScore = findViewById(R.id.tvHighScore);
        tvHighScore.setText(String.valueOf(prefs.getHighestScore()));

        btnTrue = findViewById(R.id.btnTrue);
        btnFalse = findViewById(R.id.btnFalse);

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        btnTrue.setOnClickListener(this);
        btnFalse.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                tvQuestion.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                tvCounter.setText(currentQuestionIndex+1 +" / "+ questionArrayList.size());
            }
        });

        currentQuestionIndex = prefs.getState();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnTrue:
                 checkAnswer(true);

                 updateQuestion();

                break;
            case R.id.btnFalse:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.btnPrev:
                if (currentQuestionIndex > 0)
                {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.btnNext:
                goNext();
                break;
        }
    }

    private void checkAnswer(boolean userChoice) {
        boolean answerTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        if (userChoice == answerTrue)
        {
            fadeView();
            addScore();
            Toast.makeText(this, "Correct Answer", Toast.LENGTH_SHORT).show();

        }
        else
        {
            shakeAnimation();
            reduceScore();
            Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();

        }

    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        tvQuestion.setText(question);
        tvCounter.setText(currentQuestionIndex+1 +" / "+ questionList.size());
    }

    public void fadeView()
    {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation()
    {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void addScore()
    {
        scoreCounter += 10;
        score.setScore(scoreCounter);
        tvScore.setText(String.valueOf(score.getScore()));
    }

    public void reduceScore()
    {
        if (scoreCounter > 0)
        {
            scoreCounter -= 10;
            score.setScore(scoreCounter);
            tvScore.setText(String.valueOf(score.getScore()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
    }

    public void goNext()
    {
        currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
        updateQuestion();
    }
}