package com.raabnits.missingpiece.ui.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raabnits.missingpiece.LoginActivity;
import com.raabnits.missingpiece.MainActivity;
import com.raabnits.missingpiece.R;
import com.raabnits.missingpiece.models.Users;

import java.util.Random;

public class ProfileFragment extends Fragment {

    private ProfileViewModel dashboardViewModel;

    private TextView share_btn,watch_video_btn,upi_btn,paypal_btn;
    private TextView name,email,phoneno,life_count;
    private EditText upi,paypal;

    Users userdata;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference currUserRef;

    private Users user_data;

    private RewardedAd rewardedAd;
    private InterstitialAd mInterstitialAd;

    private static final String TAG = "ProfileFragment";
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()== null){
            startActivity(new Intent(getContext(), LoginActivity.class));
        }else {
            //ads starts
            video_ad_initialize();
            video_ad_create_obj();
            video_ad_load();
            load_intr();

            //ads end

            share_btn = root.findViewById(R.id.share_button);
            watch_video_btn=root.findViewById(R.id.watch_video_btn);
            name = root.findViewById(R.id.name);
            email = root.findViewById(R.id.email);
            phoneno= root.findViewById(R.id.phoneno);
            life_count = root.findViewById(R.id.life_count);
            upi = root.findViewById(R.id.upi);
            paypal = root.findViewById(R.id.paypal);

            upi_btn = root.findViewById(R.id.upi_button);
            paypal_btn = root.findViewById(R.id.paypal_button);




            database = FirebaseDatabase.getInstance();
            currUserRef = database.getReference("user").child(firebaseAuth.getCurrentUser().getUid());

            data_load();

            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShareClicked();
                }
            });
            watch_video_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int rand_val=random_in_range(0,10);
                    if(rand_val>4) {
                        video_ad_show();
                    }else{
                        show_intr();
                    }
                }
            });

            upi_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save_upi();
                }
            });
            paypal_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save_paypal();
                }
            });

        }
        return root;
    }

    public void openDialog(String title, String text) {
        TextView dialog_title,dialog_text;

        final Dialog dialog = new Dialog(getContext()); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_layout);
        dialog_title=dialog.findViewById(R.id.dialog_title);
        dialog_text=dialog.findViewById(R.id.dialog_info);
        dialog_title.setText(title);
        dialog_text.setText(text);

        dialog.setTitle(title);
        dialog.show();

    }

    public void openGoldenDialog(String title, String text) {
        TextView dialog_title,dialog_text;

        final Dialog dialog = new Dialog(getContext()); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_golden_layout);
        dialog_title=dialog.findViewById(R.id.dialog_title);
        dialog_text=dialog.findViewById(R.id.dialog_info);
        dialog_title.setText(title);
        dialog_text.setText(text);

        dialog.setTitle(title);
        dialog.show();
    }
    public void openRewardDialog(String title, String text) {
        TextView dialog_title,dialog_text;

        final Dialog dialog = new Dialog(getContext()); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_reward_layout);
        dialog_title=dialog.findViewById(R.id.dialog_title);
        dialog_text=dialog.findViewById(R.id.dialog_info);
        dialog_title.setText(title);
        dialog_text.setText(text);

        dialog.setTitle(title);
        dialog.show();
    }
    public void openWarnDialog(String title, String text) {
        TextView dialog_title,dialog_text;

        final Dialog dialog = new Dialog(getContext()); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_warning_layout);
        dialog_title=dialog.findViewById(R.id.dialog_title);
        dialog_text=dialog.findViewById(R.id.dialog_info);
        dialog_title.setText(title);
        dialog_text.setText(text);

        dialog.setTitle(title);
        dialog.show();
    }

    private void video_ad_initialize(){
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }
    private void video_ad_create_obj(){
        //create reward object ad id: ca-app-pub-1895846891909813/4589755193 test ad id: ca-app-pub-3940256099942544/5224354917
        rewardedAd = new RewardedAd(getContext(), "ca-app-pub-1895846891909813/4589755193");
        mInterstitialAd = new InterstitialAd(getContext());
        //myid: ca-app-pub-1895846891909813/1095225130 testid:ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.setAdUnitId("ca-app-pub-1895846891909813/1095225130");
    }
    private void video_ad_load(){
        //load ad callback
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.

                //Toast.makeText(getContext(),"ad loaded successfully! ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }
    private void load_intr(){
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                give_reward_range(1,5);
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                give_reward_range(10,20);
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                //show_intr();

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                load_intr();
            }
        });
    }
    private void show_intr(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            openGoldenDialog("Ad Loading!","Ad is being loaded! Please try again later");
        }
    }
    private void video_ad_show(){
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    video_ad_create_obj();
                    video_ad_load();
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward
                    give_reward_range(3,10);

                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    // Ad failed to display.
                    openWarnDialog("Err","Unable to display the video!");

                }
            };
            rewardedAd.show(getActivity(), adCallback);
        } else {
            //Log.d("TAG", "The rewarded ad wasn't loaded yet.");
            openGoldenDialog("Ad Loading","Ad is being loaded. please try again after sometime");
//            video_ad_create_obj();
//            video_ad_load();
        }

    }
    private void give_reward_range(int x,int y){
        int reward_amount=random_in_range(x,y);
        //saving to user
        ValueEventListener user_dataLoad = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userdata = dataSnapshot.getValue(Users.class);
                currUserRef.child("life").setValue(userdata.getLife()+reward_amount);
                openRewardDialog("+"+String.valueOf(reward_amount),"Congratulations "+userdata.getName()+"!\n You got "+String.valueOf(reward_amount)+" blank pieces.");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

                // ...
            }
        };

        currUserRef.addListenerForSingleValueEvent(user_dataLoad);
    }

    private int random_in_range(int x,int y){
        Random rand = new Random();
        return x+rand.nextInt(y-x);
    }

    private void save_upi(){
        if(upi.getText()!=null)
        currUserRef.child("upi").setValue(upi.getText().toString());
    }
    private void save_paypal(){
        if(paypal.getText()!=null)
        currUserRef.child("pay").setValue(paypal.getText().toString());
    }

    private void data_load(){
        ValueEventListener dataLoad = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userdata = dataSnapshot.getValue(Users.class);
                //
                name.setText(userdata.getName());
                email.setText(userdata.getEmail());
                phoneno.setText(userdata.getPhone());

                upi.setText(userdata.getUpi());
                paypal.setText(userdata.getPay());

                life_count.setText(String.valueOf(userdata.getLife()));



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        currUserRef.addValueEventListener(dataLoad);

    }

    private void onShareClicked() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Users post = dataSnapshot.getValue(Users.class);
                // ...
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Can you help me with finding the missing piece? Download the Missing Piece App and Signup using the link below. \n"+post.getRef());//+"\n Download: https://play.google.com/store/apps/details?id=com.raabnits.missingpiece");
                startActivity(Intent.createChooser(intent, "Share Link"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        currUserRef.addValueEventListener(postListener);


    }

}