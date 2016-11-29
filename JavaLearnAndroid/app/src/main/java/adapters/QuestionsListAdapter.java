package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import controllers.QuestionsController;
import models.Questions;
import ru.dev58.javalearn.AnswersActivity;
import ru.dev58.javalearn.R;

/**
 * Created by Дмитрий on 23.11.2016.
 */

public class QuestionsListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Questions> questions;
    LayoutInflater inflater;

    public QuestionsListAdapter(Context context, ArrayList<Questions> questions) {
        this.context = context;
        Collections.sort(questions, new Comparator<Questions>() {
            @Override
            public int compare(Questions question1, Questions question2) {
                if(question1.getRoworder() > question2.getRoworder()) return 1;
                if(question1.getRoworder() < question2.getRoworder()) return -1;
                return 0;
            }
        });
        this.questions = questions;
        this.inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int i) {
        return questions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.questions_layout, viewGroup, false);
        }
        final Questions question = (Questions) getItem(i);
        TextView questionTitle = (TextView)view.findViewById(R.id.question_title);
        questionTitle.setText(question.getRoworder() + ". " + question.getTitle());

        questionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnswersActivity.class);
                intent.putExtra("_question_id", question.getId());
                context.startActivity(intent);
            }
        });

        return view;
    }
}
