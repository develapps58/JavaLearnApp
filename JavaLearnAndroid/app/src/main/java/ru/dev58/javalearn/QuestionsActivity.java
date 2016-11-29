package ru.dev58.javalearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import adapters.QuestionsListAdapter;
import controllers.QuestionsController;
import controllers.SectionsController;
import models.Sections;

/**
 * Created by Дмитрий on 23.11.2016.
 */

public class QuestionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Intent intent = getIntent();
        String sectionId = intent.getStringExtra("_section_id");
        if (sectionId.isEmpty()) {
            return;
        }
        final Sections section = SectionsController.instance().getById(sectionId);
        if (section == null) return;
        setTitle("Вопросы раздела " + section.getRoworder() + ". " + section.getTitle());
        ((ListView) findViewById(R.id.questions_list))
                .setAdapter(new QuestionsListAdapter(this, QuestionsController.instance().getBySectionId(sectionId)));
    }
}
