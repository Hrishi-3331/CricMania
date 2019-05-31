package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HostTable extends AppCompatActivity {

    private TextView Players;
    private EditText Coins;
    private ImageButton coinsadd, coinsminus;
    private ImageButton playersadd, playersminus;
    private String match_id;
    private FirebaseAuth jAuth;
    private FirebaseUser jUser;
    private ProgressDialog jDialog;
    private CoordinatorLayout cord;
    private String table_id;
    private int coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_table);

        Players = (TextView)findViewById(R.id.players_number);
        Coins = (EditText) findViewById(R.id.coins_number);
        coinsadd = (ImageButton)findViewById(R.id.coin_plus);
        coinsminus = (ImageButton)findViewById(R.id.coin_minus);
        playersadd = (ImageButton)findViewById(R.id.players_plus);
        playersminus = (ImageButton)findViewById(R.id.players_minus);
        cord = (CoordinatorLayout)findViewById(R.id.cord1);

        jDialog = new ProgressDialog(HostTable.this);
        jDialog.setTitle("Creating Table");
        jDialog.setMessage("Please wait");
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(false);

        Intent intent = getIntent();
        match_id = intent.getStringExtra("match_id");

        jAuth = FirebaseAuth.getInstance();
        jUser = jAuth.getCurrentUser();

        FirebaseDatabase.getInstance().getReference().child("Users").child(jUser.getUid()).child("usercoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coins = Integer.valueOf(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        playersadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jag = Integer.valueOf(Players.getText().toString());
                if (jag < 6){
                    Players.setText(String.valueOf(jag + 1));
                }
            }
        });

        playersminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jag = Integer.valueOf(Players.getText().toString());
                if (jag > 1){
                    Players.setText(String.valueOf(jag - 1));
                }
            }
        });

        coinsadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jag = Integer.valueOf(Coins.getText().toString());
                Coins.setText(String.valueOf(jag + 1));

            }
        });

        coinsminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jag = Integer.valueOf(Coins.getText().toString());
                if (jag > 10){
                    Coins.setText(String.valueOf(jag - 1));
                }
            }
        });
    }

    public void host(View view){
        if (Integer.valueOf(Coins.getText().toString()) < 10){
            Toast.makeText(this, "Minimum 10 coins per run needed", Toast.LENGTH_SHORT).show();
        }
        else {
            if (CheckCriteria(Integer.valueOf(Players.getText().toString()), Integer.valueOf(Coins.getText().toString()))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HostTable.this);
                builder.setTitle("Create Table?")
                        .setMessage("Do you want to create this table?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CreateTable();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Snackbar snackbar = Snackbar.make(cord, "You don't have sufficient coins!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

    }

    public boolean CheckCriteria(int players, int cr){
        int MinimumCriteria;
        switch (players) {
            case 1:
                MinimumCriteria = cr * 200;
                break;

            case 2:
                MinimumCriteria = cr * 300;
                break;

            case 3:
                MinimumCriteria = cr * 350;
                break;

            default:
                MinimumCriteria = cr * 400;
                break;
        }

            return coins >= MinimumCriteria;
    }

    public void CreateTable(){
        jDialog.show();
        final DatabaseReference jRef = FirebaseDatabase.getInstance().getReference().child("Matches").child(match_id).child("Tables").push();
        jRef.child("CR").setValue(Integer.valueOf(Coins.getText().toString()));
        jRef.child("Players").setValue(Integer.valueOf(Players.getText().toString()));
        jRef.child("host").setValue(jUser.getDisplayName());
        jRef.child("host_id").setValue(jUser.getUid());
        jRef.child("status").setValue(0);
        jRef.child("table_id").setValue(jRef.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                jDialog.dismiss();
                if (task.isSuccessful()){
                   jRef.child("table_id").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.getValue() != null) {
                               table_id = dataSnapshot.getValue().toString();
                               Intent intent = new Intent(HostTable.this, HostWait.class);
                               intent.putExtra("match_id", match_id);
                               intent.putExtra("table_id", table_id);
                               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(intent);
                               finish();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
                }
                else {
                    Snackbar snackbar = Snackbar.make(cord, "Error in creating table. Please try again later !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }


}
