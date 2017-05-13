package com.jawadjahangir.android.bookwatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A fragment representing a single Local Book detail screen.
 * This fragment is either contained in a {@link LocalBookListActivity}
 * in two-pane mode (on tablets) or a {@link LocalBookDetailActivity}
 * on handsets.
 */
public class LocalBookDetailFragment extends Fragment{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    static Button bookAddButton;
    CharSequence Titles[];


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocalBookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Titles = new CharSequence[]{getString(R.string.details_tab),getString(R.string.extras_tab)};

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            adapter =  new ViewPagerAdapter(getActivity().getSupportFragmentManager(),Titles,2,
                    getArguments().getInt(ARG_ITEM_ID));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.localbook_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.local_detail_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.book_details));


        pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });


        tabs.setViewPager(pager);
        

        return rootView;
    }


}
