package com.vad.appbarometer.screens.aboutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vad.appbarometer.R;

public class AboutAppActivity extends AppCompatActivity {

    private TextView textVersion;
    private TextView textTweeter;
    private TextView textShare;
    private TextView textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_about_app);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        textVersion = (TextView) findViewById(R.id.textViewVersion);
        textTweeter = (TextView) findViewById(R.id.textViewTweeter);
        textShare = (TextView) findViewById(R.id.textViewShare);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);

        textViewEmail.setText("gabderahmanov99@gmail.com");
        textViewEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_alternate_email_24, 0, 0, 0);

        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            textVersion.setText(getString(R.string.version) + version);
            textVersion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_info_24, 0, 0, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        textTweeter.setText("@vadhubt");
        textTweeter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24, 0, 0, 0);

        textShare.setText(R.string.share);
        textShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_share_24, 0, 0, 0);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.vad.appbarometer");

        Intent sIntent = Intent.createChooser(shareIntent, null);
        startActivity(sIntent);
    }

    public void shareClick(View view) {
        share();
    }

    private void openTwitter() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/vadhubt"));
        startActivity(browserIntent);
    }

    public void twitterClick(View view) {
        openTwitter();
    }

    public void onCopy(View view) {
        Toast.makeText(this, getResources().getString(R.string.copy), Toast.LENGTH_SHORT).show();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", textViewEmail.getText());
        clipboard.setPrimaryClip(clip);
    }
}