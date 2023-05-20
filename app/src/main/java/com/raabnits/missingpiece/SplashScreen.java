package com.raabnits.missingpiece;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Random;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";
    public static String referer_id="";
    private String[] welcome_message={
            "Welcome!",
            "Nice to see u!",
            "Ohh! Great",
            "Are you Ready?",
            "Welcome!",
            "Greetings for u",
            "Welcome!",
            "On Your Mark!",
            "Welcome",
            "Hmmm.."
    };
    private String[] next_message={
            "what are you waiting for?",
            "start finding for the missing piece",
            "you are better than 90%",
            "Have a good day",
            "Think something really big",
            "You are awesome",
            "Don't play alone! Invite your friend too.",
            "Be the first to find missing pieces",
                                    };


    TextView referer,randstr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        setContentView(R.layout.activity_splash_screen);

        /*notification code*/
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("MyNotification","MyNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "successful";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        /*notification code*/


        //referer=findViewById(R.id.referer);
        Typewriter welcm=findViewById(R.id.welcome);
        //randstr=findViewById(R.id.random_string);

        Typewriter writer = findViewById(R.id.typewriter);
        welcm.animate_search_random(welcome_message[random_in_range(0,welcome_message.length)],30,10,20,300);
        writer.animate_search_random(next_message[random_in_range(0,next_message.length)],30,10,20,300);
        welcm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            try{
                               String refLink=deepLink.toString();
                               refLink=refLink.substring(refLink.indexOf("D")+1,refLink.indexOf("&"));
                               //referer.setText(refLink);
                                referer_id=refLink;
                                finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }catch (Exception e){
                                Log.e(TAG, "onSuccess: error: "+e.toString());
                            }


                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });

    }

    private int random_in_range(int x,int y){
        Random rand = new Random();
        return x+rand.nextInt(y-x);
    }

}