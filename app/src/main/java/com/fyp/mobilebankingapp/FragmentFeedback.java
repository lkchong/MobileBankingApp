package com.fyp.mobilebankingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FragmentFeedback extends Fragment {
    View view;
    Spinner feedbackSpinner;
    RatingBar ratingBar;

    public FragmentFeedback() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback, container, false);

        feedbackSpinner = view.findViewById(R.id.feedbackCategory);
        ArrayAdapter<CharSequence> feedbackAdapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.feedback_category, android.R.layout.simple_spinner_item);
        feedbackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackSpinner.setAdapter(feedbackAdapter);

        ratingBar = (RatingBar) view.findViewById(R.id.feedbackRating);


        feedbackSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = feedbackSpinner.getSelectedItem().toString();

                switch (selected) {
                    case "General":
                        ratingBar.setVisibility(View.VISIBLE);
                        break;
                    default:
                        ratingBar.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button submitBtn = view.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });

        return view;
    }

    public void submitFeedback() {
        String feedbackCategory = feedbackSpinner.getSelectedItem().toString();
        String feedbackDetails = ((EditText)view.findViewById(R.id.feedbackDetails))
                                                        .getText().toString();

        FeedbackBackground feedbackBackground = new FeedbackBackground();

        if(feedbackCategory.equals("General")) {
            String rating = Float.toHexString(ratingBar.getRating());
            feedbackBackground.execute(feedbackCategory, feedbackDetails, rating);
        }
        else {
            feedbackBackground.execute(feedbackCategory, feedbackDetails, null);
        }
    }


    public class FeedbackBackground extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


    }
}
