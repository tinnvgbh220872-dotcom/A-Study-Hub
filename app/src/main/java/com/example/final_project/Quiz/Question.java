package com.example.final_project.Quiz;

public class Question {
    private int id;
    private String language;
    private String question;
    private String code;
    private String[] options;
    private String answer;

    public Question(int id, String language, String question, String code, String[] options, String answer) {
        this.id = id;
        this.language = language;
        this.question = question;
        this.code = code;
        this.options = options;
        this.answer = answer;
    }

    public int getId() { return id; }
    public String getLanguage() { return language; }
    public String getQuestion() { return question; }
    public String getCode() { return code; }
    public String[] getOptions() { return options; }
    public String getAnswer() { return answer; }
}


