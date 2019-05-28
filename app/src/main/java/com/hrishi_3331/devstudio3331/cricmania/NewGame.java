package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewGame extends AppCompatActivity {

    private String match_id;
    private String table_id;
    private FirebaseAuth jAuth;
    private DatabaseReference jRef;
    private FirebaseUser jUser;
    private boolean tableSet;
    private ProgressDialog jDialog;
    private boolean isHost;
    private TextView p1, p1_coins, p2, p2_coins;
    private String other_player;
    private String settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        Intent intent = getIntent();
        match_id = intent.getStringExtra("match_id");
        table_id = intent.getStringExtra("table_id");
        other_player = intent.getStringExtra("other");
        isHost = intent.getBooleanExtra("isHost", true);
        settings = intent.getStringExtra("settings");

        p1 = (TextView)findViewById(R.id.ng_p1_name);
        p2 = (TextView)findViewById(R.id.ng_p2_name);
        p1_coins = (TextView)findViewById(R.id.ng_p1_coins);
        p2_coins = (TextView)findViewById(R.id.ng_p2_coins);

        jAuth = FirebaseAuth.getInstance();
        jUser = jAuth.getCurrentUser();

        tableSet = false;
        jRef = FirebaseDatabase.getInstance().getReference().child("Matches").child(match_id).child("Tables").child(table_id);

        jDialog = new ProgressDialog(NewGame.this);
        jDialog.setTitle("Loading");
        jDialog.setMessage("Please wait..");
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(false);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(jUser.getUid());
        ref.child("usercoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String coins = dataSnapshot.getValue().toString();
                if(isHost){
                    p1_coins.setText(coins);
                }
                else {
                    p2_coins.setText(coins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString();
                if(isHost){
                    p1.setText(name);
                }
                else {
                    p2.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users").child(other_player);

        ref2.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String player_name = dataSnapshot.getValue().toString();
                if(isHost){
                    p2.setText(player_name);
                }
                else {
                    p1.setText(player_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref2.child("usercoins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String player_coins = dataSnapshot.getValue().toString();
                if(isHost){
                    p2_coins.setText(player_coins);
                }
                else {
                    p1_coins.setText(player_coins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(NewGame.this);
        builder.setTitle("Quit Game?");
        builder.setMessage("Are you sure? you will be charged 20 coins for quitting game in between!");
        builder.setCancelable(false);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(NewGame.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String createGame(String uid, String other_player, boolean isHost) {
        String GameId;

        if (isHost){
            GameId = uid + other_player;
        }
        else {
            GameId = other_player + uid;
        }

        DatabaseReference hRef = FirebaseDatabase.getInstance().getReference().child("Games").child(GameId);
        hRef.child("settings").setValue(settings);
        if (isHost){
            hRef.child("player1").child("id").setValue(uid);
            hRef.child("player2").child("id").setValue(other_player);
        }
        else {
            hRef.child("player2").child("id").setValue(uid);
            hRef.child("player1").child("id").setValue(other_player);
        }
        hRef.child("player1").child("status").setValue(3);
        hRef.child("player2").child("status").setValue(3);
        return GameId;
    }

    public void StartGame(View view){
        String Game = createGame(jUser.getUid(), other_player, isHost);
        Intent intent = new Intent(NewGame.this, TossActivity.class);
        intent.putExtra("game", Game);
        intent.putExtra("isHost", isHost);
        startActivity(intent);
        finish();
    }
}
