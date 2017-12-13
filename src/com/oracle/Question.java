/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tmcginn
 */
public class Question {

    private String text;
    private final List<String> code;
    private final List<Answer> answers;

    public Question() {
        answers = new ArrayList<>();
        code = new ArrayList<>();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addAnswer(String answerText) {
        Answer answer = new Answer();
        // Remove any trailing white space
        answerText = answerText.trim();
        if (answerText.lastIndexOf('*') == (answerText.length() - 1)) {
            answer.setCorrect(true);
            answerText = answerText.substring(0, (answerText.length() - 1));
        }
        answer.setText(answerText);
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public int getNumCorrect() {
        int correct = 0;
        correct = answers.stream().filter((answer) -> (answer.isCorrect())).map((_item) -> 1).reduce(correct, Integer::sum);
        return correct;
    }

    public String getCode() {
        if (code.isEmpty()) {
            return null;
        }
        String codeString = "";
        for (String codeLine : code) {
            codeString+=(codeLine + "\n");
        }
        return codeString;
    }

    public void addCode(String codeLine) {
        code.add(codeLine);
    }

}
