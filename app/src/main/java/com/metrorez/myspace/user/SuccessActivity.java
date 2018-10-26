package com.metrorez.myspace.user;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Window;
import android.widget.TextView;

import com.metrorez.myspace.R;
import com.metrorez.myspace.user.data.Constants;
import com.metrorez.myspace.user.data.Tools;

import java.util.Timer;
import java.util.TimerTask;

public class SuccessActivity extends AppCompatActivity {

    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Slide());
            getWindow().setExitTransition(new Fade());
        } else {
            // Swap without transition
        }
        setContentView(R.layout.activity_success);
        message = findViewById(R.id.intent_message);

        Intent intent = getIntent();

        String text = intent.getStringExtra(Constants.STRING_EXTRA);
        message.setText(text);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //kill current activity
                finish();
            }
        };

        new Timer().schedule(task, 2000);
        Tools.systemBarLolipop(this);
    }

}
