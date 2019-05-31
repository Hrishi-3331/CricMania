package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChoosePlayers extends AppCompatActivity {

    private RecyclerView mPlayers;
    private RecyclerView.LayoutManager manager;
    private DatabaseReference jRef;
    private static DatabaseReference hRef;
    private String game;
    private boolean isFirst;
    private static boolean isHost;
    private int status;
    private String settings;
    private int limit;
    private TextView noticeboard, you_count, opp_count;
    private int YOUR_TURN;
    private static int OTHER_TURN;
    public static boolean yourChance;
    private static int val;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog jDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);

        mPlayers = (RecyclerView)findViewById(R.id.players);
        noticeboard = (TextView)findViewById(R.id.notice_board);
        you_count = (TextView)findViewById(R.id.you_count);
        opp_count = (TextView)findViewById(R.id.opp_count);
        yourChance = false;

        Intent intent = getIntent();
        game = intent.getStringExtra("game");
        isFirst = intent.getBooleanExtra("isFirst", false);
        isHost = intent.getBooleanExtra("isHost", false);

        hRef = FirebaseDatabase.getInstance().getReference().child("Games").child(game);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        jDialog = new ProgressDialog(ChoosePlayers.this);
        jDialog.setTitle("Submitting Game");
        jDialog.setMessage("Please wait..");
        jDialog.setCancelable(false);
        jDialog.setCanceledOnTouchOutside(false);

        noticeboard.setText("waiting for player to come online");

        if (isHost){
            YOUR_TURN = 0;
            OTHER_TURN = 1;
        }
        else {
            YOUR_TURN = 1;
            OTHER_TURN = 0;
        }

        if (isHost) {
            hRef.child("player1").child("status").setValue(0);
            hRef.child("player1").child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hRef.child("player1").child("status").setValue(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            hRef.child("player2").child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    status = Integer.valueOf(dataSnapshot.getValue().toString());
                    if (status == 0){
                        startGame();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            hRef.child("player2").child("status").setValue(0);
            hRef.child("player2").child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    hRef.child("player2").child("status").setValue(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            hRef.child("player1").child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    status = Integer.valueOf(dataSnapshot.getValue().toString());
                    if (status == 0){
                        startGame();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



        hRef.child("settings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                settings = dataSnapshot.getValue().toString();
                try {
                    limit = Integer.valueOf(settings.substring(settings.length() - 2));
                }
                catch (Exception e){
                    limit = Integer.valueOf(settings.substring(settings.length() - 1));
                }
                Toast.makeText(ChoosePlayers.this, "Limit is " + limit, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (isHost){
            hRef.child("player1").child("selected_players").setValue(0);
            hRef.child("player1").child("selected_players").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        val = Integer.valueOf(dataSnapshot.getValue().toString());
                        you_count.setText(val + "/" + limit);

                        if (val == limit && val != 0) {
                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    SubmitGame();
                                }
                            };
                            handler.postDelayed(runnable, 1500);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else {
            hRef.child("player2").child("selected_players").setValue(0);
            hRef.child("player2").child("selected_players").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        val = Integer.valueOf(dataSnapshot.getValue().toString());
                        you_count.setText(val + "/" + limit);

                        if (val == limit && val != 0) {
                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    SubmitGame();
                                }
                            };
                            handler.postDelayed(runnable, 1500);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        WaitForOpponentCount();

        manager = new GridLayoutManager(ChoosePlayers.this, 3);
        mPlayers.setLayoutManager(manager);
    }

    private void SubmitGame() {
        jDialog.show();
        DatabaseReference aRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        aRef.child("History").push().child("game_id").setValue(game);
        aRef.child("available").setValue(1);
        aRef.child("last_active_game").setValue(game);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ChoosePlayers.this, GameStats.class);
                intent.putExtra("game", game);
                startActivity(intent);
                jDialog.dismiss();
                finish();
            }
        };

        handler.postDelayed(runnable, 2000);


    }

    public void WaitForOpponentCount(){
        if (isHost) {
            hRef.child("player2").child("selected_players").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        WaitForOpponentCount();
                    } else {
                        hRef.child("player2").child("selected_players").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String val = dataSnapshot.getValue().toString();
                                opp_count.setText(val + "/" + limit);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            hRef.child("player1").child("selected_players").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        WaitForOpponentCount();
                    } else {
                        hRef.child("player1").child("selected_players").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String val = dataSnapshot.getValue().toString();
                                opp_count.setText(val + "/" + limit);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void startGame() {
        if (isFirst) {
            hRef.child("chance").setValue(YOUR_TURN);
            hRef.child("chance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int chance = Integer.valueOf(dataSnapshot.getValue().toString());

                    if (chance == YOUR_TURN){
                        noticeboard.setText("Choose a player");
                        yourChance = true;
                    }
                    else if (chance == OTHER_TURN){
                        noticeboard.setText("Waiting for opponent to choose player");
                        yourChance = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            waitForFirst();
        }
    }

    public void waitForFirst(){
        hRef.child("chance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            waitForFirst();
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                }
                else {
                    hRef.child("chance").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int chance = Integer.valueOf(dataSnapshot.getValue().toString());

                            if (chance == YOUR_TURN){
                                noticeboard.setText("Choose a player");
                                yourChance = true;
                            }
                            else if (chance == OTHER_TURN){
                                noticeboard.setText("Waiting for opponent to choose player");
                                yourChance = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

        jRef = FirebaseDatabase.getInstance().getReference().child("Games").child(game).child("players");

        FirebaseRecyclerAdapter<Player, PlayerViewHolder> adapter = new FirebaseRecyclerAdapter<Player, PlayerViewHolder>(Player.class, R.layout.player_card, PlayerViewHolder.class, jRef) {
            @Override
            protected void populateViewHolder(PlayerViewHolder viewHolder, Player model, int position) {
                viewHolder.setPlayerCard(model.getName(), model.getImage(), model.getId());
                viewHolder.setFrame(model.getKey());
                viewHolder.setListners(model.getKey());
            }
        };

        mPlayers.setAdapter(adapter);

    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder{

        View mView;
        String player_id;
        ImageView player_image;
        TextView player_name;
        LinearLayout frame;
        int key;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            player_image = mView.findViewById(R.id.player_image);
            player_name = mView.findViewById(R.id.player_name);
            frame = mView.findViewById(R.id.player_frame);
        }

        public void setPlayerCard(String playername, String uri, String id){
            player_name.setText(playername);
            player_id = id;
            try {
                Picasso.get().load(uri).into(player_image);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public void setFrame(int key){
            this.key = key;
            if (key == 0){
                if (isHost){
                    frame.setBackgroundColor(Color.GREEN);
                }
                else {
                    frame.setBackgroundColor(Color.RED);
                }
            }
            else if (key == 1){
                if (isHost){
                    frame.setBackgroundColor(Color.RED);
                }
                else {
                    frame.setBackgroundColor(Color.GREEN);
                }
            }
        }

        public void setListners(final int key){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (yourChance){
                        if (key == 3) {
                            hRef.child("chance").setValue(OTHER_TURN);
                            if (isHost) {
                                hRef.child("player1").child("selected_players").setValue(Integer.valueOf(val) + 1);
                                hRef.child("player1").child("players_selected").push().child("id").setValue(player_id);
                                hRef.child("players").child(player_id).child("key").setValue(0);

                            } else {
                                hRef.child("player2").child("selected_players").setValue(Integer.valueOf(val) + 1);
                                hRef.child("player2").child("players_selected").push().child("id").setValue(player_id);
                                hRef.child("players").child(player_id).child("key").setValue(1);
                            }
                        }

                    }
                }
            });
        }
    }
}
