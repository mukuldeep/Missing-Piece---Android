package com.raabnits.missingpiece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class SignupActivity extends AppCompatActivity {

    private EditText name,email,phone_no,password;
    private Button signup_btn;
    private ProgressBar progressBar;
    private TextView login_text;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    String s_name;
    String s_email;
    String s_phone_no;
    String s_password;

    private static final String TAG = "SignupActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        name=findViewById(R.id.signup_name);
        email=findViewById(R.id.signup_email);
        phone_no=findViewById(R.id.signup_phone);
        password=findViewById(R.id.signup_password);
        signup_btn=findViewById(R.id.signup_button);
        progressBar=findViewById(R.id.progressBar);
        login_text=findViewById(R.id.login_text);

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            signup_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                }
            });

            login_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });
        }

    }

    private void save_to_db(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user").child(firebaseAuth.getCurrentUser().getUid());
        myRef.child("name").setValue(s_name);
        myRef.child("email").setValue(s_email);
        myRef.child("pass").setValue(s_password);
        myRef.child("phone").setValue(s_phone_no);

        myRef.child("life").setValue(18);
        myRef.child("referer").setValue("");
        myRef.child("ref").setValue("");
        myRef.child("pay").setValue("");
        myRef.child("upi").setValue("");
        Log.d(TAG, "save_to_db: saved");
        refferal_link_generator();
    }

    private void refferal_link_generator(){
        Log.d(TAG, "refferal_link_generator: referral link generating");
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.raabnits.com/"))
                .setDomainUriPrefix("https://missingpiece.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        String shareLinkManual="https://missingpiece.page.link/?"
                +"link=https://play.google.com/store/apps/details?id=com.raabnits.missingpiece"//https://www.raabnits.com/?refid="+firebaseAuth.getCurrentUser().getUid()
                +"&apn="+getPackageName()
                +"at=My refer link"
                +"sd=we both will be rewarded with blank pieces"
                +"si=https://raabnits.com/wp-content/uploads/2020/12/missing_piece.png";


        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLink(dynamicLinkUri)
                .setLink(Uri.parse(shareLinkManual))
                .setDomainUriPrefix("https://missingpiece.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.d(TAG, "onSuccess: saving referral link to db");
                            myRef.child("ref").setValue(shortLink.toString());
                            // ...
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            // Error
                            openWarnDialog("Some unexpected error occured","please contact us at mukul@raabnits.com");

                            // ...
                        }
                    }
                });


        /*
        final Uri[] mInvitationUrl = new Uri[1];
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String link = "https://raabnits.com/" + uid;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://missingpiece.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.raabnits.missingpiece")
                                .setMinimumVersion(125)
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl[0] = shortDynamicLink.getShortLink();
                        // ...
                        //save to database
                        Log.d(TAG, "onSuccess: saving referral link to db");
                        myRef.child("ref").setValue(mInvitationUrl[0].toString());
                        // ...
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });

         */
    }
    private void registerUser(){

        //getting email and password from edit texts
         s_name=name.getText().toString().trim();
         s_email = email.getText().toString().trim();
         s_phone_no = phone_no.getText().toString().trim();
         s_password  = password.getText().toString().trim();

        //Log.d(TAG,s_name+s_email+s_password+s_phone_no);

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(s_email)){
            Toast.makeText(SignupActivity.this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(s_name)){
            Toast.makeText(SignupActivity.this,"Please enter name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(s_phone_no)){
            Toast.makeText(SignupActivity.this,"Please enter phone no",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(s_password)){
            Toast.makeText(SignupActivity.this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(s_email, s_password)
                .addOnCompleteListener(this, task -> {

                    //checking if success
                    if(task.isSuccessful()){
                        save_to_db();
                    }else{
                        //display some message here
                        //Toast.makeText(SignupActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        openWarnDialog("Signup Failed!","Something  went wrong\n1. please verify the information you provided \n2. Use a password of length more than 6 characters \n3. Login, iff you already have an account\n4. Check your internet connection ");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
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