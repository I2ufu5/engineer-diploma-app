package com.rbelcyr.kia.sol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rbelcyr.kia.sol.ModbusSlaves.BallPattern.BallColorPatternActivity;
import com.rbelcyr.kia.sol.ModbusSlaves.BallSorting.BallColorSortingActivity;
import com.rbelcyr.kia.sol.ModbusSlaves.HouseAlarm.HouseAlarmSimActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button houseAlarmSimButton = (Button) findViewById(R.id.HouseAlarmSimulation);
        Button ballColorSortingSimButton = (Button) findViewById(R.id.ballColorSorting);
        Button ballColorPatternSimButton = (Button) findViewById(R.id.ballColorPattern);


        houseAlarmSimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HouseAlarmSimActivity.class);
                startActivity(intent);
            }
        });

        ballColorSortingSimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),BallColorSortingActivity.class);
                startActivity(intent);
            }
        });

        ballColorPatternSimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),BallColorPatternActivity.class);
                startActivity(intent);
            }
        });

    }
}
