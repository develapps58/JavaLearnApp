package ru.dev58.javalearn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import adapters.ChaptersListAdapter;
import controllers.ChaptersController;
import controllers.SectionsController;
import models.Sections;

/**
 * Created by Дмитрий on 06.11.2016.
 */

public class ChaptersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        Intent intent = getIntent();
        int sectionId = intent.getIntExtra("_section_id", 0);
        if(sectionId == 0) {
            return;
        }
        Sections section = SectionsController.instance().getById(sectionId);
        setTitle(section.getTitle());
        ((TextView)findViewById(R.id.section_title)).setText(section.getDescription());
        ((ListView)findViewById(R.id.chapters_view))
                .setAdapter(new ChaptersListAdapter(this, ChaptersController.instance().getBySectionId(sectionId)));
    }
}
