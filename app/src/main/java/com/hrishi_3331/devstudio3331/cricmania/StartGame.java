package com.hrishi_3331.devstudio3331.cricmania;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class StartGame extends AppCompatActivity {


    private DatabaseReference jRef;
    private ImageView promoImage;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Intent intent = getIntent();
        id = intent.getStringExtra("match_id");

        try {
            jRef = FirebaseDatabase.getInstance().getReference().child("Matches").child(id);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        final TextView team1 = (TextView)findViewById(R.id.game_team1);
        final TextView team2 = (TextView)findViewById(R.id.game_team2);

        promoImage = (ImageView)findViewById(R.id.promo_image);


        jRef.child("team1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team1.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("team2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team2.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("promo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image_uri = dataSnapshot.getValue().toString().trim();
                try {
                    Picasso.get().load(image_uri).into(promoImage);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void JoinTable(View view){
        Intent intent = new Intent(StartGame.this, JoinTable.class);
        intent.putExtra("match_id", id);
        startActivity(intent);
    }

    public void HostTable(View view){
        Intent intent = new Intent(StartGame.this, HostTable.class);
        intent.putExtra("match_id", id);
        startActivity(intent);
    }
}
