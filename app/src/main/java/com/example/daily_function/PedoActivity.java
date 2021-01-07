package com.example.daily_function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class PedoActivity extends AppCompatActivity  {

    Intent pedometerService;
    BroadcastReceiver receiver;

    boolean flag = true;
    String serviceData; //발 걸음
    String serviceData2;// 거리
    String serviceData3;// 칼로리
    TextView countText;
    // Button playingBtn;

    //gps 변수
    TextView tvDistDif;
    TextView tvKcal;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedo);

        pedometerService = new Intent(this, StepCheckServices.class);
        receiver = new PlayingReceiver();

        countText = (TextView) findViewById(R.id.stepText);
        tvDistDif = (TextView) findViewById(R.id.tv_dist);
        tvKcal=(TextView)findViewById(R.id.tv_kcal);
        // playingBtn = (Button) findViewById(R.id.btnStopService);
        /*
        playingBtn.setOnClickListener(new View.OnClickListener() {

           @Override
            public void onClick(View v) {

                if (flag) {
                    // TODO Auto-generated method stub
                    try {

                        IntentFilter mainFilter = new IntentFilter("com.example.daily_functions");

                        registerReceiver(receiver, mainFilter);
                        startService(manboService);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {

                    playingBtn.setText("Go !!");

                    // TODO Auto-generated method stub
                    try {

                        unregisterReceiver(receiver);

                        stopService(manboService);

                        // txtMsg.setText("After stoping Service:\n"+service.getClassName());
                    } catch (Exception e) {
                        // TODO: handle exception
                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                flag = !flag;

            }
        });
        */

        //if (flag) {
        try {
            IntentFilter mainFilter = new IntentFilter("com.example.daily_functions");
            registerReceiver(receiver, mainFilter);
            //startService(pedometerService);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(pedometerService);
            }else {
                startService(pedometerService);
            }

        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        //   }
        /* 여기는 이제 다음날 바꿀경우 0으로 초기화하는 구간
        else {


            // TODO Auto-generated method stub
            try {

                unregisterReceiver(receiver);

                stopService(pedometerService);

                // txtMsg.setText("After stoping Service:\n"+service.getClassName());
            } catch (Exception e) {
                // TODO: handle exception
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }


        }
        */
        //   flag = !flag;



    }

    class PlayingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("PlayignReceiver", "IN");
            //serviceData = intent.getStringExtra("stepService");
            serviceData = intent.getExtras().getString("stepService");
            countText.setText(serviceData);

            serviceData2 = intent.getExtras().getString("distService");
            tvDistDif.setText("거리: "+serviceData2);

            serviceData3 = intent.getExtras().getString("kcalService");
            tvKcal.setText("칼로리 소모량: "+serviceData3);



        }


    }
}