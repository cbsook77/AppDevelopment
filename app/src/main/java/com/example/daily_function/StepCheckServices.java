package com.example.daily_function;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.os.Build;

import androidx.core.app.NotificationCompat;


public class StepCheckServices extends Service implements SensorEventListener {
    public StepCheckServices() {
    }
    int count = StepValue.Step;
    // 맴버변수 (마지막과 현재값을 비교하여 변위를 계산하는 방식
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;

    private float x, y, z;
    private static final int SHAKE_THRESHOLD = 800;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    public static String CHANNEL_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("onCreate", "IN");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("") // 제목 설정
                    .setContentText("").build(); // 내용 설정



            startForeground(1, notification);

            // 시스템 서비스에서 센서메니져 획득
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            // TYPE_ACCELEROMETER의 기본 센서객체를 획득
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("onStartCommand", "IN");
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        return START_STICKY; //서비스가 종료되어도 자동으로 다시 실행
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "IN");
        //  if (sensorManager != null) {
        //    sensorManager.unregisterListener(this);
        //  StepValue.Step = 0;
        //  }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && sensorManager != null) {
            sensorManager.unregisterListener(this);
            StepValue.Step = 0;
        }

    }



    // 센서에 변화를 감지하기 위해 계속 호출되고 있는 함수
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("onSensorChanged", "IN");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);

            if (gabOfTime > 100) { //  gap of time of step count
                Log.i("onSensorChanged_IF", "FIRST_IF_IN");
                lastTime = currentTime;

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];


                //  변위의 절대값  / gabOfTime * 10000 하여 스피드 계산
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                // 임계값보다 크게 움직였을 경우 다음을 수행
                if (speed > SHAKE_THRESHOLD) {
                    Log.i("onSensorChanged_IF", "SECOND_IF_IN");
                    Intent myFilteredResponse = new Intent("com.example.daily_function");
                    // Intent myFilteredResponse2 = new Intent("com.example.daily_functions");

                    StepValue.Step = count++;
                    Notification_info();


                    String msg = StepValue.Step / 2 + ""; //발걸음 메인액티비티 결과값 파싱
                    // String msg = StepValue.Step + ""; //발걸음 메인액티비티 결과값 파싱
                    String dist_msg = getDistanceRun(StepValue.Step/2) + "km"; // 이동거리
                    String kcal_msg = getCalorie(StepValue.Step)+"kcal"; //칼로리
                    String prog_msg= StepValue.Step/2+"";

                    // 발걸음과, 이동거리 2개다 넣기
                    myFilteredResponse.putExtra("stepService", msg);
                    myFilteredResponse.putExtra("kcalService", kcal_msg);
                    myFilteredResponse.putExtra("distService", dist_msg);
                    myFilteredResponse.putExtra("progService", prog_msg);

                    // 발걸음 브로드 캐스트
                    sendBroadcast(myFilteredResponse);

                }

                // 마지막 위치 저장
                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //발걸음 거리 계산 함수
    public String getDistanceRun(int steps){
        float distance = (float)(steps*78)/(float)100000.0;  //남성은 78cm,  여성은 70cm 이거 그래서 나중에 디비에서 성별 받아와서 추가 수정해야함
        String str_dist=String.format("%.2f",distance);
        return str_dist;
    }

    //칼로리 계산 함수
    public int getCalorie(int steps){
        // double weigt=62.4; //추후에 DB에서 사용자의 몸무게를 가져와야함. 일단 테스트용으로 내 몸무게로 ^^
        int cal = (int)Math.floor((steps/10000.0)*62.4*5.5); //62.4가 weight
        // String str_kcal=Double.toString(cal);
        return (cal/2);
    }

    public void Notification_info() {


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("notificationId", count); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                .setContentTitle("오늘의 걸음: "+StepValue.Step/2)
                .setContentText("10000걸음까지 파이팅입니다!")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 메인액티비로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }
}