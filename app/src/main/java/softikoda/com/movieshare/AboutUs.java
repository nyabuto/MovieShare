package softikoda.com.movieshare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutUs extends AppCompatActivity {
TextView aboutusData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


aboutusData = (TextView) findViewById(R.id.about_us);
        aboutusData.setText("We are pleased to release version 1 of movie share application. " +
                "Through this application, we hope you will get rich experience in sharing and managing your movies.\n\n" +
                "Business Developer : Anthony Khajira Bukhalana (ABK).\n" +
                "\n" +
                "Software Developer : Geofrey Mwamba Nyabuto (Chief).\n\n" +
                "" +
                "Contact on : \n" +
                "email : info@softikoda.com\n" +
                "Phone : +254711300013.");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
