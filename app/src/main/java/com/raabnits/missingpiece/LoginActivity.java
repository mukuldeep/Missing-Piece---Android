package com.raabnits.missingpiece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private TextView signup_text;
    private Button login_btn;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.login_email);
        signup_text=findViewById(R.id.signup_text);
        password=findViewById(R.id.login_password);
        login_btn=findViewById(R.id.login_button);
        progressBar=findViewById(R.id.progressBar);


        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userLogin();
                }
            });

            signup_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                }
            });

        }
    }

    //method for user login
    private void userLogin(){
        String s_email = email.getText().toString().trim();
        String s_password  = password.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(s_email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(s_password)){
            Toast.makeText(this,"Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressBar.setVisibility(View.VISIBLE);

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(s_email, s_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //if the task is successfull
                        if(task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            openWarnDialog("Login Failed!","Something  went wrong\n1. please check your credentials\n2.if you doesn't have account with us, create an account first\n3. Check your internet connection ");
                        }
                    }
                });

    }
    public void openWarnDialog(String title, String text) {
        TextView dialog_title,dialog_text;

        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_warning_layout);
        dialog_title=dialog.findViewById(R.id.dialog_title);
        dialog_text=dialog.findViewById(R.id.dialog_info);
        dialog_title.setText(title);
        dialog_text.setText(text);

        dialog.setTitle(title);
        dialog.show();
    }

}