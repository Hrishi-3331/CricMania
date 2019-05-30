package com.hrishi_3331.devstudio3331.cricmania;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HostWait extends AppCompatActivity {

    private String match_id;
    private String table_id;
    private DatabaseReference jRef;
    private String CR;
    private String Players;
    private String guest_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_wait);

        final Intent intent = getIntent();
        table_id = intent.getStringExtra("table_id");
        match_id = intent.getStringExtra("match_id");

        jRef = FirebaseDatabase.getInstance().getReference().child("Matches").child(match_id).child("Tables").child(table_id);

        jRef.child("CR").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CR = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("Players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Players = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int state = Integer.valueOf(dataSnapshot.getValue().toString());
                if (state == 1){
                    getGuestId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getGuestId(){
        jRef.child("guest_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    guest_id = dataSnapshot.getValue().toString();
                    Intent intent1 = new Intent(HostWait.this, NewGame.class);
                    intent1.putExtra("table_id", table_id);
                    intent1.putExtra("match_id", match_id);
                    intent1.putExtra("isHost", true);
                    intent1.putExtra("settings", CR + "x" + Players);
                    intent1.putExtra("other", guest_id);
                    startActivity(intent1);
                }
                else {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            getGuestId();
                        }
                    };
                    handler.postDelayed(runnable, 1500);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
