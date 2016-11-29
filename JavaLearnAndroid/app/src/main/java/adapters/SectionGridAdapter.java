package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import core.CurrentUser;
import core.Settings;
import models.Sections;
import ru.dev58.javalearn.ChaptersActivity;
import ru.dev58.javalearn.MainActivity;
import ru.dev58.javalearn.QuestionsActivity;
import ru.dev58.javalearn.R;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class SectionGridAdapter extends BaseAdapter {

    Context context;
    ArrayList<Sections> sections;
    LayoutInflater inflater;

    public SectionGridAdapter(Context context, ArrayList<Sections> sections) {
        this.context = context;
        Collections.sort(sections, new Comparator<Sections>() {
            @Override
            public int compare(Sections section1, Sections section2) {
                if(section1.getRoworder() > section2.getRoworder()) return 1;
                if(section1.getRoworder() < section2.getRoworder()) return -1;
                return 0;
            }
        });
        this.sections = sections;
        this.inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sections.size();
    }

    @Override
    public Object getItem(int i) {
        return sections.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.sections_layout, viewGroup, false);
        }
        final Sections section = (Sections) getItem(i);
        TextView sectionTitle = (TextView)view.findViewById(R.id.section_title);
        sectionTitle.setText(section.getRoworder() + ". " + section.getTitle());
        sectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChaptersActivity.class);
                intent.putExtra("_section_id", section.getId());
                context.startActivity(intent);
            }
        });

        TextView sectionDescription = (TextView)view.findViewById(R.id.section_description);
        sectionDescription.setText(section.getShortDescription());
        sectionDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChaptersActivity.class);
                intent.putExtra("_section_id", section.getId());
                context.startActivity(intent);
            }
        });

        TextView question = (TextView) view.findViewById(R.id.section_questions);

        ((TextView)view.findViewById(R.id.section_chapters_count)).setText("Глав в разделе: " + section.getChaptersCount());
        if(CurrentUser.getCurrentUser() == null) {
            question.setTextSize(10);
            question.setText("Зарегистрируйтесь для прохождения тестов!");
            return view;
        }
        if(!Settings.isOnline) {
            question.setTextSize(10);
            question.setText("Для прохождения тестов необходим доступ в интернет!");
            return view;
        }
        if(section.getAllQuestionsCount() == 0) {
            question.setText("В разделе нет тестов");
            return view;
        }
        question.setText("Набранный балл " + section.getPassedQuestionsCount() + " из " + section.getAllQuestionsCount());
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuestionsActivity.class);
                intent.putExtra("_section_id", section.getId());
                context.startActivity(intent);
            }
        });


        return  view;
    }
}
