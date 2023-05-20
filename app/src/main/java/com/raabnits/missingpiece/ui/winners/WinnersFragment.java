package com.raabnits.missingpiece.ui.winners;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.raabnits.missingpiece.DownloadImageTask;
import com.raabnits.missingpiece.LoginActivity;
import com.raabnits.missingpiece.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.Stack;

import javax.sql.StatementEvent;

public class WinnersFragment extends Fragment {

    private WinnersViewModel winnersViewModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private LinearLayout win_lin_lay;

    private static final String TAG = "WinnersFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        winnersViewModel =
                new ViewModelProvider(this).get(WinnersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_winners, container, false);
        win_lin_lay=root.findViewById(R.id.winners_linear_layout);


        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        //if getCurrentUser returns null
        if(firebaseAuth.getCurrentUser() == null){
            //that means user is not logged in
            //getActivity().finish();
            startActivity(new Intent(getContext(), LoginActivity.class));
        }else {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("winners");

            // Read from the database
            myRef.orderByKey().limitToLast(25).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Stack<String> s_date = new Stack<>(),s_email=new Stack<>(),s_name=new Stack<>();
                    for(DataSnapshot ds:dataSnapshot.getChildren()) {
                        String date = getDate(ds.child("date").getValue(Long.class));
                        String email = ds.child("email").getValue(String.class);
                        String name = ds.child("name").getValue(String.class);

                        s_date.push(date);
                        s_email.push(email);
                        s_name.push(name);

                        Log.d(TAG, "onDataChange: " + date + email + name);
                    }

                    if(win_lin_lay.getChildCount() > 0)
                        win_lin_lay.removeAllViews();

                    while(!s_name.empty()){

                        String date = s_date.pop();
                        String email = s_email.pop();
                        String name = s_name.pop();

                        RelativeLayout relativeLayout=new RelativeLayout(getContext());
                            RelativeLayout innerRelLayout=new RelativeLayout(getContext());
                                LinearLayout outer_ll=new LinearLayout(getContext());
                                    TextView name_tv=new TextView(getContext());
                                    LinearLayout desc_ll=new LinearLayout(getContext());
                                        TextView date_tv=new TextView(getContext());
                                        TextView email_tv=new TextView(getContext());

                        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        //relativeLayout.setBackgroundColor(Color.rgb(255,202,40));
                        relativeLayout.setPadding(1,1,1,1);

                        innerRelLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        innerRelLayout.setBackgroundColor(Color.rgb(0,0,0));
                        innerRelLayout.setPadding(30,5,30,5);


                        outer_ll.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        outer_ll.setOrientation(LinearLayout.VERTICAL);

                        name_tv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        name_tv.setBackgroundColor(Color.argb(51,255,202,40));
                        name_tv.setPadding(5,5,5,5);
                        name_tv.setTextColor(Color.rgb(255,202,40));
                        name_tv.setTextSize(20);
                        name_tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            name_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }

                        desc_ll.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        desc_ll.setOrientation(LinearLayout.HORIZONTAL);

                        date_tv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        date_tv.setPadding(5,1,5,5);
                        date_tv.setBackgroundColor(Color.argb(51,255,202,40));
                        date_tv.setTextColor(Color.rgb(255,202,40));
                        date_tv.setTextSize(10);

                        email_tv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        email_tv.setPadding(5,1,5,5);
                        email_tv.setBackgroundColor(Color.argb(25,255,202,40));
                        email_tv.setTextColor(Color.rgb(255,202,40));
                        email_tv.setTextSize(10);
                        email_tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            email_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }

                        name_tv.setText(name);
                        date_tv.setText(date);
                        email_tv.setText(alternate_asterisks(email));

                        desc_ll.addView(date_tv);
                        desc_ll.addView(email_tv);
                        outer_ll.addView(name_tv);
                        outer_ll.addView(desc_ll);
                        innerRelLayout.addView(outer_ll);
                        relativeLayout.addView(innerRelLayout);

                        win_lin_lay.addView(relativeLayout);

                    }

                    
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });





        }

//        final TextView textView = root.findViewById(R.id.text_notifications);
//        winnersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private String alternate_asterisks(String a){
        String m="";
        for(int i=0;i<a.length();i++){
            if(i%3<2) {
                m+=a.charAt(i);
            }else{
                m+="*";
            }
        }
        return m;
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}