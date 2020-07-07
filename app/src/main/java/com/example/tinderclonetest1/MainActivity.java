package com.example.tinderclonetest1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.tinderclonetest1.fragments.ChatFragment;
import com.example.tinderclonetest1.fragments.MatchFragment;
import com.example.tinderclonetest1.fragments.ProfileFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tob_layout);


        viewPager = findViewById(R.id.view_P);

        //list of all fragment you what in view pager
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ProfileFragment());
        fragments.add(new MatchFragment());
        fragments.add(new ChatFragment());

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // setting the icon for each tab
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_account_circle_black);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_whatshot_black_tool);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_sms_black_24dp);

        //change the icon color if selected or unselected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorNone), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //select the second tab the match tab
        tabLayout.getTabAt(1).select();

    }


    //view pager adapter class
    class ViewPagerAdapter extends FragmentPagerAdapter{

        List<Fragment> fragmentList;

        public ViewPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //set user status to online
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fUser = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", "online");

        databaseReference.updateChildren(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //set user status to offline
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String fUser = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fUser);

        Map<String, Object> map = new HashMap<>();
        map.put("isOnline", "offline");

        databaseReference.updateChildren(map);
    }
}
