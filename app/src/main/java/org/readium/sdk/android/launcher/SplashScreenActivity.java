package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 2000L;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this,WelcomeActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();

                /*((LearnMyWayApplication) getApplication()).setPetHelper(LearnMyWayUserOptions.PET_HELPER.LION);
                Intent mainIntent = new Intent(SplashScreenActivity.this,BookshelfActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();*/

               /* Intent listIntent = new Intent(getApplicationContext(),
                        ContainerList.class);
                startActivity(listIntent);*/
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
