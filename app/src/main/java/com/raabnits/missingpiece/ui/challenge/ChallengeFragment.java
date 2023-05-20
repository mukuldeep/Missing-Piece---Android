package com.raabnits.missingpiece.ui.challenge;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.raabnits.missingpiece.DownloadImageTask;
import com.raabnits.missingpiece.LoginActivity;
import com.raabnits.missingpiece.MainActivity;
import com.raabnits.missingpiece.R;
import com.raabnits.missingpiece.SplashScreen;
import com.raabnits.missingpiece.models.Users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.Executor;

public class ChallengeFragment extends Fragment {

    private ChallengeViewModel challengeViewModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference challengesdb,usersdb,winnersdb,constantsdb,challengeref;
    private int height;
    Users userdata,refererdata;


    private String key="";

    private ImageView ch_img;
    private TextView ch_textview;
    private TextView submit_btn;
    private EditText challenge_edit;
    String ch_no="";

    private static final String TAG = "ChallengeFragment";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        challengeViewModel =
                new ViewModelProvider(this).get(ChallengeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_challenge, container, false);

        ch_img=root.findViewById(R.id.imageView);
        ch_textview=root.findViewById(R.id.challenge_text);
        submit_btn=root.findViewById(R.id.submit_challenge);
        challenge_edit=root.findViewById(R.id.challenge_edit);

        submit_btn.setEnabled(false);
        challenge_edit.setEnabled(false);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        //if getCurrentUser returns null
        if(firebaseAuth.getCurrentUser() == null){
            //that means user is not logged in
            getActivity().finish();
            startActivity(new Intent(getContext(), LoginActivity.class));
        }else {

            database = FirebaseDatabase.getInstance();
            if(database==null) Log.d(TAG, "onCreateView: null in firebase database instances");
            //**************************null obj ref database
            challengesdb = database.getReference("challenges");
            usersdb = database.getReference("user").child(firebaseAuth.getCurrentUser().getUid());
            winnersdb = database.getReference("winners");
            constantsdb = database.getReference("constants");

            data_load();
            // Read from the database
            constantsdb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.child("challenges_height").getValue(String.class);
                    ch_no=value;
                    //Log.d(TAG, "Value is: " + value);

                    height=Integer.parseInt(value);
                    challengeref=challengesdb.child(value);

                    // Read from the database
                    challengeref.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             String url=dataSnapshot.child("img").getValue(String.class);
                             String ch_text=dataSnapshot.child("text").getValue(String.class);
                             key=dataSnapshot.child("key").getValue(String.class);

                             new DownloadImageTask(ch_img).execute(url);
                             ch_textview.setText(ch_text);
                             if(dataSnapshot.child("solved").getValue(Integer.class)==0){
                                 submit_btn.setEnabled(true);
                                 challenge_edit.setEnabled(true);
                             }else{
                                 submit_btn.setText("Already solved!");
                                 challenge_edit.setHint("Stay tuned for the next Challenge");
                             }

                         }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
//

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    usersdb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int no_lifes = dataSnapshot.child("life").getValue(Integer.class);

                            if(no_lifes<3) {
                                openWarnDialog("Insufficient blank pieces available!","You must have atleast 3 blank pieces. \n you might get blank pieces by\n1. Sharing this App\n 2. Watching Ads! ");
                            }else{
                            String challenge_ans = challenge_edit.getText().toString();

                            String challenge_ans_hash = "";
                            try {
                                challenge_ans_hash = SHA256(challenge_ans);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            //Log.d(TAG, "onClick: ch_ans_hash:" + challenge_ans_hash + " " + challenge_ans);
                            //Log.d(TAG, "onClick: key:" + key);
                            if (key == null || challenge_ans_hash==null) {
                                Toast.makeText(getContext(), "something went wrong! please try again after some time!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (challenge_ans_hash.equals(key)) {
                                    //Toast.makeText(getContext(), "YEH! you cracked it", Toast.LENGTH_SHORT).show();

                                    challengeref.child("winner").setValue(firebaseAuth.getCurrentUser().getUid());
                                    challengeref.child("solved").setValue(1);

                                    DatabaseReference winnerlist = winnersdb.child(String.valueOf(height));

                                    Long tsLong = System.currentTimeMillis() / 1000;
                                    winnerlist.child("date").setValue(tsLong);

                                    usersdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String user_email = dataSnapshot.child("email").getValue(String.class);
                                            String user_name = dataSnapshot.child("name").getValue(String.class);
                                            winnerlist.child("email").setValue(user_email);
                                            winnerlist.child("name").setValue(user_name);
                                            usersdb.child("life").setValue(no_lifes - 3);

                                            submit_btn.setEnabled(false);
                                            challenge_edit.setEnabled(false);
                                            openGoldenDialog("Oh yeah!", "Congratulations!\n\nyou found the missing piece!");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value
                                            Log.w(TAG, "Failed to read value.", error.toException());
                                        }
                                    });

                                    winnerlist.child("uid").setValue(firebaseAuth.getCurrentUser().getUid());
                                } else {
                                    //Toast.makeText(getContext(), "No!", Toast.LENGTH_SHORT).show();

                                    usersdb.child("life").setValue(no_lifes - 1);
                                    openDialog("Try Harder next time", "Nope! The piece you sent isn't correct!\n\n" +
                                            "Remember: The Missing Piece is Case Sensitive, \n might consist of english alphabet letters, numbers, spaces & symbols");
                                }

                            }
                            //////sdjhfbsjdhbf
                        }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                }
            });



//            final TextView textView = root.findViewById(R.id.challenge_text);
//            challengeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//                @Override
//                public void onChanged(@Nullable String s) {
//                    textView.setText(s);
//                }
//            });
        }
        return root;
    }
    private void data_load(){
        ValueEventListener dataLoad = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userdata = dataSnapshot.getValue(Users.class);
                assert userdata != null;
                if("".equals(userdata.getReferer())){
                    put_referer();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        usersdb.addValueEventListener(dataLoad);

    }
    void put_referer(){
        int ref_amount=random_in_range(9,15);
        String referer = SplashScreen.referer_id;
        //Log.d(TAG, "put_referer: referer:"+referer);
        if(referer!="" && referer!=firebaseAuth.getCurrentUser().getUid()){
            //#########################
            usersdb.child("referer").setValue(referer);
            usersdb.child("life").setValue(userdata.getLife()+ref_amount);
            add_life_to_referer(referer,ref_amount);
            //openGoldenDialog("Congratulations!","You & your friend get 10 blank pieces. Now you can utilize them while submitting challenges!");
        }
    }
    public void add_life_to_referer(String referer,int ref_amount){

        DatabaseReference ref_usersdb;
        ref_usersdb=database.getReference("user").child(referer);

        ValueEventListener ref_user_dataLoad = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refererdata = dataSnapshot.getValue(Users.class);
                ref_usersdb.child("life").setValue(refererdata.getLife()+10);
                openDialog("+"+String.valueOf(ref_amount),refererdata.getName()+" referred you Sucessfully!\nBoth of You got +"+String.valueOf(ref_amount)+" blank pieces.  Now you can utilize them while submitting challenges!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        ref_usersdb.addListenerForSingleValueEvent(ref_user_dataLoad);
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

    public static String SHA256 (String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());
        byte[] digest = md.digest();
        //return Base64.encodeToString(digest, Base64.DEFAULT);
        return  bytesToHex(digest);
    }
    public static String bytesToHex(byte[] bytes) {
        final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    private int random_in_range(int x,int y){
        Random rand = new Random();
        return x+rand.nextInt(y-x);
    }
}