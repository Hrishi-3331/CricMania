package com.hrishi_3331.devstudio3331.cricmania;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class TossActivity extends AppCompatActivity {

    private DatabaseReference jRef;
    private String game;
    private boolean isHost;
    private static final int STATUS_ONLINE = 0;
    private static final int STATUS_OFFLINE = 1;
    private TextView player1, player2, host_status, guest_status;
    private ImageView toss_choice;
    private int host_state, guest_state;
    private String player_1, player_2;
    private TextView wait_bar;
    private boolean ready;
    private FrameLayout toss_frame;
    private ImageView toss_head, toss_tail;
    private String toss_choice_st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toss);

        ViewDialog alert = new ViewDialog();
        alert.showDialog(TossActivity.this, getString(R.string.rules));

        Intent intent = getIntent();
        game = intent.getStringExtra("game");
        isHost = intent.getBooleanExtra("isHost", true);

        jRef = FirebaseDatabase.getInstance().getReference().child("Games").child(game);

        player1 = (TextView)findViewById(R.id.toss_p1);
        player2 = (TextView)findViewById(R.id.toss_p2);
        host_status = (TextView)findViewById(R.id.host_status);
        guest_status = (TextView)findViewById(R.id.guest_status);
        toss_choice = (ImageView)findViewById(R.id.guest_choice);
        wait_bar = (TextView)findViewById(R.id.wait_bar);
        toss_frame = (FrameLayout)findViewById(R.id.toss_frame);
        toss_head = (ImageView)findViewById(R.id.frame_heads);
        toss_tail = (ImageView)findViewById(R.id.frame_tails);
        ready = false;

        toss_frame.setVisibility(View.GONE);

        jRef.child("toss").child("status").setValue("pending");
        jRef.child("toss").child("res").setValue("pending");
        jRef.child("toss").child("winner").setValue("pending");

        if (isHost){
            DatabaseReference hRef = jRef.child("player1").child("status");
            hRef.setValue(STATUS_ONLINE);
            hRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    host_state = Integer.valueOf(dataSnapshot.getValue().toString());
                    switch (host_state){
                        case 0:
                            host_status.setText(":  online");
                            break;

                        case 1:
                            host_status.setText(":  offline");
                            break;

                        default:
                            host_status.setText(":  joining");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            jRef.child("player2").child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    guest_state = Integer.valueOf(dataSnapshot.getValue().toString());
                    switch (guest_state){
                        case 0:
                            guest_status.setText(":  online");
                            break;

                        case 1:
                            guest_status.setText(":  offline");
                            break;

                        default:
                            guest_status.setText(":  joining");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            hRef.onDisconnect().setValue(STATUS_OFFLINE);
        }

        else {
            DatabaseReference hRef = jRef.child("player2").child("status");
            hRef.setValue(STATUS_ONLINE);

            hRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    guest_state = Integer.valueOf(dataSnapshot.getValue().toString());
                    switch (guest_state){
                        case 0:
                            guest_status.setText(":  online");
                            break;

                        case 1:
                            guest_status.setText(":  offline");
                            break;

                        default:
                            guest_status.setText(":  joining");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            jRef.child("player1").child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    host_state = Integer.valueOf(dataSnapshot.getValue().toString());
                    switch (host_state){
                        case 0:
                            host_status.setText(":  online");
                            break;

                        case 1:
                            host_status.setText(":  offline");
                            break;

                        default:
                            host_status.setText(":  joining");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            hRef.onDisconnect().setValue(STATUS_OFFLINE);
        }

        jRef.child("player1").child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                player_1 = dataSnapshot.getValue().toString();
                try {
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(player_1).child("username");
                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            player1.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("player2").child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                player_2 = dataSnapshot.getValue().toString();
                try {
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(player_2).child("username");
                    mRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            player2.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkAvailability();

    }

    public void checkAvailability(){

        if (isHost){
            if (guest_status.getText().toString() == ":  online"){
                startGamePlay();
            }
            else {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        checkAvailability();
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        }

        else {
            if (host_status.getText().toString() == ":  online"){
                startGamePlay();
            }
            else {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        checkAvailability();
                    }
                };
                handler.postDelayed(runnable, 2000);

            }
        }

    }

    public void startGamePlay(){
        wait_bar.setVisibility(View.GONE);
        jRef.child("toss").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue().toString().trim();
                if (status != "pending"){
                    if (status == "heads"){
                        toss_choice_st = "heads";
                        toss_choice.setImageResource(R.drawable.heads);
                    }
                    else {
                        toss_choice_st = "tails";
                        toss_choice.setImageResource(R.drawable.tails);
                    }
                    startToss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("toss").child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String res = dataSnapshot.getValue().toString();

                if (res != "pending"){
                    if(res == toss_choice_st){
                        jRef.child("toss").child("winner").setValue("player2");
                    }
                    else {
                        jRef.child("toss").child("winner").setValue("player1");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("toss").child("winner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String winner = dataSnapshot.getValue().toString();
                if (winner != "pending"){
                    if (winner == "player1"){
                        if (isHost){
                            Toast.makeText(TossActivity.this, "Your opponent lost the toss", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(TossActivity.this, "You lost the toss", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        if (isHost){
                            Toast.makeText(TossActivity.this, "Your opponent won the toss", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(TossActivity.this, "You won the toss", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (isHost){

        }
        else {
           chooseToss();
        }
    }

    public void startToss() {
        toss_frame.setVisibility(View.VISIBLE);
        toss_anim();
    }

    public void toss_anim() {

            toss_head.setAlpha(0f);
            toss_tail.setAlpha(0f);

            toss_head.animate().alpha(1f).setDuration(1500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    toss_head.animate().alpha(0f).setDuration(1500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            toss_tail.animate().alpha(1f).setDuration(1500).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    toss_tail.animate().alpha(0f).setDuration(1500).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            toss_head.animate().alpha(1f).setDuration(1500).withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    toss_head.animate().alpha(0f).setDuration(1500).withEndAction(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            toss_tail.animate().alpha(1f).setDuration(1500).withEndAction(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    toss_tail.animate().alpha(1f).setDuration(1500).withEndAction(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            toss_tail.animate().alpha(0f).setDuration(1500).withEndAction(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if(!isHost){
                                                                                        generateTossResult();
                                                                                    }

                                                                                }
                                                                            }).start();
                                                                        }
                                                                    }).start();
                                                                }
                                                            }).start();
                                                        }
                                                    }).start();
                                                }
                                            }).start();
                                        }
                                    }).start();
                                }
                            }).start();
                        }
                    }).start();
                }
            }).start();

    }

    public void generateTossResult(){
        toss_head.setAlpha(0f);
        toss_tail.setAlpha(0f);

        Random random = new Random();
        boolean val = random.nextBoolean();
        if (val){
            toss_head.animate().alpha(1f).setDuration(1500).start();
            jRef.child("toss").child("res").setValue("heads");
        }
        else {
            toss_head.animate().alpha(1f).setDuration(1500).start();
            jRef.child("toss").child("res").setValue("tails");
        }
    }


    public void chooseToss(){
        if (ready){
            TossDialog dialog = new TossDialog();
            dialog.showDialog(TossActivity.this);
        }
        else {
            Handler handler = new Handler();
            Runnable runnable= new Runnable() {
                @Override
                public void run() {
                    chooseToss();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    public class ViewDialog {

        public void showDialog(Activity activity, String msg){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ready = true;
                }
            });

            dialog.show();

        }
    }

    public class TossDialog {

        public void showDialog(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.toss_dialog);

            ImageButton heads = dialog.findViewById(R.id.heads);
            ImageButton tails = dialog.findViewById(R.id.tails);

            heads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jRef.child("toss").child("status").setValue("heads");
                    dialog.dismiss();
                }
            });

            tails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jRef.child("toss").child("status").setValue("tails");
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }
}
