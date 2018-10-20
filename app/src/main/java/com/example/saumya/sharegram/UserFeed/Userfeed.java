package com.example.saumya.sharegram.UserFeed;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.saumya.sharegram.R;
import com.example.saumya.sharegram.Utils.Navigation;
import com.example.saumya.sharegram.Utils.SectionPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Userfeed extends AppCompatActivity {

    private static final String TAG = "Feed Activity";
    private static final int ACTIVITY_NUM = 3;
    private Context mContext = Userfeed.this;;
    private ViewPager mViewPager;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("message");
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private HashMap<String,String> nameList = new HashMap<String,String>();


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebasedatabase;

    //widgets
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity user feed Starting......");
        setContentView(R.layout.activity_userfeed);
        navigation();
        init();
    }

    private void init() {
//        getUsername();

        SectionPagerAdapter adapter =  new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserFeedFragment());
        adapter.addFragment(new MyFeedFragment());

        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("Following");
        tabLayout.getTabAt(1).setText("You");
    }

    public void navigation(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavViewwBar);
        Navigation.enablenavigation(mContext,this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


//    private void getUsername(){
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference.child(getString(R.string.dbname_users));
//
//        query.addListenerForSingleValueEvent(og ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//                    nameList.put(singleSnapshot.child("user_id").getValue().toString(),singleSnapshot.child("username").getValue().toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    protected HashMap<String,String> getNameList(){
        return nameList;
    }

}
