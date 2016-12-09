package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import core.CurrentUser;
import models.Sections;
import ru.dev58.javalearn.ChaptersActivity;
import ru.dev58.javalearn.MainActivity;
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
        return sections.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.sections_layout, viewGroup, false);
        }
        final Sections section = (Sections) getItem(i);
        TextView sectionTitle = (TextView)view.findViewById(R.id.section_title);
        sectionTitle.setText(section.getTitle());
        sectionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChaptersActivity.class);
                intent.putExtra("_section_id", section.getId());
                context.startActivity(intent);
            }
        });

        ((TextView)view.findViewById(R.id.section_description)).setText(section.getDescription());
        ((TextView)view.findViewById(R.id.section_chapters_count)).setText("Глав в разделе: " + section.getChaptersCount());



        if(CurrentUser.getCurrentUser() == null) {
            ((TextView)view.findViewById(R.id.section_questions)).setTextSize(10);
            ((TextView)view.findViewById(R.id.section_questions)).setText("Зарегистрируйтесь для прохождения тестов!");
            return view;
        }
        ((TextView)view.findViewById(R.id.section_questions))
                .setText("Вопросы " + section.getPassedQuestionsCount() + " из " + section.getAllQuestionsCount());
        return  view;
    }
}
