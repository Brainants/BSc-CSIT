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

import com.afollestad.materialdialogs.MaterialDialog;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.eLibraryDownloader;


public class eLibrary extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public eLibrary() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Singleton.eLibraryCount() == 0) {
            final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .progress(true, 0)
                    .content("Fetching data...")
                    .cancelable(false)
                    .build();
            dialog.show();

            eLibraryDownloader downloader = new eLibraryDownloader();
            downloader.doInBackground();
            downloader.setTaskCompleteListener(new eLibraryDownloader.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean success) {
                    if (success) {
                        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        new MaterialDialog.Builder(getActivity())
                                .content("Unable to retrieve data. Please try in a little bit.")
                                .title("Connection Error")
                                .show();
                    }
                }
            });
        }
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayoutLibrary);
        viewPager = (ViewPager) view.findViewById(R.id.viewPagerLibrary);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_library, container, false);
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Syllabus";
            else if (position == 1)
                return "Notes";
            else if (position == 2)
                return "Old Questions";
            else if (position == 3)
                return "Solutions";
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            return eLibraryPagerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
