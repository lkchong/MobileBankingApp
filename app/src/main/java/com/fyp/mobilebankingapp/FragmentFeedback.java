package com.fyp.mobilebankingapp;

import android.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FragmentFeedback extends Fragment {
    View view;
    Spinner feedbackSpinner;
    RatingBar ratingBar;

    AlertDialog.Builder alertDialogBuilder;

    // Variable for feedback insertion into database
    String custID;
    String feedbackCategory;
    String feedbackRating;
    String feedbackDetails;

    String result;

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
        custID = getActivity().getIntent().getStringExtra("custID");
        feedbackCategory = feedbackSpinner.getSelectedItem().toString();

        if(feedbackCategory.equals("General")) {
            feedbackRating = Float.toString(ratingBar.getRating());
        }
        else
        {
            feedbackRating = "-";
        }

        feedbackDetails = ((EditText)view.findViewById(R.id.feedbackDetails))
                                                        .getText().toString().trim();

        FeedbackBackground feedbackBackground = new FeedbackBackground();
        feedbackBackground.execute();
    }


    public class FeedbackBackground extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("OK", null)
                                    .setTitle("Feedback Submission");
        }

        @Override
        protected String doInBackground(String... params) {

            String host = getString(R.string.ip_address);    // IP use 10.0.2.2 for testing using emulator
            String submitfeedback_URL = host + "submitfeedback.php";

            try {
                URL url = new URL(submitfeedback_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("custID","UTF-8")+"="+URLEncoder.encode(custID,"UTF-8")+"&"
                        +URLEncoder.encode("feedbackCategory","UTF-8")+"="+URLEncoder.encode(feedbackCategory,"UTF-8")+"&"
                        +URLEncoder.encode("feedbackRating","UTF-8")+"="+URLEncoder.encode(feedbackRating,"UTF-8")+"&"
                        +URLEncoder.encode("feedbackDetails","UTF-8")+"="+URLEncoder.encode(feedbackDetails,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String line;
                result = "";

                while((line = bufferedReader.readLine()) != null) {
                    result = result + line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("Success")) {
                alertDialogBuilder.setMessage("Feedback Submission Successful")
                                    .create().show();
            }
            else {
                alertDialogBuilder.setMessage("Feedback Submission Failed")
                                    .create().show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
