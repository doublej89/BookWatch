package com.jawadjahangir.android.bookwatch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class UseTab extends Fragment {

    public static RadioGroup radioGroup;
    public static EditText borrowerField;
    public static RadioButton checkedRadio;


    public UseTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_use_tab, container, false);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioRead);
        borrowerField = (EditText) rootView.findViewById(R.id.loan_field);

        int checkedId = radioGroup.getCheckedRadioButtonId();
        checkedRadio = (RadioButton) rootView.findViewById(checkedId);

        return rootView;

    }

}
