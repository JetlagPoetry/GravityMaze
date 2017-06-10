package fancy.sj0175.s2.studyjams.cn.gravitymaze;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fancy.sj0175.s2.studyjams.cn.gravitymaze.view.BallView;

public class MainActivity extends AppCompatActivity {
    SensorManager sensorManager = null;
    Sensor sensor = null;
    private boolean init = false;
    private int container_width = 0;
    private int container_height = 0;
    private int ball_width = 0;
    private int ball_height = 0;
    private BallView ball;

    private float ballX = 100;
    private float ballY = 100;

    private TextView show = null;
    private RelativeLayout linearLayout = null;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        showAlertDialog(getWindow().findViewById(R.id.activity_main));
        linearLayout = (RelativeLayout)super.findViewById(R.id.activity_main);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("---action down-----");
                        show.setText("起始位置为："+"("+event.getX()+" , "+event.getY()+")");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("---action move-----");
                        show.setText("移动中坐标为："+"("+event.getX()+" , "+event.getY()+")");
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("---action up-----");
                        show.setText("最后位置为："+"("+event.getX()+" , "+event.getY()+")");
                }
                return true;
            }
        });
    }
    // x, y is between [-MAX_ACCELEROMETER, MAX_ACCELEROMETER]
    private void moveTo(float x, float y) {
        ballX +=x;
        ballY +=y;

        if (ballX < 0 ){
            ballX = 0;
        }

        if (ballY < 0){
            ballY = 0;
        }

        if(ballX > container_width - ball_width&&ballY > container_height - ball_height){
            unregister();
            finish();
            startActivity(new Intent(getApplicationContext(),SuccessActivity.class));
        }

        if (ballX > container_width - ball_width){
            ballX = container_width - ball_width;
        }

        if (ballY > container_height - ball_height){
            ballY = container_height - ball_height;
        }

        if(((ballX-70)*(ballX-70)+(ballY-200)*(ballY-200))<60*60) {
            unregister();
            finish();
            startActivity(new Intent(getApplicationContext(),GameOverActivity.class));
        }

        ball.moveTo((int)ballX, (int)ballY);
        Log.v("ball", "ball x="+ballX+" ball y="+ballY);
    }

//    void translate(int pixelX, int pixelY) {
//        int x = pixelX + container_width / 2 - ball_width / 2;
//        int y = pixelY + container_height / 2 - ball_height / 2;
//        ball.moveTo(x, y);
//    }

    public void register(){
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregister(){
        sensorManager.unregisterListener(listener);
    }

    SensorEventListener listener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!init)
                return;
            float x = event.values[SensorManager.DATA_X]*3;
            float y = event.values[SensorManager.DATA_Y]*3;
            float z = event.values[SensorManager.DATA_Z];
//			tv.setText("sensor X="+x+" Y="+y+" Z="+z);
            moveTo(-x,y);

        }

    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && !init){
            init();
            init = true;
        }
    }




    public void init(){
        View container = findViewById(R.id.ball_container);
        container_width = container.getWidth();
        container_height = container.getHeight();
        Log.v("test", "conatiner x="+container_width+" container y="+container_height);
        ball = (BallView) findViewById(R.id.ball);
        ball_width = ball.getWidth();
        ball_height = ball.getHeight();
        //126,141
//        Toast.makeText(getApplicationContext(),ball_height+"=="+ball_width+"==",Toast.LENGTH_LONG).show();
        Log.v("test", "ball x ="+ball_width+" y="+ball_height);
        moveTo(0, 0);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregister();
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregister();
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        register();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        register();
    }

}
