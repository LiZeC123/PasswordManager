package lizec.lizec.tlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

import lizec.lizec.tlock.net.client.PwdClient;
import lizec.lizec.tlock.net.command.OnExitListener;

public class BackUpActivity extends AppCompatActivity implements OnExitListener {

    PwdClient client;
    Button btnConnect;
    Button btnDisConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisConnect = findViewById(R.id.btnDisConnect);
        btnDisConnect.setEnabled(false);

        EditText editIP = findViewById(R.id.editIP);
        EditText editPort = findViewById(R.id.editPort);

        btnConnect.setOnClickListener(v->{
            String IP = editIP.getText().toString();
            int port = Integer.parseInt(editPort.getText().toString());

            new Thread(() -> {
                try {
                    client = new PwdClient(IP,port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.setSendFile(new File(getFilesDir(),"pwd.data"));
                client.setOnExitListerner(this);
            }).start();
            btnConnect.setEnabled(false);
            btnDisConnect.setEnabled(true);
        });

        btnDisConnect.setOnClickListener(v->{
            new Thread(()->{
                try {
                    client.sendExit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            btnConnect.setEnabled(true);
            btnDisConnect.setEnabled(false);
        });
    }

    @Override
    public void onExit() {
        runOnUiThread(()->{
            btnConnect.setEnabled(true);
            btnDisConnect.setEnabled(false);
        });
    }
}
