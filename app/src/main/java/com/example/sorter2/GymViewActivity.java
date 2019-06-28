package com.example.sorter2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import models.Gym;
import models.User;
import models.UserManager;

public class GymViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GymViewActivity";

    private ImageView icon;
    private TextView title;
    private TextView score;
    private TextView type;
    private ArrayList<ProgressBar> bars = new ArrayList<>();
    private Button button;
    private User currentUser;
    private UserManager um;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_view);
        // get the extra
        i = getIntent();
        // get widgets
        icon = findViewById(R.id.icon);
        title = findViewById(R.id.name);
        score = findViewById(R.id.score);
        type = findViewById(R.id.type);
        button = findViewById(R.id.going);
        button.setOnClickListener(this);
        um = UserManager.getInstance();
        currentUser = um.getCurrentUser();
        if (currentUser.getGoingTo() != null) {
            if (currentUser.getGoingTo().equals(i.getStringExtra("title_path"))) {
                button.setText("Cancel");
            }
        }
        // bars
        bars.add((ProgressBar) findViewById(R.id.bar06));
        bars.add((ProgressBar) findViewById(R.id.bar07));
        bars.add((ProgressBar) findViewById(R.id.bar08));
        bars.add((ProgressBar) findViewById(R.id.bar09));
        bars.add((ProgressBar) findViewById(R.id.bar10));
        bars.add((ProgressBar) findViewById(R.id.bar11));
        bars.add((ProgressBar) findViewById(R.id.bar12));
        bars.add((ProgressBar) findViewById(R.id.bar13));
        bars.add((ProgressBar) findViewById(R.id.bar14));
        bars.add((ProgressBar) findViewById(R.id.bar15));
        bars.add((ProgressBar) findViewById(R.id.bar16));
        bars.add((ProgressBar) findViewById(R.id.bar17));
        bars.add((ProgressBar) findViewById(R.id.bar18));
        bars.add((ProgressBar) findViewById(R.id.bar19));
        bars.add((ProgressBar) findViewById(R.id.bar20));
        bars.add((ProgressBar) findViewById(R.id.bar21));
        bars.add((ProgressBar) findViewById(R.id.bar22));
        bars.add((ProgressBar) findViewById(R.id.bar23));
        bars.add((ProgressBar) findViewById(R.id.bar24));
        // set the values
        Glide.with(this).asBitmap().load(i.getStringExtra("icon_path")).into(icon);
        title.setText(i.getStringExtra("title_path"));
        score.setText(String.valueOf(i.getDoubleExtra("score_path", 0)));
        type.setText(Gym.Type.values()[i.getIntExtra("type_path", 0)].toString());
        int idx = 0;
        double[] parts =  i.getDoubleArrayExtra("hourly_path");
        for (int j = 0; j < 19; j++) {
            bars.get(j).setProgress((int) (parts[j] * 100));
            idx++;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        if (currentUser.getGoingTo() != null && um.getCurrentUser().getGoingTo().equals(i.getStringExtra("title_path"))){
            um.getCurrentUser().setGoingTo(null);
            button.setText("Go");
        } else {
            um.getCurrentUser().setGoingTo(null);
            um.getCurrentUser().setGoingTo(i.getStringExtra("title_path"));
            button.setText("Cancel");
        }
    }
}
