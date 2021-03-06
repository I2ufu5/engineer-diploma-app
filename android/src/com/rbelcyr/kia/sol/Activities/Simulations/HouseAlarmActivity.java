package com.rbelcyr.kia.sol.Activities.Simulations;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rbelcyr.kia.sol.Dekorator.DekoratorHouseAlarm;
import com.rbelcyr.kia.sol.ModbusSlaves.HouseAlarmSlave;
import com.rbelcyr.kia.sol.R;
import com.serotonin.modbus4j.exception.IllegalDataAddressException;


public class HouseAlarmActivity extends AppCompatActivity {

    private Button window1Button ;
    private Button window2Button ;
    private Button doorButton ;
    private Button armingButton ;
    private Button movementSensorButton;
    private ImageView alarmLightTB;
    private ImageView alarmSound;
    private ImageView alarmOnOff;
    private ImageView window1,window2,door, movementSensor,lock;
    private Thread modbusUpdater;
    private Handler uiUpdater;
    private HouseAlarmSlave modbusSlave;
    private DekoratorHouseAlarm dekorator;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean isVibrating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_alarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initUiElements();
        setOnClickListeners();

        uiUpdater = new Handler();
        modbusSlave = new HouseAlarmSlave(getApplicationContext());
        dekorator = new DekoratorHouseAlarm(this, modbusSlave);
        mediaPlayer = MediaPlayer.create(this.getApplicationContext(),R.raw.alarm);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        try {
            modbusSlave.startSlaveListener();
            modbusSlave.getAllCoils().set(1,true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        startUiUpdater();
    }

    //@Override
    void initUiElements() {
        window1Button =  findViewById(R.id.window1Button);
        window2Button =  findViewById(R.id.window2Button);
        doorButton =  findViewById(R.id.doorButton);
        armingButton =  findViewById(R.id.armingButton);
        movementSensorButton =  findViewById(R.id.movementSensorbutton);
        alarmLightTB =  findViewById(R.id.alarm_swiatlo);
        alarmSound =  findViewById(R.id.alarm_dzwiek);
        alarmOnOff =  findViewById(R.id.alarm_status);

        window1 = findViewById(R.id.window1_open);
        window2 = findViewById(R.id.window2_open);
        door = findViewById(R.id.door_open);
        movementSensor = findViewById(R.id.buddy);
        lock = findViewById(R.id.lock);
    }

    protected void setOnClickListeners(){
        window1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbusSlave.switchInput(0);
                    window1Button.setText(modbusSlave.getAllInputs().get(0).toString());
                } catch (IllegalDataAddressException e) {
                    e.printStackTrace();
                }
            }
        });

        window2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbusSlave.switchInput(1);
                    window2Button.setText(modbusSlave.getAllInputs().get(1).toString());
                } catch (IllegalDataAddressException e) {
                    e.printStackTrace();
                }
            }
        });

        doorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbusSlave.switchInput(2);
                    doorButton.setText(modbusSlave.getAllInputs().get(2).toString());
                } catch (IllegalDataAddressException e) {
                    e.printStackTrace();
                }
            }
        });

        movementSensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbusSlave.switchInput(3);
                    movementSensorButton.setText(modbusSlave.getAllInputs().get(3).toString());
                } catch (IllegalDataAddressException e) {
                    e.printStackTrace();
                }
            }
        });

        armingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    modbusSlave.switchInput(4);
                    armingButton.setText(modbusSlave.getAllInputs().get(4).toString());
                } catch (IllegalDataAddressException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void startUiUpdater(){

        uiUpdater.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                                if(modbusSlave.getAllInputs().get(0))
                                    window1.setVisibility(View.VISIBLE);
                                else window1.setVisibility(View.INVISIBLE);

                                if(modbusSlave.getAllInputs().get(1))
                                    window2.setVisibility(View.VISIBLE);
                                else window2.setVisibility(View.INVISIBLE);

                                if(modbusSlave.getAllInputs().get(2))
                                    door.setVisibility(View.VISIBLE);
                                else door.setVisibility(View.INVISIBLE);

                                if(modbusSlave.getAllInputs().get(3))
                                    movementSensor.setVisibility(View.VISIBLE);
                                else movementSensor.setVisibility(View.INVISIBLE);

                                if(modbusSlave.getAllInputs().get(4))
                                    lock.setImageResource(R.drawable.lock_on);
                                else lock.setImageResource(R.drawable.lock_off);

                                if(modbusSlave.getAllCoils().get(0)){
                                    alarmLightTB.setImageResource(R.drawable.red_light);
                                    startVibrate();
                                }else {
                                    alarmLightTB.setImageResource(R.drawable.gray_light);
                                    stopVibrate();
                                }

                                if(modbusSlave.getAllCoils().get(1)){
                                    alarmSound.setImageResource(R.drawable.iconfinder_speaker_volume_293647);
                                    mediaPlayer.start();
                                }else {
                                    alarmSound.setImageResource(R.drawable.iconfinder_speaker_293703);
                                    mediaPlayer.stop();
                                    mediaPlayer.prepare();
                                }

                                if(modbusSlave.getAllCoils().get(2)){
                                    alarmOnOff.setImageResource(R.drawable.alarm_on);
                                }else alarmOnOff.setImageResource(R.drawable.alarm_of);

                                dekorator.update();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        uiUpdater.postDelayed(this,50);
                    }
                });
            }
        },50);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //modbusUpdater.interrupt();
        uiUpdater.removeCallbacksAndMessages(null);
    }

    private void startVibrate(){
        long[] vibrationPattern = new long[]{0,200,500,200,500};
        if(vibrator.hasVibrator() & !isVibrating){
            vibrator.vibrate(vibrationPattern,1);
            isVibrating = true;
        }

    }

    private void stopVibrate(){
        if(vibrator.hasVibrator() & isVibrating){
            vibrator.cancel();
            isVibrating = false;
        }

    }
}



