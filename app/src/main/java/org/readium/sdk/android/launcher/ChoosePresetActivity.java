package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChoosePresetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_preset);
    }



    public void buttonClicked(View view){
        Intent intent=new Intent(this,VoiceOptionsActivity.class);
        startActivity(intent);
    }


}
