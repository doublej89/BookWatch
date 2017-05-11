package com.jawadjahangir.android.bookwatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A fragment representing a single SearchedBook detail screen.
 * This fragment is either contained in a {@link SearchedBookListActivity}
 * in two-pane mode (on tablets) or a {@link SearchedBookDetailActivity}
 * on handsets.
 */
public class SearchedBookDetailFragment extends Fragment{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    Button bookAddButton;
    CharSequence Titles[]={getString(R.string.details_tab),getString(R.string.extras_tab)};






    /**
     * The dummy content this fragment is presenting.
     */


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchedBookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = SearchedBookContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            //Log.d("Book Details", "book_id: " + getArguments().getString(ARG_ITEM_ID));
            adapter =  new ViewPagerAdapter(getActivity().getSupportFragmentManager(),Titles,2,
                    getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            //CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null && mItem != null) {
//                appBarLayout.setTitle(mItem.title);
//            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.searchedbook_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
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

        bookAddButton = (Button) rootView.findViewById(R.id.book_adder);
        bookAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailTab.alertDialogBuilder.create();
                DetailTab.alertDialogBuilder.show();
            }
        });



        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            //((TextView) rootView.findViewById(R.id.searchedbook_detail)).setText(mItem.title);
//            Log.d("Book Details Load", "view are supposed to load from here");
//
//
//        } else {
//            Log.d("Book Details Load", "the book object is empty");
//        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //getLoaderManager().initLoader(SHELVES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }



}
