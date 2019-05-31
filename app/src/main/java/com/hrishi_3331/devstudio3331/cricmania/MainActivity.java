package com.hrishi_3331.devstudio3331.cricmania;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle mToggle;
    private TextView Username, Usercoins;
    private ImageView avtar;
    private FirebaseAuth jAuth;
    private FirebaseUser jUser;
    private DatabaseReference jRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Freedom");

        mDrawer = (DrawerLayout) findViewById(R.id.mDrawer);
        mNavigation = (NavigationView) findViewById(R.id.mNavigation);
        mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawer, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        jAuth = FirebaseAuth.getInstance();
        jUser = jAuth.getCurrentUser();

        Username = (TextView)findViewById(R.id.username_dashboard);
        Usercoins = (TextView)findViewById(R.id.user_coins);
        avtar = (ImageView)findViewById(R.id.user_image);

        jRef = FirebaseDatabase.getInstance().getReference().child("Users").child(jUser.getUid().trim());
        jRef.child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Username.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("useravtar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Avtar avtara = new Avtar();
                avtar.setImageResource(avtara.getMyAvtar(Integer.valueOf(dataSnapshot.getValue().toString())));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jRef.child("usercoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usercoins.setText(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.logout:
                        jAuth.signOut();
                        startActivity(new Intent(MainActivity.this, Authentication.class));
                        finish();
                        break;

                    case R.id.history:
                        startActivity(new Intent(MainActivity.this, History.class));
                        break;

                    case R.id.contact:
                        startActivity(new Intent(MainActivity.this, Contact.class));
                        break;

                    default:
                        break;
                }
                mDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });

        View header = mNavigation.getHeaderView(0);
        final ImageView user_image = (ImageView) header.findViewById(R.id.user_header_image);
        TextView name = (TextView) header.findViewById(R.id.user_name);
        TextView email = (TextView) header.findViewById(R.id.header_email);

        email.setText(jUser.getEmail());
        name.setText(jUser.getDisplayName());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void PlayNow(View view){
        Intent intent = new Intent(MainActivity.this, AvailableGames.class);
        startActivity(intent);
    }
}
