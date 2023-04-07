package com.example.pokebar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText LoginMail, LoginPwd;
    private Button LoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        LoginMail = findViewById(R.id.LoginMail);
        LoginPwd = findViewById(R.id.LoginPwd);

        LoginBtn = (Button) findViewById(R.id.BtnLogin);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { LogInUser(); }
        });
    }

    private void LogInUser() {
        String email = LoginMail.getText().toString().trim();
        String password = LoginPwd.getText().toString().trim();

        if(email.isEmpty()){
            LoginMail.setError("Email is required!");
            LoginMail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            LoginMail.setError("Please enter a valid email!");
            LoginMail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            LoginPwd.setError("Password is required!");
            LoginPwd.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Failed to login!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Register(View view){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {

    }
}