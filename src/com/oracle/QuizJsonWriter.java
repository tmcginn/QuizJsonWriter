/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author tmcginn
 */
public class QuizJsonWriter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java QuizJsonWriter <input text question file> <output JSON file>");
            System.exit(-1);
        }
        
        System.out.println("Opening this file for reading: " + args[0]);
        System.out.println("Writing to this file:          " + args[1]);
        
        // Create an array of questions
        List<Question> inQuestions = new ArrayList<>();
        try {
            // Read the quiz strings from the text file
            List<String> records = readFile(args[0]);

            // Loop through the lines of text
            for (int i = 0; i < records.size(); i++) {
                // Create a question object
                Question quizItem = new Question();
                String record = records.get(i);
                // Check for Q this demarks a question
                if (record.indexOf('Q') == 0) {
                    int colonPos = record.indexOf(':');
                    String question = record.substring(colonPos + 2);
                    quizItem.setText(question);
                    //System.out.println(question);

                    // Determine if the question contains code
                    i++;
                    record = records.get(i);
                    if (record.startsWith("Code:")) {
                        String code = null;
                        // Get the number of code lines
                        int numCodeLines = Integer.parseInt(record.trim().substring(5));
                        // Read the code lines and add them to the question
                        for (int j = 0;j < numCodeLines;j++) {
                            i++;
                            quizItem.addCode(records.get(i));
                        }
                        
                        i++;
                        record = records.get(i);
                    }

                    // Finding a question also means the next strings are answers until the next question
                    while (record.length() > 0 && record.indexOf(' ') != 0) {
                        int dotPos = record.indexOf('.');
                        String answer = record.substring(dotPos + 2);
                        quizItem.addAnswer(answer);
                        i++;
                        record = records.get(i);
                    }
                }
                // Add the question object to the array
                inQuestions.add(quizItem);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }

        // Now generate JSON from the questions
        createQuizJson(inQuestions, args[1]);

    }

    private static List<String> readFile(String filename)
            throws Exception {
        String line;
        List<String> records = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            // use the readLine method of the BufferedReader to read one line at a time.
            // the readLine method returns null when there is nothing else to read.
            while ((line = bufferedReader.readLine()) != null) {
                records.add(line);
            }
        }
        return records;
    }

    private static void createQuizJson(List<Question> questionList, String outputFile) {
        // Build the JSON Object for the Quiz
        JSONObject quizJson = new JSONObject();
        JSONArray questionsJson = new JSONArray();

        // Take each question and add it to the JSONArray
        for (Question question : questionList) {
            // Create the JSON object to hold the question text
            JSONObject questionJson = new JSONObject();
            // What type of question is it?
            if (question.getNumCorrect() > 1) {
                questionJson.put("question_type", "MULTIPLE");
            } else {
                questionJson.put("question_type", "SINGLE");
            }
            // Add the question text
            questionJson.put("text", question.getText());
            
            // If there is code text, add it now
            if (question.getCode() != null) {
                questionJson.put("question_details", question.getCode());
            }

            // Display order and points - hard coding these for now
            //
            questionJson.put("display_order", 10);
            questionJson.put("points", 10);

            // Create a JSON array for the answers
            JSONArray answersJson = new JSONArray();

            int i = 10;
            // Create a JSON Object for each answer
            for (Answer answer : question.getAnswers()) {

                JSONObject answerJson = new JSONObject();
                // Add the answer text
                answerJson.put("label", answer.getText());
                // add the display order
                answerJson.put("display_order", i);
                // add the correct_answer_yn
                if (answer.isCorrect()) {
                    answerJson.put("correct_answer_yn", "Y");
                } else {
                    answerJson.put("correct_answer_yn", "N");
                }

                // Add this question to the array
                answersJson.add(answerJson);

                // increment the display order
                i += 10;
            }

            // Add the answers array to this questionJson
            questionJson.put("answers", answersJson);

            // Add question to the questions array
            questionsJson.add(questionJson);
        }

        // Finally add the questions to quiz
        quizJson.put("questions", questionsJson);

        // Test the output
        //System.out.println(quizJson);
        
        // Write the output to a file
        File file = new File(outputFile);
        try {
            file.createNewFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(quizJson.toString());
                writer.flush();
            }
        } catch (IOException ex) {
            System.out.println("Exception opening file: " + outputFile + " : " + ex);
        }
    }
}
