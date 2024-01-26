package com.ivor.arduino_esp01_light;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ivor.arduino_esp01_light.tools.UdpMassageAssemble;
import com.ivor.arduino_esp01_light.tools.UdpTool;

public class MainActivity extends AppCompatActivity {
    private RadioGroup rg_light;
    private SeekBar light_strength;
    private TextView light_strength_value;
    private LinearLayout root;
    private EditText loopTime;
    private UdpTool udpTool;
    private String color = UdpMassageAssemble.LIGHT_COLORS[0];
    private String state = UdpMassageAssemble.LIGHT_STATE_ON;
    private String strength = UdpMassageAssemble.LIGHT_DEFAULT_STRENGTH;
    private long loopTimeDeply = 200;//循环间隔200
    private boolean isLoop = false;

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
        loopTime = findViewById(R.id.loopTime);
        light_strength_value = findViewById(R.id.light_strength_value);
        light_strength_value.setText("Strength:" + 50);//250 - 200
        root = findViewById(R.id.root);
        findViewById(R.id.Open).setOnClickListener(v -> {
            state = UdpMassageAssemble.LIGHT_STATE_ON;
            sendMessage();
        });
        findViewById(R.id.Close).setOnClickListener(v -> {
            state = UdpMassageAssemble.LIGHT_STATE_OFF;
            sendMessage();
        });

        findViewById(R.id.OpenAll).setOnClickListener(v -> {
            isLoop = false;
            state = UdpMassageAssemble.LIGHT_STATE_ON;
            operateAll();
        });

        findViewById(R.id.CloseAll).setOnClickListener(v -> {
            isLoop = false;
            state = UdpMassageAssemble.LIGHT_STATE_OFF;
            operateAll();
        });

        findViewById(R.id.LoopAll).setOnClickListener(v -> {
            isLoop = true;
            operateAll();
        });
        findViewById(R.id.LoopCancel).setOnClickListener(v -> {
            isLoop = false;
            operateAll();
        });

        findViewById(R.id.ClearWifiConfig).setOnClickListener(v -> {
            udpTool.sendMessage(UdpMassageAssemble.getClearConfig());
        });

        rg_light = findViewById(R.id.lights);
        rg_light.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.light_White) {
                    color = UdpMassageAssemble.LIGHT_COLORS[0];
                    root.setBackgroundColor(ColorUtils.getColor(R.color.white));
                }
                if (checkedId == R.id.light_Red) {
                    color = UdpMassageAssemble.LIGHT_COLORS[2];
                    root.setBackgroundColor(ColorUtils.getColor(R.color.red));
                }
                if (checkedId == R.id.light_Blue) {
                    color = UdpMassageAssemble.LIGHT_COLORS[4];
                    root.setBackgroundColor(ColorUtils.getColor(R.color.blue));
                }
                if (checkedId == R.id.light_Yellow) {
                    color = UdpMassageAssemble.LIGHT_COLORS[1];
                    root.setBackgroundColor(ColorUtils.getColor(R.color.yellow));
                }
                if (checkedId == R.id.light_Green) {
                    color = UdpMassageAssemble.LIGHT_COLORS[3];
                    root.setBackgroundColor(ColorUtils.getColor(R.color.green));
                }
            }
        });

        light_strength = findViewById(R.id.light_strength);
        light_strength.setProgress(Integer.parseInt(light_strength_value.getText().toString()), true);
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
                sendMessage();
            }
        });
    }

    private void operateAll() {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() throws Throwable {
                if (isLoop) {
                    while (isLoop) {
                        for (int i = 0; i < UdpMassageAssemble.LIGHT_COLORS.length; i++) {
                            color = UdpMassageAssemble.LIGHT_COLORS[i];
                            sendMessage();
                            sleep(loopTimeDeply);
                        }

                        if (state.equals(UdpMassageAssemble.LIGHT_STATE_ON)) {
                            state = UdpMassageAssemble.LIGHT_STATE_OFF;
                        } else {
                            state = UdpMassageAssemble.LIGHT_STATE_ON;
                        }
                    }
                } else {
                    for (int i = 0; i < UdpMassageAssemble.LIGHT_COLORS.length; i++) {
                        color = UdpMassageAssemble.LIGHT_COLORS[i];
                        sendMessage();
                        sleep(loopTimeDeply);
                    }

                }
                return null;
            }

            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void sendMessage() {
        udpTool.sendMessage(UdpMassageAssemble.getCommend(color, state, strength));
    }
}