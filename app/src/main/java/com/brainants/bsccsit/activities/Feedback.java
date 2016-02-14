package com.brainants.bsccsit.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.brainants.bsccsit.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class Feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final FancyButton suggest = (FancyButton) findViewById(R.id.suggest);
        final BootstrapEditText suggestText  = (BootstrapEditText) findViewById(R.id.feedbackText);


        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!suggestText.getText().toString().equals("")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    emailIntent.setData(Uri.parse("malito:"));
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@brainants.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback For Beta version of App");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, suggestText.getText().toString());
                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException ex) {
                        // handle error
                    }
                    finish();
                } else {
                    Toast.makeText(Feedback.this,"Please, don't leave the field empty!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        setSupportActionBar((Toolbar) findViewById(R.id.feedBackToolbar));
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }


}
