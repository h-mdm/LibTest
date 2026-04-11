package com.hmdm.libtest;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hmdm.HeadwindMDM;
import com.hmdm.MDMService;

public class MainActivity extends AppCompatActivity implements HeadwindMDM.EventHandler {

    private String API_KEY = "changeme-8gzk321W";
    private TextView infoView;
    private Button updateConfigButton;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoView = findViewById(R.id.info_view);
        updateConfigButton = findViewById(R.id.update_config_button);
        updateConfigButton.setOnClickListener(v -> {
            try {
                HeadwindMDM.getInstance().forceConfigUpdate();
            } catch (Exception e) {
                Toast.makeText(this, "Error updating config: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HeadwindMDM headwindMDM = HeadwindMDM.getInstance();
        if (!headwindMDM.isConnected()) {
            headwindMDM.setApiKey(API_KEY);
            Log.d("com.hmdm.libtest", "Querying Headwind MDM");
            if (!headwindMDM.connect(this, this)) {
                infoView.setText("Headwind MDM not found!");
            }
        }
    }

    @Override
    public void onHeadwindMDMConnected() {
        String deviceId = HeadwindMDM.getInstance().getDeviceId();
        String imei = HeadwindMDM.getInstance().getImei();
        int version = HeadwindMDM.getInstance().getVersion();
        String param = MDMService.Preferences.get("libtest", null);
        Log.d("com.hmdm.libtest", "Got a device ID: " + deviceId);
        String text = "Library version: " + version + "\n" +
                "Device ID: " + deviceId + "\n" +
                "IMEI: " + imei + "\n";
        if (param != null) {
            text += "Param: " + param + "\n";
        }
        infoView.setText(text);
    }

    @Override
    public void onHeadwindMDMDisconnected() {
        infoView.setText("Headwind MDM disconnected!");
    }

    @Override
    public void onHeadwindMDMConfigChanged() {
        handler.post(() -> {
            Toast.makeText(MainActivity.this, "Configuration updated", Toast.LENGTH_LONG).show();
        });
    }
}
