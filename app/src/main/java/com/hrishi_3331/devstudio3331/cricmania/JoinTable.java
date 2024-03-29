package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinTable extends AppCompatActivity {

    private RecyclerView Tables;
    private static FirebaseAuth jAuth;
    private static FirebaseUser jUser;
    private static DatabaseReference jRef;
    private Query jQuery;
    private ProgressDialog jDialog;
    private LinearLayoutManager manager;
    public static String match_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_table);

        jDialog = new ProgressDialog(JoinTable.this);
        jDialog.setTitle("Loading available tables");
        jDialog.setMessage("Please wait...");
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(false);

        Intent intent = getIntent();
        match_id = intent.getStringExtra("match_id");

        jRef = FirebaseDatabase.getInstance().getReference().child("Matches").child(match_id).child("Tables");
        jQuery = jRef.orderByChild("status").equalTo(0);

        jAuth = FirebaseAuth.getInstance();
        jUser = jAuth.getCurrentUser();

        Tables = (RecyclerView)findViewById(R.id.available_tables);

        manager = new LinearLayoutManager(JoinTable.this);
        manager.setSmoothScrollbarEnabled(true);
        Tables.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        jDialog.show();
        super.onStart();

        FirebaseRecyclerAdapter<Table, TableViewHolder> adapter = new FirebaseRecyclerAdapter<Table, TableViewHolder>(Table.class, R.layout.table_box, TableViewHolder.class, jQuery) {
            @Override
            protected void populateViewHolder(TableViewHolder viewHolder, Table model, int position) {
                viewHolder.setTable(model.getHost(), model.getPlayers(), model.getCR());
                viewHolder.getTableId(model.getTable_id());
                viewHolder.getHostId(model.getHost_id());
                viewHolder.setListners(JoinTable.this);
            }
        };

        Tables.setAdapter(adapter);
        userdialog();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder{

        private View jView;
        private TextView host, players, cr;
        private String table_id;
        private AlertDialog dialog;
        private String host_id;
        private int cr_val, players_val;
        private ImageView host_image;
        private int MinimumCriteria, player_coins;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            jView = itemView;
            host = jView.findViewById(R.id.hostname);
            players = jView.findViewById(R.id.players);
            cr = jView.findViewById(R.id.cr);
            host_image = jView.findViewById(R.id.host_image);
        }

        public void setTable(String host, int Players, int cr){
            cr_val = cr;
            players_val = Players;
            this.host.setText(host);
            this.players.setText(String.valueOf(Players));
            this.cr.setText(String.valueOf(cr));
        }

        public void getTableId(String table_id){
            this.table_id = table_id;
        }

        public void getHostId(String host_id){
            this.host_id = host_id;
            FirebaseDatabase.getInstance().getReference().child("Users").child(host_id).child("useravtar").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Avtar avtar = new Avtar();
                    host_image.setImageResource(avtar.getMyAvtar(Integer.valueOf(dataSnapshot.getValue().toString())));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Users").child(host_id).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   host.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setListners(final Context context){
            jView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Join this table?");
                    builder.setMessage("Are you sure? You will be charged 50 coins if you quit table before end of the game!");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (minimumCriteria(cr_val, players_val)) {
                                jRef.child(table_id).child("guest_id").setValue(jUser.getUid());
                                jRef.child(table_id).child("status").setValue(1);
                                Intent intent = new Intent(context, NewGame.class);
                                intent.putExtra("table_id", table_id);
                                intent.putExtra("match_id", match_id);
                                intent.putExtra("isHost", false);
                                intent.putExtra("other", host_id);
                                intent.putExtra("settings", cr_val + "x" + players_val);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else {
                                Toast.makeText(context, "You don't have sufficient coins to join this table", Toast.LENGTH_SHORT).show();
                            }
                        }

                        private boolean minimumCriteria(final int cr_val, final int players_val) {

                            FirebaseDatabase.getInstance().getReference().child("Users").child(jUser.getUid()).child("usercoins").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    player_coins = Integer.valueOf(dataSnapshot.getValue().toString());

                                    switch (players_val){
                                        case 1:
                                            MinimumCriteria = cr_val * 200;
                                            break;

                                        case 2:
                                            MinimumCriteria = cr_val * 300;
                                            break;

                                        case 3:
                                            MinimumCriteria = cr_val * 350;
                                            break;

                                        default:
                                            MinimumCriteria = cr_val * 400;
                                            break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            return player_coins >= MinimumCriteria;
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();

                    dialog.show();
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
