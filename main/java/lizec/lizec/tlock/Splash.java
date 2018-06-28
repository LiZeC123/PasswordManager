package lizec.lizec.tlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(checkRegister,1000);
    }

    Runnable checkRegister = ()->{
        Intent intent = new Intent(Splash.this,LoginActivity.class);
        startActivity(intent);
        finish();
    };
}
