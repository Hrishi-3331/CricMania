package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Authentication extends AppCompatActivity {

    private FirebaseAuth jAuth;
    private EditText user_email;
    private EditText user_password;
    private CoordinatorLayout cord;
    private ProgressDialog jDialog;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        cord = (CoordinatorLayout)findViewById(R.id.cord);

        Toolbar toolbar = (Toolbar)findViewById(R.id.auth_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login User");

        user_email = (EditText)findViewById(R.id.user_email);
        user_password = (EditText)findViewById(R.id.user_password);

        jDialog = new ProgressDialog(Authentication.this);
        jDialog.setTitle("Signing In");
        jDialog.setMessage("Please wait...");
        jDialog.setCancelable(false);
        jDialog.setCanceledOnTouchOutside(false);

        jAuth = FirebaseAuth.getInstance();

    }

    public void Login(View view){
        String Email = user_email.getText().toString();
        String Password = user_password.getText().toString();

        if (Email.isEmpty()){
            Snackbar snackbar = Snackbar.make(cord, "Please enter Email ID", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if (Password.isEmpty()){
            Snackbar snackbar = Snackbar.make(cord, "Please enter Password", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else {
            jDialog.show();
            jAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    jDialog.dismiss();
                    if (task.isSuccessful()){
                        mUser = jAuth.getCurrentUser();
                        Intent intent = new Intent(Authentication.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(cord, "Invalid credentials. Please try again!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });
        }
    }
}
