package ru.dev58.javalearn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import adapters.AnswersListAdapter;
import controllers.AnswersController;
import controllers.QuestionsController;
import models.Questions;

/**
 * Created by Дмитрий on 23.11.2016.
 */

public class AnswersActivity extends AppCompatActivity {

    Questions question;
    int min, max;
    ImageButton prev, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        Intent intent = getIntent();
        String questionId = intent.getStringExtra("_question_id");
        if (questionId.isEmpty()) {
            return;
        }
        question  = QuestionsController.instance().getById(questionId);
        final String sectionId = question.getSection_id();
        if (question == null) return;

        final ListView answersListView = (ListView) findViewById(R.id.answers_list);
        final AnswersListAdapter answersListAdapter = new AnswersListAdapter(this, AnswersController.instance().getByQuestionId(questionId));
        answersListView.setAdapter(answersListAdapter);

        prev = (ImageButton)findViewById(R.id.answer_prev);
        next = (ImageButton)findViewById(R.id.answer_next);

        ArrayList<Questions> questions = QuestionsController.instance().getBySectionId(question.getSection_id());

        min = Collections.min(questions, new Comparator<Questions>() {
            @Override
            public int compare(Questions questions1, Questions questions2) {
                if(questions1.getRoworder() > questions2.getRoworder()) return 1;
                if(questions1.getRoworder() < questions2.getRoworder()) return -1;
                return 0;
            }
        }).getRoworder();

        max = Collections.max(questions, new Comparator<Questions>() {
            @Override
            public int compare(Questions questions1, Questions questions2) {
                if(questions1.getRoworder() > questions2.getRoworder()) return 1;
                if(questions1.getRoworder() < questions2.getRoworder()) return -1;
                return 0;
            }
        }).getRoworder();

        setDisabledButtons(question);
        setQuestionTitle(question);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Questions prevQuestion = QuestionsController.instance().getPrevBySectionId(sectionId, question);
                AnswersActivity.this.question = prevQuestion;
                answersListAdapter.setAnswers(prevQuestion.getAnswers());
                answersListAdapter.notifyDataSetChanged();
                answersListView.invalidateViews();

                setDisabledButtons(prevQuestion);
                setQuestionTitle(prevQuestion);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Questions nextQuestion = QuestionsController.instance().getNextBySectionId(sectionId, question);
                AnswersActivity.this.question = nextQuestion;
                answersListAdapter.setAnswers(nextQuestion.getAnswers());
                answersListAdapter.notifyDataSetChanged();
                answersListView.invalidateViews();

                setDisabledButtons(nextQuestion);
                setQuestionTitle(nextQuestion);
            }
        });

        ((Button)findViewById(R.id.end_test)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuestionsController.instance().saveAnswers(AnswersActivity.this.question.getSection_id());
                int rightAnswers = QuestionsController.instance().calculateRightBySectionId(AnswersActivity.this.question.getSection_id());
                Toast.makeText(AnswersActivity.this, "Набранный балл: " + rightAnswers, Toast.LENGTH_LONG).show();
                MainActivity.invalidateSections();
                AnswersActivity.this.finish();
            }
        });

    }

    private void setDisabledButtons (Questions question) {
        prev.setEnabled(true);
        next.setEnabled(true);
        prev.setBackgroundColor(Color.parseColor("#ff0099cc"));
        next.setBackgroundColor(Color.parseColor("#ff0099cc"));

        if(question.getRoworder() == min) {
            prev.setEnabled(false);
            prev.setBackgroundColor(Color.GRAY);
        }
        if(question.getRoworder() == max) {
            next.setEnabled(false);
            next.setBackgroundColor(Color.GRAY);
        }
    }

    private void setQuestionTitle (Questions question) {
        ((TextView)findViewById(R.id.question_title_all)).setText(question.getTitle());
        setTitle(question.getTitle());
    }

}
