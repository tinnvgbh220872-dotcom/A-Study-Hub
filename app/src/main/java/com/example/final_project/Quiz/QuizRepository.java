package com.example.final_project.Quiz;

import android.content.Context;
import android.database.Cursor;

import com.example.final_project.Quiz.Question;
import com.example.final_project.SQL.UserDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class QuizRepository {

    private UserDatabase dbHelper;

    public QuizRepository(Context context) {
        dbHelper = new UserDatabase(context);
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM Quiz", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String language = cursor.getString(cursor.getColumnIndexOrThrow("language"));
                String questionText = cursor.getString(cursor.getColumnIndexOrThrow("question"));
                String code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
                String option1 = cursor.getString(cursor.getColumnIndexOrThrow("option1"));
                String option2 = cursor.getString(cursor.getColumnIndexOrThrow("option2"));
                String option3 = cursor.getString(cursor.getColumnIndexOrThrow("option3"));
                String option4 = cursor.getString(cursor.getColumnIndexOrThrow("option4"));
                String answer = cursor.getString(cursor.getColumnIndexOrThrow("answer"));

                String[] options = new String[]{option1, option2, option3, option4};

                Question q = new Question(id, language, questionText, code, options, answer);
                questionList.add(q);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Shuffle toàn bộ câu hỏi
        Collections.shuffle(questionList);

        return questionList;
    }
}
