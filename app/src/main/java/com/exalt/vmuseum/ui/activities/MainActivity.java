package com.exalt.vmuseum.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.exalt.vmuseum.R;
import com.exalt.vmuseum.ui.activities.DisplayActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveToDisplayActivity();
    }
    public void moveToDisplayActivity(){
        Intent intent=new Intent(this,DisplayActivity.class);
        startActivity(intent);
    }
}
