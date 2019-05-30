package com.hrishi_3331.devstudio3331.cricmania;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameStats extends AppCompatActivity {

    private String game;
    private DatabaseReference mRef, hRef1, hRef2;
    private TextView player1, player2;
    private RecyclerView player1_list, player2_list;
    private LinearLayoutManager manager1, manager2;
    private static String match;
    private static DatabaseReference jRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stats);

        Intent intent = getIntent();
        game = intent.getStringExtra("game");

        player1 = (TextView)findViewById(R.id.player1_name);
        player2 = (TextView)findViewById(R.id.player2_name);
        player1_list = (RecyclerView)findViewById(R.id.player1_list);
        player2_list = (RecyclerView)findViewById(R.id.player2_list);

        manager1 = new LinearLayoutManager(GameStats.this);
        manager2 = new LinearLayoutManager(GameStats.this);

        player1_list.setLayoutManager(manager1);
        player2_list.setLayoutManager(manager2);


        mRef = FirebaseDatabase.getInstance().getReference().child("Games").child(game);

        mRef.child("player1").child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String id = dataSnapshot.getValue().toString();
                try {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                player1.setText(dataSnapshot.getValue().toString());
                            }
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

        mRef.child("player2").child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String id = dataSnapshot.getValue().toString();
                try {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                player2.setText(dataSnapshot.getValue().toString());
                            }
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

        hRef1 = mRef.child("player1").child("players_selected");
        hRef2 = mRef.child("player2").child("players_selected");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameStats.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mRef.child("match").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                match = dataSnapshot.getValue().toString();
                jRef = FirebaseDatabase.getInstance().getReference().child("Matches").child(match).child("Players");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<SelectedPlayer, PlayerListViewHolder> adapter1 = new FirebaseRecyclerAdapter<SelectedPlayer, PlayerListViewHolder>(SelectedPlayer.class, R.layout.selected_players, PlayerListViewHolder.class, hRef1) {
            @Override
            protected void populateViewHolder(PlayerListViewHolder viewHolder, SelectedPlayer model, int position) {
                viewHolder.setTile(model.getId());
            }
        };
        player1_list.setAdapter(adapter1);

        FirebaseRecyclerAdapter<SelectedPlayer, PlayerListViewHolder> adapter2 = new FirebaseRecyclerAdapter<SelectedPlayer, PlayerListViewHolder>(SelectedPlayer.class, R.layout.selected_players, PlayerListViewHolder.class, hRef2) {
            @Override
            protected void populateViewHolder(PlayerListViewHolder viewHolder, SelectedPlayer model, int position) {
                viewHolder.setTile(model.getId());
            }
        };
        player2_list.setAdapter(adapter2);
    }

    public static class PlayerListViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView PlayerName;
        String id;

        public PlayerListViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            PlayerName = itemView.findViewById(R.id.name);
        }

        public void setTile(String id){
            this.id = id;
            jRef.child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        PlayerName.setText(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
