package lizec.lizec.tlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import lizec.lizec.tlock.model.PwdInfo;
import lizec.lizec.tlock.rand.RandomPassword;

public class AddPwdActivity extends AppCompatActivity {
    EditText editAPP,editName,editPwd,editPwd2;
    Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pwd);

        editAPP = findViewById(R.id.editAPP);
        editName = findViewById(R.id.editName);
        editPwd = findViewById(R.id.editPwd);
        editPwd2 = findViewById(R.id.editPwd2);

        btnOK = findViewById(R.id.btnOK);
        btnOK.setOnClickListener(v->{
            String APPName = editAPP.getText().toString();
            String userName = editName.getText().toString();
            String pwd = editPwd.getText().toString();
            String pwd2 = editPwd2.getText().toString();
            if(pwd.isEmpty() && pwd2.isEmpty()){
                RandomPassword rand = new RandomPassword();
                pwd = rand.getOne();
                Intent intent = new Intent();
                intent.putExtra("APP",APPName);
                intent.putExtra("Name",userName);
                intent.putExtra("Pwd",pwd);
                setResult(0,intent);
                finish();
            }
        });
    }
}
