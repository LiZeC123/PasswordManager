package lizec.lizec.tlock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

import lizec.lizec.tlock.register.ChoosePwdType;

public class Splash extends AppCompatActivity {
    private static final String preferenceName = "info";
    private static final String hasRegisterStr = "HasRegister";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(checkRegister,1000);
    }

    Runnable checkRegister = ()->{
        SharedPreferences preferences = getSharedPreferences(preferenceName,MODE_PRIVATE);
        boolean hasRegister = preferences.getBoolean(hasRegisterStr,true);
        Intent intent;
        if(hasRegister){
            intent = new Intent(Splash.this,LoginActivity.class);
        }
        else{
            intent = new Intent(Splash.this, ChoosePwdType.class);
        }
        startActivity(intent);
        finish();
    };
}
