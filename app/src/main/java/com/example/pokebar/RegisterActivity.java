package com.example.pokebar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTxtName, editTxtMail, editTxtPwd;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTxtName = findViewById(R.id.RegisterName);
        editTxtMail = findViewById(R.id.RegisterEmail);
        editTxtPwd = findViewById(R.id.RegisterPassword);

        registerButton = (Button) findViewById(R.id.BtnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

    }

    private void RegisterUser() {
        String name = editTxtName.getText().toString().trim();
        String mail = editTxtMail.getText().toString().trim();
        String pwd = editTxtPwd.getText().toString().trim();

        if (name.isEmpty() || mail.isEmpty() || pwd.isEmpty()) {
            registerButton.setError("");
            registerButton.requestFocus();
            Toast.makeText(getApplicationContext(), "Some fields are empty", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(mail, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isComplete()) {
                                User user = new User(name, mail);

                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "User is registered", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Failed to registered", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }


    public void Login(View view) {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
