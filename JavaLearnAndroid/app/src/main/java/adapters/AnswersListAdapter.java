package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import controllers.ChaptersController;
import controllers.QuestionsController;
import controllers.UserAnswersController;
import models.Answers;
import models.Changes;
import models.Chapters;
import models.Questions;
import models.UserAnswers;
import ru.dev58.javalearn.R;

/**
 * Created by Дмитрий on 23.11.2016.
 */

public class AnswersListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Answers> answers;
    LayoutInflater inflater;

    public AnswersListAdapter(Context context, ArrayList<Answers> answers) {
        this.context = context;
        this.answers = answers;
        this.inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        sort();
    }

    public void setAnswers(ArrayList<Answers> answers) {
        this.answers = answers;
        sort();
    }

    private void sort () {
        Collections.sort(answers, new Comparator<Answers>() {
            @Override
            public int compare(Answers answers1, Answers answers2) {
                if(answers1.getRoworder() > answers2.getRoworder()) return 1;
                if(answers1.getRoworder() < answers2.getRoworder()) return -1;
                return 0;
            }
        });
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int i) {
        return answers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.answers_layout, viewGroup, false);
        }
        final Answers answers = (Answers) getItem(i);
        TextView answerTitle = (TextView)view.findViewById(R.id.answer_text);
        answerTitle.setText(answers.getTitle());

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.select_answer);
        linearLayout.removeAllViews();
        final CheckBox checkBox = new CheckBox(context);
        checkBox.setChecked(answers.isSelected());
        linearLayout.addView(checkBox);

        final Questions question = QuestionsController.instance().getById(answers.getQuestion_id());
        UserAnswers userAnswers = question.getUserAnswerInCurrent(answers.getId());
        if(userAnswers == null)
            userAnswers = question.getUserAnswerInAdded(answers.getId());
        UserAnswers forRemoved = question.getUserAnswerInRemoved(answers.getId());
        if(userAnswers != null) {
            if(forRemoved == null || !userAnswers.equals(forRemoved)) {
                checkBox.setChecked(true);
                answers.setSelected(true);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBox.setChecked(!checkBox.isChecked());
                answers.setSelected(checkBox.isChecked());
                if(answers.isSelected()) {
                    question.addForAdded(answers);
                } else {
                    question.addForRemoved(answers);
                }
            }
        });

        return view;
    }
}
