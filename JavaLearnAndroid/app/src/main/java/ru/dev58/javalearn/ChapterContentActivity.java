package ru.dev58.javalearn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.ListView;

import controllers.ChaptersController;
import controllers.SectionsController;
import models.Chapters;
import models.Sections;

/**
 * Created by Дмитрий on 09.12.2016.
 */

public class ChapterContentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters_content);

        Intent intent = getIntent();
        String chapterId = intent.getStringExtra("_chapter_id");
        if(chapterId.isEmpty()) {
            return;
        }
        Chapters chapter = ChaptersController.instance().getById(chapterId);
        if(chapter == null) return;
        Sections section = SectionsController.instance().getById(chapter.getId_section());
        setTitle("Глава " + section.getRoworder() + "." + chapter.getRoworder() + ". " + chapter.getTitle());
        ((WebView)findViewById(R.id.chapter_content)).loadData(chapter.getContent(), "text/html; charset=utf-8", "utf-8");
    }
}
