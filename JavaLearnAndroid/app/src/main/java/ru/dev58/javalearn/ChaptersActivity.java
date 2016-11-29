package ru.dev58.javalearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import adapters.ChaptersListAdapter;
import controllers.ChaptersController;
import controllers.SectionsController;
import core.CurrentUser;
import core.Settings;
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
        String sectionId = intent.getStringExtra("_section_id");
        if(sectionId.isEmpty()) {
            return;
        }
        final Sections section = SectionsController.instance().getById(sectionId);
        if(section == null) return;
        setTitle("Раздел " + section.getRoworder() + ". " + section.getTitle());
        ((ListView)findViewById(R.id.chapters_view))
                .setAdapter(new ChaptersListAdapter(this, ChaptersController.instance().getBySectionId(sectionId)));

        Button goToSectionDescription = (Button)findViewById(R.id.go_section_description);
        goToSectionDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChaptersActivity.this, SectionDescriptionActivity.class);
                intent.putExtra("_section_id", section.getId());
                startActivity(intent);
            }
        });

        Button goToTest = (Button)findViewById(R.id.go_test);
        if(CurrentUser.getCurrentUser() == null) {
            goToTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChaptersActivity.this, "Для прохождения тестов необходимо зарегистрироваться", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        if(!Settings.isOnline) {
            goToTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChaptersActivity.this, "Для прохождения тестов необходим доступ в интернет", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        if(section.getAllQuestionsCount() == 0) {
            goToTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChaptersActivity.this, "В разделе нет тестов", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        goToTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChaptersActivity.this, QuestionsActivity.class);
                intent.putExtra("_section_id", section.getId());
                ChaptersActivity.this.startActivity(intent);
            }
        });

    }
}
