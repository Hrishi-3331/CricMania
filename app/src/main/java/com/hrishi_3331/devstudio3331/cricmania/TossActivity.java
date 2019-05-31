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
    private ImageView toss_head, toss_tail, p1_avtar, p2_avtar;
    private int toss_choice_st;
    private boolean isFirst;

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
        p1_avtar = (ImageView)findViewById(R.id.toss_p1_image);
        p2_avtar = (ImageView)findViewById(R.id.toss_p2_image);

        ready = false;
        isFirst = false;

        toss_frame.setVisibility(View.GONE);
        jRef.child("toss").child("status").setValue(3);
        jRef.child("toss").child("res").setValue(3);
        jRef.child("toss").child("winner").setValue(3);

        if (isHost){
            final DatabaseReference hRef = jRef.child("player1").child("status1");
            hRef.setValue(STATUS_ONLINE);
            hRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hRef.setValue(0);
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
            jRef.child("player2").child("status1").addValueEventListener(new ValueEventListener() {
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
            final DatabaseReference hRef = jRef.child("player2").child("status1");
            hRef.setValue(STATUS_ONLINE);

            hRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    guest_state = Integer.valueOf(dataSnapshot.getValue().toString());
                    hRef.setValue(0);
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

            jRef.child("player1").child("status1").addValueEventListener(new ValueEventListener() {
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

                    DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(player_1).child("useravtar");
                    mRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Avtar avtar = new Avtar();
                            p1_avtar.setImageResource(avtar.getMyAvtar(Integer.valueOf(dataSnapshot.getValue().toString())));
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

                    DatabaseReference mRef2 = FirebaseDatabase.getInstance().getReference().child("Users").child(player_2).child("useravtar");
                    mRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Avtar avtar = new Avtar();
                            p2_avtar.setImageResource(avtar.getMyAvtar(Integer.valueOf(dataSnapshot.getValue().toString())));
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



    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAvailability();
    }

    public void checkAvailability(){
        if (isHost) {
            jRef.child("player2").child("status1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int data = Integer.valueOf(dataSnapshot.getValue().toString());
                    if (data == 0){
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else {
            jRef.child("player1").child("status1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int data = Integer.valueOf(dataSnapshot.getValue().toString());
                    if (data == 0){
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public void startGamePlay(){
        wait_bar.setVisibility(View.GONE);
        jRef.child("toss").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int status = Integer.valueOf(dataSnapshot.getValue().toString());
                if (status != 3){
                    if (status == 0){
                        toss_choice_st = 0;
                        toss_choice.setImageResource(R.drawable.heads);
                    }
                    else {
                        toss_choice_st = 1;
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
                final int res = Integer.valueOf(dataSnapshot.getValue().toString());

                if (res != 3){

                    if (res == 0){
                        toss_head.animate().alpha(1f).setDuration(1500).start();
                    }
                    else {
                        toss_tail.animate().alpha(1f).setDuration(1500).start();
                    }
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if(res == toss_choice_st){
                                jRef.child("toss").child("winner").setValue(1);
                            }
                            else {
                                jRef.child("toss").child("winner").setValue(0);
                            }
                        }
                    };
                    handler.postDelayed(runnable, 2000);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("toss").child("winner").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int winner = Integer.valueOf(dataSnapshot.getValue().toString());
                if (winner != 3){
                    if (winner == 0){
                        if (isHost){
                            Toast.makeText(TossActivity.this, "Your opponent lost the toss", Toast.LENGTH_SHORT).show();
                            isFirst = true;
                        }
                        else {
                            Toast.makeText(TossActivity.this, "You lost the toss", Toast.LENGTH_SHORT).show();
                            isFirst = false;
                        }

                    }else {
                        if (isHost){
                            Toast.makeText(TossActivity.this, "Your opponent won the toss", Toast.LENGTH_SHORT).show();
                            isFirst = false;
                        }
                        else {
                            Toast.makeText(TossActivity.this, "You won the toss", Toast.LENGTH_SHORT).show();
                            isFirst = true;
                        }
                    }

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(TossActivity.this, ChoosePlayers.class);
                            intent.putExtra("game", game);
                            intent.putExtra("isHost", isHost);
                            intent.putExtra("isFirst", isFirst);
                            startActivity(intent);
                            finish();
                        }
                    };

                    handler.postDelayed(runnable, 1500);
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
            jRef.child("toss").child("res").setValue(0);
        }
        else {
            toss_tail.animate().alpha(1f).setDuration(1500).start();
            jRef.child("toss").child("res").setValue(1);
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
                    jRef.child("toss").child("status").setValue(0);
                    dialog.dismiss();
                }
            });

            tails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jRef.child("toss").child("status").setValue(1);
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }
}
