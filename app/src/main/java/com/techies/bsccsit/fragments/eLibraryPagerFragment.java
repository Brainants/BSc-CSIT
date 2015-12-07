package com.techies.bsccsit.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techies.bsccsit.R;

public class eLibraryPagerFragment extends Fragment {

    private boolean _hasLoadedOnce= false; // your boolean field

    public eLibraryPagerFragment(){
        // Required empty public constructor
    }

    public static eLibraryPagerFragment newInstance(int page) {
        eLibraryPagerFragment fragmentFirst = new eLibraryPagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private void getSyllabus() {
    }

    private void getNotes() {
    }

    private void getOldQuestion() {
    }

    private void getSolutions() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && !_hasLoadedOnce) {
                _hasLoadedOnce = true;
                switch (getArguments().getInt("position")){
                    case 0:
                        getSyllabus();
                        break;
                    case 1:
                        getNotes();
                        break;
                    case 2:
                        getOldQuestion();
                        break;
                    case 3:
                        getSolutions();
                        break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_library_pager, container, false);
    }
}

