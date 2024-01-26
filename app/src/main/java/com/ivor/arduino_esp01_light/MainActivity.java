package com.ivor.arduino_esp01_light;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ivor.arduino_esp01_light.tools.UdpMassageAssemble;
import com.ivor.arduino_esp01_light.tools.UdpTool;

public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_light;
    private SeekBar light_strength;
    private TextView light_strength_value;
    private LinearLayout root;
    private UdpTool udpTool;
    private String color = UdpMassageAssemble.LIGHT_COLOR_WHITE;
    private String state = UdpMassageAssemble.LIGHT_STATE_ON;
    private String strength = UdpMassageAssemble.LIGHT_DEFAULT_STRENGTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        udpTool = new UdpTool(this);
        udpTool.setUpdListener(new UdpTool.UPDListener() {
            @Override
            public void OnReceive(String msg) {
                if ("success".equals(msg)) {
                    ToastUtils.showShort(msg);
                }
                LogUtils.d("======>" + msg);
            }
        });
        light_strength_value = findViewById(R.id.light_strength_value);
        light_strength_value.setText("Strength:" + strength);
        root = findViewById(R.id.root);
        findViewById(R.id.Open).setOnClickListener(v -> {
            state = UdpMassageAssemble.LIGHT_STATE_ON;
            sendMessage();
        });
        findViewById(R.id.Close).setOnClickListener(v -> {
            state = UdpMassageAssemble.LIGHT_STATE_OFF;
            sendMessage();
        });

        findViewById(R.id.ClearWifiConfig).setOnClickListener(v -> {
            udpTool.sendMessage(UdpMassageAssemble.getClearConfig());
        });

        rg_light = findViewById(R.id.lights);
        rg_light.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.light_White) {
                    color = UdpMassageAssemble.LIGHT_COLOR_WHITE;
                    root.setBackgroundColor(ColorUtils.getColor(R.color.white));
                }
                if (checkedId == R.id.light_Red) {
                    color = UdpMassageAssemble.LIGHT_COLOR_RED;
                    root.setBackgroundColor(ColorUtils.getColor(R.color.red));
                }
                if (checkedId == R.id.light_Blue) {
                    color = UdpMassageAssemble.LIGHT_COLOR_BLUE;
                    root.setBackgroundColor(ColorUtils.getColor(R.color.blue));
                }
                if (checkedId == R.id.light_Yellow) {
                    color = UdpMassageAssemble.LIGHT_COLOR_Yellow;
                    root.setBackgroundColor(ColorUtils.getColor(R.color.yellow));
                }
                if (checkedId == R.id.light_Green) {
                    color = UdpMassageAssemble.LIGHT_COLOR_GREEN;
                    root.setBackgroundColor(ColorUtils.getColor(R.color.green));
                }
            }
        });

        light_strength = findViewById(R.id.light_strength);
        light_strength.setProgress(Integer.parseInt(strength), true);
        light_strength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                light_strength_value.setText("Strength:" + progress);
                strength = String.valueOf(251 - progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void sendMessage() {
        udpTool.sendMessage(UdpMassageAssemble.getCommend(color, state, strength));
    }
}