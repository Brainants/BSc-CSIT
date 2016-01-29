package com.brainants.bsccsit.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainants.bsccsit.R;

public class Community extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    public Community() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout= (TabLayout) view.findViewById(R.id.tabLayoutCommunity);
        viewPager= (ViewPager) view.findViewById(R.id.viewPagerCommunity);
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0)
                return "Popular";
            else if (position==1)
                return "Following";
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            if (position==0)
                return new PopularCommunities();
            else if(position==1)
                return new FollowingCommunities();
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false);
    }

}
