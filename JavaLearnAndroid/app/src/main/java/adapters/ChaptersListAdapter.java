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

import controllers.SectionsController;
import models.Chapters;
import models.Sections;
import ru.dev58.javalearn.ChapterContentActivity;
import ru.dev58.javalearn.R;

/**
 * Created by Дмитрий on 06.11.2016.
 */


public class ChaptersListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Chapters> chapters;
    LayoutInflater inflater;

    public ChaptersListAdapter(Context context, ArrayList<Chapters> chapters) {
        this.context = context;
        Collections.sort(chapters, new Comparator<Chapters>() {
            @Override
            public int compare(Chapters chapter1, Chapters chapter2) {
                if(chapter1.getRoworder() > chapter2.getRoworder()) return 1;
                if(chapter1.getRoworder() < chapter2.getRoworder()) return -1;
                return 0;
            }
        });
        this.chapters = chapters;
        this.inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chapters.size();
    }

    @Override
    public Object getItem(int i) {
        return chapters.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.chapters_layout, viewGroup, false);
        }
        final Chapters chapter = (Chapters) getItem(i);
        TextView chapterTitle = (TextView)view.findViewById(R.id.chapters_title);
        Sections section = SectionsController.instance().getById(chapter.getId_section());

        chapterTitle.setText(section.getRoworder() + "." + chapter.getRoworder() + ". " + chapter.getTitle());
        chapterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChapterContentActivity.class);
                intent.putExtra("_chapter_id", chapter.getId());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
