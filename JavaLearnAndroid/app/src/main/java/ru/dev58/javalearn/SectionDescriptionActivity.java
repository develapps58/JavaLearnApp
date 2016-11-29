package ru.dev58.javalearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import controllers.SectionsController;
import models.Sections;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public class SectionDescriptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_description);

        Intent intent = getIntent();
        String sectionId = intent.getStringExtra("_section_id");
        if(sectionId.isEmpty()) {
            return;
        }
        Sections section = SectionsController.instance().getById(sectionId);
        setTitle("Раздел " + section.getRoworder() + ". " + section.getTitle());
        ((WebView)findViewById(R.id.section_description)).loadData(section.getDescription(), "text/html; charset=utf-8", "utf-8");
    }
}
