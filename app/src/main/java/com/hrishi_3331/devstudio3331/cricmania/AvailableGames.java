package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AvailableGames extends AppCompatActivity {

    private DatabaseReference jRef;
    private RecyclerView matches;
    private LinearLayoutManager manager;
    private ProgressDialog jDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_games);

        matches = (RecyclerView)findViewById(R.id.matches);

        jDialog = new ProgressDialog(AvailableGames.this);
        jDialog.setTitle("Loading available matches");
        jDialog.setMessage("Please wait...");
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(false);

        manager = new LinearLayoutManager(AvailableGames.this, LinearLayoutManager.VERTICAL, false);
        matches.setLayoutManager(manager);

        jRef = FirebaseDatabase.getInstance().getReference().child("Matches");

    }

    @Override
    protected void onStart() {
        super.onStart();
        jDialog.show();

        FirebaseRecyclerAdapter<Match, MatchViewHolder> adapter = new FirebaseRecyclerAdapter<Match, MatchViewHolder>(Match.class, R.layout.match_board, MatchViewHolder.class, jRef) {
            @Override
            protected void populateViewHolder(MatchViewHolder viewHolder, Match model, int position) {
                viewHolder.setTeams(model.getTeam1(), model.getTeam2(), model.getTitle());
                viewHolder.setId(model.getMatch_id());
                viewHolder.setListner(AvailableGames.this);

            }
        };
        matches.setAdapter(adapter);
        userdialog();
    }


    public static class MatchViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView match_title, team1, team2;
        private ImageView team1_icon, team2_icon;
        private String id;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            match_title = mView.findViewById(R.id.match_title);
            team1 = mView.findViewById(R.id.team_1);
            team2 = mView.findViewById(R.id.team_2);
            team1_icon = mView.findViewById(R.id.team1_icon);
            team2_icon = mView.findViewById(R.id.team2_icon);
        }

        public void setTeams(String team1, String team2, String title){
            this.team1.setText(team1);
            this.team2.setText(team2);
            match_title.setText(title);
        }

        public void setId(String Id){
            this.id = Id;
        }

        public void setListner(final Context context){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StartGame.class);
                    intent.putExtra("match_id", id);
                    context.startActivity(intent);
                }
            });
        }


    }

    public void userdialog() {
        if (manager.getChildCount() > 0) {
            jDialog.dismiss();
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (manager.getChildCount() > 0) {
                        jDialog.dismiss();
                    } else {
                        userdialog();
                    }
                }
            };
            handler.postDelayed(runnable, 1500);
        }
    }
}
