package com.example.bludiskoteplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

public class Platno extends View implements SensorEventListener {
    private int time = 60;
    private Handler handler;
    private SensorManager sManager;
    private Sensor accelerometer;   // senzor akcelerometra
    private Point screenSize;       // rozmery obrazovky
    private Paint paint;
    private float xBall, yBall;     // poloha cervenej
    private int xHole, yHole;       // poloha modrej
    private List<Prekazka> polePrekazok; // zoznam prekážok
    private int points = 0;
    private Bitmap bg;

    public Platno(Context context) {
        super(context);
        paint = new Paint();

        bg = Bitmap.createBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.background));

        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // ziskanie rozmerov zariadenia
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);



        xBall = screenSize.x / 2;
        yBall = screenSize.y / 2-20;

        // polohu modrej
        xHole = (int) (Math.random() * screenSize.x);
        yHole = (int) (Math.random() * (screenSize.y - 200));

        // prekážky
        polePrekazok = new ArrayList<>();
        polePrekazok.add(new Prekazka(0 , screenSize.y / 2, screenSize.x-150, 80));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3+200, screenSize.x, 80));
        polePrekazok.add(new Prekazka(600, screenSize.y / 3+600, screenSize.x, 80));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3+600, screenSize.x-800, 80));
        polePrekazok.add(new Prekazka(350, screenSize.y / 3+800, 80, screenSize.y));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3+800, 80, 200));
        polePrekazok.add(new Prekazka(0, screenSize.y -400, screenSize.x-850, 80));
        polePrekazok.add(new Prekazka(screenSize.x /2, screenSize.y -350, screenSize.x, 80));
        polePrekazok.add(new Prekazka(screenSize.x /2, screenSize.y -550, screenSize.x, 80));
        polePrekazok.add(new Prekazka(350, screenSize.y / 3 +800, 600, 80));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3-200, 80, 300));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3-800, 80, 300));
        polePrekazok.add(new Prekazka(400, screenSize.y / 3, screenSize.x, 80));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3 - 200, screenSize.x-250, 80));
        polePrekazok.add(new Prekazka(150, screenSize.y / 3 - 400, screenSize.x-250, 80));
        polePrekazok.add(new Prekazka(400, screenSize.y / 3-600, screenSize.x, 80));

        handler = new Handler();
        startTimer();
    }

    private void startTimer() {
        Thread timerThread = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                    // cakaj 1s
                    Thread.sleep(1000);
                    handler.post(() -> {

                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        activity.setTitle("Body: " + points + " | " + "Čas: " + time);
                        time--;
                    });
                    if (time == 0) {
                        handler.post(() -> {
                            Toast.makeText(getContext(), "KONIEC", Toast.LENGTH_LONG).show();
                            zastavSnimanie();
                            endGame();
                        });
                        Thread.currentThread().interrupt();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timerThread.start();
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float krokx = -2f * event.values[0];
            float kroky = 2f * event.values[1];
            float novaPolohaX = xBall + krokx;
            float novaPolohaY = yBall + kroky;

            // klzanie po okraji prekazky
            if (novaPolohaX < 20) {
                novaPolohaX = 20;
            } else if (novaPolohaX > screenSize.x - 20) {
                novaPolohaX = screenSize.x - 20;
            }

            if (novaPolohaY < 20) {
                novaPolohaY = 20;
            } else if (novaPolohaY > screenSize.y - 200) {
                novaPolohaY = screenSize.y - 200;
            }

            // ---
            if (!jeKoliziaSPrekazkou(novaPolohaX, novaPolohaY)) {
                xBall = novaPolohaX;
                yBall = novaPolohaY;
            }

            // klzanie po hrane
            float vzdialenostOdLavejHrany = Math.abs(novaPolohaX - polePrekazok.get(0).getX());
            float vzdialenostOdPravejHrany = Math.abs(novaPolohaX - (polePrekazok.get(0).getX() + polePrekazok.get(0).getWidth()));
            float vzdialenostOdHornejHrany = Math.abs(novaPolohaY - polePrekazok.get(0).getY());
            float vzdialenostOdDolnejHrany = Math.abs(novaPolohaY - (polePrekazok.get(0).getY() + polePrekazok.get(0).getHeight()));


            float minVzdialenost = Math.min(Math.min(vzdialenostOdLavejHrany-20, vzdialenostOdPravejHrany+20),
                    Math.min(vzdialenostOdHornejHrany-20, vzdialenostOdDolnejHrany+20));

            if (minVzdialenost == vzdialenostOdLavejHrany) {
                // l hrana
                yBall = novaPolohaY;
            } else if (minVzdialenost == vzdialenostOdPravejHrany) {
                // p hrana
                yBall = novaPolohaY;
            } else if (minVzdialenost == vzdialenostOdHornejHrany) {
                // h hrana
                xBall = novaPolohaX;
            } else {
                // d hrana
                xBall = novaPolohaX;
            }

            // kolizia -> nemen poziciu
            if (jeKoliziaSPrekazkou(xBall, yBall)) {
                xBall = xBall - krokx;
                yBall = yBall + kroky;
            }

            // skore +1
            if (zasahDoModrej(xBall, yBall)) {
                novyZasah();
            }
            invalidate();
        }
    }

    private boolean jeKoliziaSPrekazkou(float x, float y) {
        for (Prekazka obstacle : polePrekazok) {
            if (obstacle.jeKolizia(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void novyZasah() {
        xHole = (int) (Math.random() * screenSize.x);
        yHole = (int) (Math.random() * (screenSize.y - 200));
        points++;
        if (getContext() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getContext();
            activity.setTitle("Body: " + points + " | " + "Čas: " + time);
        }
    }

    private boolean zasahDoModrej(float x, float y) {
        return Math.sqrt((xHole - x) * (xHole - x) + (yHole - y) * (yHole - y)) < 60;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bg, 0, 0, paint);

        int modraF = Color.BLUE;
        paint.setColor(modraF);
        canvas.drawCircle(xHole, yHole, 60, paint);

        int cervenaF = Color.RED;
        paint.setColor(cervenaF);
        canvas.drawCircle(xBall, yBall, 20, paint);

        int zelenaF = Color.GREEN;
        paint.setColor(zelenaF);
        for (Prekazka prekazka : polePrekazok) {
            canvas.drawRect(prekazka.getX(), prekazka.getY(),
                    prekazka.getX() + prekazka.getWidth(), prekazka.getY() + prekazka.getHeight(), paint);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void zastavSnimanie() {
        sManager.unregisterListener(this);
    }

    public void spustiSnimanie() {
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    // prekážka
    private static class Prekazka {
        private float x, y, width, height;

        public Prekazka(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public boolean jeKolizia(float px, float py) {
            return px+20 > x && px-20 < x + width && py+20 > y && py-20 < y + height;
        }
    }

    private void endGame() {
        Intent intent = new Intent(getContext(), ResetActivity.class);
        intent.putExtra("points", String.valueOf(points));
        getContext().startActivity(intent);
        if (getContext() instanceof AppCompatActivity) {
            ((AppCompatActivity) getContext()).finish();
        }
    }
}