package com.example.snitch.snitchapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegLogActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private EditText ETemail;
    private EditText ETpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_reg_log);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        ETemail = (EditText) findViewById(R.id.email_field);
        ETpassword = (EditText) findViewById(R.id.password_field);
        findViewById(R.id.signup_button).setOnClickListener(this);
        findViewById(R.id.signin_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.AboutActivity) {
            startActivity(new Intent(RegLogActivity.this, AboutActivity.class));
            return true;
        } else if (id == R.id.Logout) {
            Toast.makeText(RegLogActivity.this, "Вы не можете выйти из аккаунта до " +
                    "того, как авторизуетесь", Toast.LENGTH_SHORT).show();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signup_button) {
            SignUp(ETemail.getText().toString(), ETpassword.getText().toString());
        } else if (view.getId() == R.id.signin_button) {
            SignIn(ETemail.getText().toString(), ETpassword.getText().toString());
        }
    }

    public void SignUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(RegLogActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegLogActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void SignIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegLogActivity.this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(RegLogActivity.this, "Авторизация провалена", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
