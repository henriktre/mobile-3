package com.example.henriktre.lab3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;


public class MainActivityView extends SurfaceView implements Runnable {

    private Sensor nSensor = null;
    SensorManager nSensorManager = null;
    Context ctx = null;

    Thread thread = null;
    boolean canDraw = false;
    int width, height = 0;
    float ax, ay = 0;
    Bitmap backGroundCheck;
    Canvas canvas;
    SurfaceHolder surfaceHolder;
    Circle circle;

    public MainActivityView(Context context) {
        super(context);
        ctx = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        surfaceHolder = getHolder();

        backGroundCheck = BitmapFactory.decodeResource(getResources(), R.drawable.smoke);

        setup(context);
    }


    private void setup(Context context) {

        circle = new Circle(width/2, height/2);


        nSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        if (nSensorManager != null) {
            nSensor = nSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        } else {
            Log.e("SensorManagerError: ", "nSensorManager cant get system service");
        }
        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                ax = sensorEvent.values[0] / 10;
                ay = sensorEvent.values[1] / 10;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        nSensorManager.registerListener(gyroscopeSensorListener, nSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }
    public class Circle {

        private float x, y, vx, vy = 0;
        private int radius = 50;
        Paint circlePaint = new Paint();
        private MediaPlayer mp = MediaPlayer.create(ctx, R.raw.bounce);
        private Vibrator v = (Vibrator) ctx.getSystemService(VIBRATOR_SERVICE);

        private Circle(float xpos, float ypos) {
            circlePaint.setColor(Color.WHITE);
            circlePaint.setStyle(Paint.Style.FILL);
            x = xpos; y = ypos;
        }
        private void collision() {
            if (x <= this.radius) {
                vx = vx*(-0.8f);
                x++;
                hit();
            }
            if (x >= width-this.radius) {
                vx = vx*(-0.8f);
                x--;
                hit();
            }
            if (y <= this.radius) {
                vy = vy*(-0.8f);
                y++;
                hit();
            }
            if (y >= height-this.radius) {
                vy = vy*(-0.8f);
                y--;
                hit();
            }

        }

        private void hit() {
            this.mp.start();
            this.mp.seekTo(250);
            v.vibrate(100);
        }

        private void draw() {
            this.vx += ax; this.vy += ay;

            collision();

            this.x -= this.vx; this.y += this.vy;

            canvas.drawCircle(this.x, this.y, this.radius, circlePaint);

            canvas.drawCircle(x, y, 20, circlePaint);
        }

    }

    @Override
    public void run() {

        while(canDraw) {

            if(!surfaceHolder.getSurface().isValid()) {
                continue;
            }

            canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(backGroundCheck,0,0,null);

            circle.draw();

            surfaceHolder.unlockCanvasAndPost(canvas);

        }

    }

    public void pause() {
        canDraw = false;

        while(true){
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        thread = null;
    }

    public void resume() {
        canDraw = true;
        thread = new Thread(this);
        thread.start();

    }

}
