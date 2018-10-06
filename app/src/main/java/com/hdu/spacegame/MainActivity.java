package com.hdu.spacegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Bitmap space;//우주선이미지
    int space_x,space_y;//우주선위치
    Bitmap Right,Left;//화살표이미지
    int Left_x,Left_y;
    int Right_x,Right_y;
    int button_width;
    int spaceWidth;
    Bitmap screen;//배경이미지
    int Width,Height;//사용자해상도
    int score;


    //미사일장치
    Bitmap missileButton;
    int missileButton_x,missileButton_y;
    int missileWidth;
    int missile_middle;//미사일크기반
    Bitmap missile;
    Bitmap planetimg;

    int count;
    ArrayList<MyMissile> myM;
    ArrayList<Planet> planet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //여기서 this는 현재액티비티를 의미
        setContentView(new MyView(this));

 /*     setContentView(new MyView(this)); 대신에
 MyView m = new MyView(this);  setContentView(m);  이런 방법으로 해도 된다 */

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        //사용중인 가로,세로크기를 변수에 대입
        Width = display.getWidth();
        Height = display.getHeight();


        myM = new ArrayList<MyMissile>();
        planet = new ArrayList<Planet>();



        //우주선의 그림파일의크기를 임의로 조절하는 소스,단말기의 해상도에 맞게 적용
        space = BitmapFactory.decodeResource(getResources(),R.drawable.wspace);
        int y = Height/11;
        int x = Width/8;
        space = Bitmap.createScaledBitmap(space,x,y,true);
        spaceWidth = space.getWidth();
        //Bitmap클래스의 getWidth메소드를 활용해서 그림크기를 구할 수 있다.
        space_x =Width*1/9;
        space_y = Height*6/9;


        //왼쪽이동처리
        Left = BitmapFactory.decodeResource(getResources(),R.drawable.wleft);
        Left_x = Width*5/9;
        Left_y = Height*7/9;
        button_width = Width/6;
        Left = Bitmap.createScaledBitmap(Left,button_width,button_width,true);

        //오른쪽처리
        Right = BitmapFactory.decodeResource(getResources(),R.drawable.wright);
        Right_x = Width*7/9;
        Right_y = Height*7/9;
        Right = Bitmap.createScaledBitmap(Right,button_width,button_width,true);

        //미사일버튼 처리
        missileButton = BitmapFactory.decodeResource(getResources(),R.drawable.panicbutton);
        missileButton = Bitmap.createScaledBitmap(missileButton,button_width,button_width,true);
        missileButton_x = Width*1/11;
        missileButton_y = Height*7/9;

        //미사일처리
        missile = BitmapFactory.decodeResource(getResources(),R.drawable.missile);
        missile = Bitmap.createScaledBitmap(missile,button_width/4,button_width/4,true);
        missileWidth=missile.getWidth();

        //행성처리
        planetimg = BitmapFactory.decodeResource(getResources(),R.drawable.planet);
        planetimg = Bitmap.createScaledBitmap(planetimg,button_width,button_width,true);




        //배경처리
        screen = BitmapFactory.decodeResource(getResources(),R.drawable.screen);
        screen = Bitmap.createScaledBitmap(screen,Width,Height,true);


    }

    class MyView extends View {
        MyView(Context context) {
            super(context);//상위클래스의 생성자 호출
            gHendler.sendEmptyMessageDelayed(0,1000);

        }

        @Override
       synchronized public void onDraw(Canvas canvas) {
            Random r1 = new Random();
            int x = r1.nextInt(Width);
            if (planet.size()<5)
                planet.add(new Planet(x, -100));



            //이곳에 화면에 나타낼 그림이나 문자를 처리
            Paint p1 = new Paint();
            p1.setColor(Color.RED);
            p1.setTextSize(50);
            canvas.drawBitmap(screen,0,0,p1);

            canvas.drawText(Integer.toString(count),0,300,p1);
            canvas.drawText("점수: "+Integer.toString(score),0,200,p1);
            canvas.drawBitmap(space,space_x,space_y,p1);
            canvas.drawBitmap(Right,Right_x,Right_y,p1);
            canvas.drawBitmap(Left,Left_x,Left_y,p1);
            canvas.drawBitmap(missileButton,missileButton_x,missileButton_y,p1);

            for (MyMissile tmp:myM)
                canvas.drawBitmap(missile,tmp.x,tmp.y,p1);

            for (Planet tmp: planet)
                canvas.drawBitmap(planetimg,tmp.x,tmp.y,p1);

            moveMissile();
            movePlanet();
            checkCollision();
            count++;


        }

        public void moveMissile(){
            for(int i = myM.size()-1;i>=0;i--) {
            myM.get(i).move();
            }
            for (int i = myM.size()-1;i>=0;i--){
                //미사일이화면에 벗어나면 없어지도록
                if (myM.get(i).y<0)
                    myM.remove(i);
            }
        }

        public void movePlanet(){

            for (int i=planet.size()-1;i>=0;i--){
                planet.get(i).move();
            }
            for (int i=planet.size()-1;i>=0;i--){
                //미사일이 화면을 벗어나게되면 없에도록 한다
                if (planet.get(i).y>Height)
                    planet.remove(i);
            }
        }
        public void checkCollision(){
            for (int i=planet.size() -1; i>=0; i--){
                for (int j=myM.size()-1; j>=0; j--){
                    if (myM.get(j).x + missile_middle > planet.get(i).x
                            &&myM.get(j).x + missile_middle < planet.get(i).x+button_width
                            &&myM.get(j).y > planet.get(i).y
                            &&myM.get(j).y < planet.get(i).y + button_width){
                        planet.remove(i);
                        myM.get(j).y=-30;
                        score+=10;//행성을 격추할 경우 점수가 100점씩 증가
                    }
                }
            }
        }

        Handler gHendler= new Handler(){
            public void handleMessage(Message msg){
                //반복처리부분
                invalidate();
                gHendler.sendEmptyMessageDelayed(0,30);
                //1000으로 하면 1초에 한번실행
            }
        };
        @Override
        public boolean onTouchEvent(MotionEvent event){
            //화면을 터치했을 경우 처리
            int x=0,y=0;
            if (event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
                x= (int)event.getX();
                y=(int)event.getY();
            }

            if ((x > Left_x) && (x < Left_x + button_width) && (y > Left_y) && (x < Left_y + button_width))
                space_x -= 20;


            if ((x > Right_x) && (x < Right_x + button_width) && (y > Right_y) && (x < Right_y + button_width))
                space_x += 20;

            if (event.getAction() == MotionEvent.ACTION_DOWN)
                if ((x > missileButton_x) && (x < missileButton_x + button_width) && (y > missileButton_y) && (x < missileButton_y + button_width))

                    if (myM.size() < 1) {
                        myM.add(new MyMissile(space_x + spaceWidth / 2 - missileWidth / 2, space_y));
                    }

            return true;
        }
    }
}
