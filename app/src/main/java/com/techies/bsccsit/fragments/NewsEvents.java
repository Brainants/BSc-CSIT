package com.techies.bsccsit.fragments;


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

import com.techies.bsccsit.R;

public class NewsEvents extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public NewsEvents() {
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
        tabLayout= (TabLayout) view.findViewById(R.id.tavView);
        viewPager= (ViewPager) view.findViewById(R.id.viewPager);
    }

    class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0)
                return "News";
            else if (position==1)
                return "Events";
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            if (position==0)
                return new News();
            else  if (position==1)
                return new Events();
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
        return inflater.inflate(R.layout.fragment_news_events, container, false);
    }
}
