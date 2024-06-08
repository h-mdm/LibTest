package com.hmdm.libtest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hmdm.HeadwindMDM;

public class MainActivity extends AppCompatActivity implements HeadwindMDM.EventHandler {

    private String API_KEY = "c4Bz60gRwz";
    private TextView infoView;
    private Button updateConfigButton;

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
        Log.d("com.hmdm.libtest", "Got a device ID: " + deviceId);
        infoView.setText("Version: " + version + "\n" +
                "Device ID: " + deviceId + "\n" +
                "IMEI: " + imei + "\n");
    }

    @Override
    public void onHeadwindMDMDisconnected() {
        infoView.setText("Headwind MDM disconnected!");
    }

    @Override
    public void onHeadwindMDMConfigChanged() {

    }
}