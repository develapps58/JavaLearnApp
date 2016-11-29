package ru.dev58.javalearn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import controllers.ChaptersController;
import controllers.QuestionsController;
import controllers.UserAnswersController;
import core.CurrentUser;
import core.UserOperations;
import models.UserAnswers;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Вход в программу.");

        final EditText login = (EditText)findViewById(R.id.et_auth_login);
        final EditText password = (EditText)findViewById(R.id.et_auth_password);

        ((Button)findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login.getText().toString().isEmpty()) {
                    showToast("Заполните Ваш Логин");
                    return;
                }
                if(password.getText().toString().isEmpty()) {
                    showToast("Заполните поле пароля");
                    return;
                }
                UserOperations userRegistration = new UserOperations();
                userRegistration.setLogin(login.getText().toString());
                userRegistration.setPassword(password.getText().toString());
                if(!userRegistration.login()) {
                    showToast(userRegistration.getError());
                }
                else {
                    showToast("Вы успешно вошли!");
                    UserAnswersController.instance();
                    QuestionsController.instance();
                    MainActivity.setUserInfo();
                    MainActivity.logoutMenuVisible();
                    finish();
                }
            }
        });

    }

    private void showToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 190);
        toast.show();
    }
}
