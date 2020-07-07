package com.example.tinderclonetest1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {

        }
    }

    public void fb(View view) {
        Intent intent = new Intent(this,LoginNumActivity.class);
        startActivity(intent);

    }

    public void login(View view) {
        Intent intent = new Intent(this,LoginNumActivity.class);
        intent.putExtra("login","login");
        startActivity(intent);
    }
}
