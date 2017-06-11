package fancy.sj0175.s2.studyjams.cn.gravitymaze;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    private float ballX;
    private float ballY;

    private TextView mTextField;

    private SensorEventListener listener;

    private long exitTime = 0;

    private Bitmap center;

    private List<Point> centerList;

    private ImageView mImageView;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        center  = BitmapFactory.decodeResource(getResources(), R.drawable.center);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (!init){
                    moveTo(0, container_height - ball_height);
                    return;
                }
                float x = event.values[SensorManager.DATA_X]*3;
                float y = event.values[SensorManager.DATA_Y]*3;
                float z = event.values[SensorManager.DATA_Z];
                moveTo(-x,y);
            }
        };

        mImageView = (ImageView) findViewById(R.id.center);
        mTextField = (TextView) findViewById(R.id.timer);
        new CountDownTimer(3750, 750) {
            int count = 3;
            public void onTick(long millisUntilFinished) {
                if (count == 0) {
                    mTextField.setText("开始!");
                    return;
                }
                mTextField.setText("" + count);
                count--;
            }

            public void onFinish() {
                mTextField.setVisibility(View.INVISIBLE);
                View container = findViewById(R.id.activity_main);
                container_width = container.getWidth();
                container_height = container.getHeight();
                Log.v("test", "container x=" + container_width + " container y=" + container_height);
                ball = (BallView) findViewById(R.id.ball);
                ball_width = ball.getWidth();
                ball_height = ball.getHeight();
                Log.v("test", "ball x =" + ball_width + " y=" + ball_height);
                ballX = 0;
                ballY = container_height - ball_height;
                register();
                init = true;
            }
        }.start();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        center = getResizedBitmap(center, width, height);
        centerList = new ArrayList<>();
        for (int i=0; i < width; i++) {
            for (int j=0; j < height; j++) {
                if (center.getPixel(i, j) != Color.TRANSPARENT) {
                    centerList.add(new Point(i, j));
                    Log.v("centerList", "i:" + i + " j:" + j);
                    i+=5;
                    j+=5;
                }
            }
        }
//        mImageView.setImageBitmap(center);
        Log.v("init", "init finished.");
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void moveTo(float x, float y) {
        ballX +=x;
        ballY +=y;

        if (ballX < 0 ) {
            ballX = 0;
        }

        if (ballY < 0) {
            ballY = 0;
        }

        if(ballX >= container_width - ball_width && ballY <= 0) {
            unregister();
            finish();
            startActivity(new Intent(getApplicationContext(),SuccessActivity.class));
        }

        if (ballX > container_width - ball_width) {
            ballX = container_width - ball_width;
        }

        if (ballY > container_height - ball_height) {
            ballY = container_height - ball_height;
        }

        ball.moveTo((int)ballX, (int)ballY);
        Log.v("ball", "ball x="+ballX+" ball y="+ballY);

        for (Point c : centerList) {
            if(Math.pow((ballX + ball_width/2 - c.x),2) + Math.pow((ballY + ball_height/2 - c.y),2) <= ball_width * ball_height / 2) {
                unregister();
                finish();
                startActivity(new Intent(getApplicationContext(),GameOverActivity.class));
            }
        }

    }

    private void register() {
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void unregister() {
        sensorManager.unregisterListener(listener);
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
        if (init)
            register();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (init)
            register();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
