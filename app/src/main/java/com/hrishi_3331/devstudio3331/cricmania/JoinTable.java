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
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class JoinTable extends AppCompatActivity {

    private RecyclerView Tables;
    private DatabaseReference jRef;
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

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            jView = itemView;
            host = jView.findViewById(R.id.hostname);
            players = jView.findViewById(R.id.players);
            cr = jView.findViewById(R.id.cr);
        }

        public void setTable(String host, int Players, int cr){
            this.host.setText(host);
            this.players.setText(String.valueOf(Players));
            this.cr.setText(String.valueOf(cr));
        }

        public void getTableId(String table_id){
            this.table_id = table_id;
        }

        public void getHostId(String host_id){
            this.host_id = host_id;
        }

        public void setListners(final Context context){
            jView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Join this table?");
                    builder.setMessage("Are you sure? You will be charged 20 coins if you quit table before end of the game!");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, NewGame.class);
                            intent.putExtra("table_id", table_id);
                            intent.putExtra("match_id", match_id);
                            intent.putExtra("isHost", false);
                            intent.putExtra("other", host_id);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
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
