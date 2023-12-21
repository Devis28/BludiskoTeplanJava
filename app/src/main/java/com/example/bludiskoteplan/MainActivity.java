package com.example.bludiskoteplan;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

// ucelom je za 1min nahrat najviac bodov tym, ze cervenou gulickou sa dotknete modrej gulicky

/*  An issue was found when checking AAR metadata:

  1.  Dependency 'androidx.activity:activity:1.8.0' requires libraries and applications that
      depend on it to compile against version 34 or later of the
      Android APIs.

      :app is currently compiled against android-33.

      Also, the maximum recommended compile SDK version for Android Gradle
      plugin 7.4.1 is 33.

      Recommended action: Update this project's version of the Android Gradle
      plugin to one that supports 34, then update this project to use
      compileSdkVerion of at least 34.

      Note that updating a library or application's compileSdkVersion (which
      allows newer APIs to be used) can be done separately from updating
      targetSdkVersion (which opts the app in to new runtime behavior) and
      minSdkVersion (which determines which devices the app can be installed
      on).

      --- riešenie --> Gradle Scripts -> build.gradle(Module:app) -> compileSsk zmeniť na 34 ---
 */


public class MainActivity extends AppCompatActivity {

    private Platno platno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button startButton = findViewById(R.id.btnStart);
        startButton.setOnClickListener(v -> {
            platno = new Platno(MainActivity.this);
            setContentView(platno);
            platno.spustiSnimanie();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (platno != null) {
            platno.spustiSnimanie();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        platno.zastavSnimanie();
    }
}