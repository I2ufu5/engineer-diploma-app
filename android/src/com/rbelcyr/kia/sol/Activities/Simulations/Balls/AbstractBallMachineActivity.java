package com.rbelcyr.kia.sol.Activities.Simulations.Balls;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.rbelcyr.kia.sol.Activities.MainActivity;
import com.rbelcyr.kia.sol.Dekorator.DekoratorBallScene;
import com.rbelcyr.kia.sol.ModbusSlaves.AbstractModbusSlave;
import com.rbelcyr.kia.sol.ModbusSlaves.BallMachineSlave;
import com.rbelcyr.kia.sol.R;
import com.serotonin.modbus4j.exception.IllegalDataAddressException;

public abstract class AbstractBallMachineActivity extends AppCompatActivity implements AndroidFragmentApplication.Callbacks {

    protected AbstractModbusSlave modbusSlave;
    protected AbstractBallFragment libgdxFragment;
    Thread thread;
    Handler handler;
    DekoratorBallScene dekorator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_scene);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        modbusSlave = new BallMachineSlave(getApplicationContext());
        handler = new Handler();

        libgdxFragment.scene.setTimeStep(Float.parseFloat(PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext())
                .getString("simSpeed", "0.01666666")));

        try {
            modbusSlave.startSlaveListener();
            getSupportFragmentManager().beginTransaction().
                    add(R.id.game_frame, libgdxFragment).
                    commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dekorator = new DekoratorBallScene(this, modbusSlave);
        dekorator.initUiElements();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dekorator.update();
                        } catch (Exception e) {
                            Log.e("uiUpdate", e.toString());
                        }

                        try {
                            modbusSlave.setInput(0, libgdxFragment.scene.getColorSensorValue());
                            modbusSlave.setInput(1, libgdxFragment.scene.getBallSensorValue());

                            if (modbusSlave.getAllCoils().get(3))
                                libgdxFragment.scene.open(libgdxFragment.scene.S4);
                            else libgdxFragment.scene.close(libgdxFragment.scene.S4);

                            if (modbusSlave.getAllCoils().get(2))
                                libgdxFragment.scene.open(libgdxFragment.scene.S3);
                            else libgdxFragment.scene.close(libgdxFragment.scene.S3);//}

                            if (modbusSlave.getAllCoils().get(1))
                                libgdxFragment.scene.open(libgdxFragment.scene.S2);
                            else libgdxFragment.scene.close(libgdxFragment.scene.S2);//}

                            if (modbusSlave.getAllCoils().get(0))
                                libgdxFragment.scene.open(libgdxFragment.scene.S1);
                            else libgdxFragment.scene.close(libgdxFragment.scene.S1);//}

                        } catch (Exception e) {
                            Log.d("modbus+fragment update:", e.toString());
                        }
                    }
                });
                handler.postDelayed(this, 20);
            }
        }, 20);

    }


    @Override
    public void exit() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        libgdxFragment.exit();
        stop();
        super.onBackPressed();


    }

    private void stop() {
        modbusSlave.stopSlaveListener();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        thread = null;
        modbusSlave = null;
        this.exit();
    }
}
