package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import models.Chapters;
import models.Sections;
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
        return chapters.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.chapters_layout, viewGroup, false);
        }
        final Chapters chapter = (Chapters) getItem(i);
        ((TextView)view.findViewById(R.id.chapters_title)).setText(chapter.getTitle());
        return view;
    }
}
