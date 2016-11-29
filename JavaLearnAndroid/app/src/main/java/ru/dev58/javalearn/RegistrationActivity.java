package ru.dev58.javalearn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import controllers.ChaptersController;
import core.CurrentUser;
import core.UserOperations;

/**
 * Created by Дмитрий on 14.11.2016.
 */

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Регистрация");

        final EditText fullName = (EditText)findViewById(R.id.et_full_name);
        final EditText login = (EditText)findViewById(R.id.et_login);
        final EditText password = (EditText)findViewById(R.id.et_password);
        final EditText repeatPassword = (EditText)findViewById(R.id.et_repeat_password);

        ((Button)findViewById(R.id.btn_registration)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fullName.getText().toString().isEmpty()) {
                    showToast("Заполните Ваше Имя");
                    return;
                }
                if(login.getText().toString().isEmpty()) {
                    showToast("Заполните Ваш Логин");
                    return;
                }
                if(password.getText().toString().isEmpty()) {
                    showToast("Заполните поле пароля");
                    return;
                }
                if(repeatPassword.getText().toString().isEmpty()) {
                    showToast("Повторите пароль");
                    return;
                }
                if(!password.getText().toString().equals(repeatPassword.getText().toString())) {
                    showToast("Пароли не совпадают");
                    return;
                }
                UserOperations userRegistration = new UserOperations();
                userRegistration.setFull_name(fullName.getText().toString());
                userRegistration.setLogin(login.getText().toString());
                userRegistration.setPassword(password.getText().toString());
                if(!userRegistration.registration()) {
                    showToast(userRegistration.getError());
                }
                else {
                    showToast("Вы успешно зарегистрировались!");
                    CurrentUser.setCurrentUser(login.getText().toString(), password.getText().toString()).setFullname(fullName.getText().toString());
                    ChaptersController.instance();
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