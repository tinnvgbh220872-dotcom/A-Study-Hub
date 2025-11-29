package com.example.final_project.Quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private TextView tvLanguage, tvQuestion, tvCode, tvScore, tvQuestionNumber;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNext, btnBack;

    private ArrayList<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_advance_activity);
        userEmail = getIntent().getStringExtra("email");

        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        tvLanguage = findViewById(R.id.tvLanguage);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCode = findViewById(R.id.tvCode);
        tvScore = findViewById(R.id.tvScore);
        rgOptions = findViewById(R.id.rgOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        QuizRepository repo = new QuizRepository(this);
        ArrayList<Question> allQuestions = repo.getAllQuestions();

        if (allQuestions.size() == 0) {
            Toast.makeText(this, "No questions found!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Collections.shuffle(allQuestions);
        questionList = new ArrayList<>();
        int limit = Math.min(10, allQuestions.size());
        for (int i = 0; i < limit; i++) questionList.add(allQuestions.get(i));

        showQuestion(currentQuestionIndex);
        updateScoreText();

        btnNext.setOnClickListener(v -> {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(QuizActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRb = findViewById(selectedId);
            checkAnswer(selectedRb.getText().toString());

            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                showQuestion(currentQuestionIndex);
                updateScoreText();
            } else {
                tvScore.setText("Score: " + score + "/" + questionList.size() + "\n" + getMotivationMessage());
                btnNext.setEnabled(false);
                btnBack.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(QuizActivity.this, MainScreenActivity.class);
            intent.putExtra("email", userEmail);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void showQuestion(int index) {
        Question q = questionList.get(index);

        tvQuestionNumber.setText("Question " + (index + 1) + " of " + questionList.size());
        tvLanguage.setText(q.getLanguage());
        tvQuestion.setText(q.getQuestion());

        if (q.getCode() != null && !q.getCode().isEmpty()) {
            tvCode.setVisibility(View.VISIBLE);
            tvCode.setText(q.getCode());
        } else {
            tvCode.setVisibility(View.GONE);
        }

        ArrayList<String> opts = new ArrayList<>();
        Collections.addAll(opts, q.getOptions());
        Collections.shuffle(opts);

        rbOption1.setText(opts.get(0));
        rbOption2.setText(opts.get(1));
        rbOption3.setText(opts.get(2));
        rbOption4.setText(opts.get(3));

        rgOptions.clearCheck();
    }

    private void checkAnswer(String selectedOption) {
        Question q = questionList.get(currentQuestionIndex);
        if (selectedOption.equals(q.getAnswer())) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong! Answer: " + q.getAnswer(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateScoreText() {
        tvScore.setText("Score: " + score + "/" + questionList.size());
    }

    private String getMotivationMessage() {
        int percent = (int) ((score * 100.0) / questionList.size());
        if (percent == 100) return "Perfect! Excellent work!";
        if (percent >= 80) return "Great job! Keep it up!";
        if (percent >= 50) return "Good effort! You can do even better!";
        return "Don't worry, practice makes perfect!";
    }
}
