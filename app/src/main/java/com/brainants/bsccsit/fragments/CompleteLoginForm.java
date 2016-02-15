package com.brainants.bsccsit.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brainants.bsccsit.R;
import com.brainants.bsccsit.advance.Singleton;
import com.brainants.bsccsit.networking.GCMRegIdUploader;

import java.util.HashMap;
import java.util.Map;


public class CompleteLoginForm extends Fragment implements AdapterView.OnItemSelectedListener, TextWatcher {

    private EditText name, email, phoneNo;
    private FloatingActionButton loginBtn;
    private AppCompatSpinner semester, college;
    private SharedPreferences.Editor editor;
    private View view;


    public CompleteLoginForm() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editor = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).edit();

        //semester list bhoreko
        final String[] semsters = {"Select your semester", "First Semester", "Second Semester", "Third Semester", "Fourth Semester",
                "Fifth Semester", "Sixth Semester", "Seventh Semester", "Eighth Semester"};
        ArrayAdapter<String> a = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, semsters);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(a);

        //college list vorekko
        final String[] colleges = getResources().getStringArray(R.array.collegeList);
        ArrayAdapter<String> b = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, colleges);
        b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        college.setAdapter(b);

        semester.setLayoutParams(name.getLayoutParams());
        college.setLayoutParams(name.getLayoutParams());

        semester.setOnItemSelectedListener(this);
        college.setOnItemSelectedListener(this);

        phoneNo.addTextChangedListener(this);
        SharedPreferences pref = getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);

        //login activiy bata aako name edit text ma halera seal gareko
        if (!pref.getString("FullName", "").equals("")) {
            name.setText(pref.getString("FullName", ""));
            name.setEnabled(false);
        }

        //login activiy bata aako email edit text ma halera seal gareko
        if (!pref.getString("email", "").equals("")) {
            email.setText(pref.getString("email", ""));
            email.setEnabled(false);
        }

        //complete button le upload garcha upload method bata
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToServer();
            }
        });
    }

    private void uploadDataToServer() {
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .content("Registering...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, getActivity().getString(R.string.login), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("success")) {
                    dialog.dismiss();
                    editor.putInt("semester", semester.getSelectedItemPosition());
                    editor.putString("phone_number", phoneNo.getText().toString());
                    editor.putString("college", college.getSelectedItem().toString());
                    editor.putBoolean("formFilled", true);
                    editor.apply();
                    new GCMRegIdUploader().doInBackground();

/*                    Answers.getInstance().logLogin(new LoginEvent()
                            .putMethod("Facebook")
                            .putSuccess(true));
*/
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.completeFragHolder, new BasicCommunityChooser())
                            .commit();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                /*Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Facebook")
                        .putSuccess(false));
                */
                Snackbar.make(view.findViewById(R.id.CompleteCore), "Unable to connect.", Snackbar.LENGTH_SHORT).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadDataToServer();
                    }
                }).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name.getText().toString());
                params.put("fbid", getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("UserID", ""));
                params.put("semester", semester.getSelectedItemPosition() + "");
                params.put("phone_number", phoneNo.getText().toString());
                params.put("college", college.getSelectedItem().toString());
                params.put("email", email.getText().toString());
                params.put("gender", getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("Gender", ""));
                params.put("location", getActivity().getSharedPreferences("loginInfo", Context.MODE_PRIVATE).getString("HomeTown", ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Singleton.getInstance().getRequestQueue().add(request);
    }


    private void verifyText() {
        if (phoneNo.getText().toString().length() == 10 &&
                semester.getSelectedItemPosition() > 0 &&
                college.getSelectedItemPosition() > 0 &&
                email.getText().toString().contains("@") &&
                email.getText().toString().contains(".") &&
                !email.getText().toString().contains(" "))
            loginBtn.show();
        else
            loginBtn.hide();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        name = (EditText) view.findViewById(R.id.inputName);
        email = (EditText) view.findViewById(R.id.inputEmail);
        college = (AppCompatSpinner) view.findViewById(R.id.inputCollege);
        phoneNo = (EditText) view.findViewById(R.id.inputPhone);
        semester = (AppCompatSpinner) view.findViewById(R.id.inputSemester);
        loginBtn = (FloatingActionButton) view.findViewById(R.id.continueButton);
        loginBtn.show();
        loginBtn.hide();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        verifyText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        verifyText();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity( fragment
        return inflater.inflate(R.layout.fragment_complete_login_form, container, false);
    }

}
