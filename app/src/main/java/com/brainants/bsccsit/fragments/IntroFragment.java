package com.brainants.bsccsit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brainants.bsccsit.R;
import com.devspark.robototextview.widget.RobotoTextView;

public class IntroFragment extends Fragment {

    private View view;

    public IntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int position = getArguments().getInt("position");
        int[] icons = {R.drawable.news, R.drawable.notice, R.drawable.elibrary
                , R.drawable.forum, R.drawable.projects};
        String[] names = {"News/Events", "TU Notices", "E-Library", "Forum", "Projects"},

                descs = {"Never miss any news update and upcomming events of your interest."
                        , "Simplest and easiest way to get Notices by TU with push notifications."
                        , "PDFs now are well managed and lot more easier to access."
                        , "Get answered by your friends on your queries."
                        , "Convert your dreams to reality finding your right technical partners."};
/*
        ImageView icon = (ImageView) view.findViewById(R.id.imageIntro);
        RobotoTextView title = (RobotoTextView) view.findViewById(R.id.titleIntro),
                desc = (RobotoTextView) view.findViewById(R.id.descIntro);

        icon.setImageResource(icons[position]);
        title.setText(names[position]);
        desc.setText(descs[position]);
        */
    }

    public static Fragment newInstance(int position) {
        IntroFragment fragmentFirst = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }
}
