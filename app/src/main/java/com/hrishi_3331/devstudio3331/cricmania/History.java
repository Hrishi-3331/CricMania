package com.hrishi_3331.devstudio3331.cricmania;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class History extends AppCompatActivity {

    private RecyclerView transactions;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private ProgressDialog jDialog;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        transactions = (RecyclerView)findViewById(R.id.transactions);

        manager = new LinearLayoutManager(History.this, LinearLayoutManager.VERTICAL, true);
        manager.setStackFromEnd(true);
        manager.setSmoothScrollbarEnabled(true);
        transactions.setLayoutManager(manager);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("transactions");

        jDialog = new ProgressDialog(History.this);
        jDialog.setTitle("Loading available matches");
        jDialog.setMessage("Please wait...");
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        jDialog.show();
        FirebaseRecyclerAdapter<Transaction, TransactionViewHolder> adapter = new FirebaseRecyclerAdapter<Transaction, TransactionViewHolder>(Transaction.class, R.layout.transaction, TransactionViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(TransactionViewHolder viewHolder, Transaction model, int position) {
                viewHolder.setNote(model.getNote(), model.getMode(), model.getDate(), model.getAmount());
            }
        };

        transactions.setAdapter(adapter);
        userdialog();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView note, mode, date, amount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            note = mView.findViewById(R.id.tran_note);
            mode = mView.findViewById(R.id.tran_mode);
            date = mView.findViewById(R.id.tran_date);
            amount = mView.findViewById(R.id.tran_amount);
        }

        public void setNote(String note, int mode, String date, int amount){
            this.note.setText(note);
            if (mode == 0){
                this.mode.setText("+");
            }
            else {
                this.mode.setText("-");
            }
            this.date.setText(date);
            this.amount.setText(String.valueOf(amount));
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
