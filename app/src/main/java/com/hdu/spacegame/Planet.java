package com.hdu.spacegame;

public class Planet {
    int x,y;
    int planetSpeed = 15;
    int dir;//0이면 행성이 오른쪽에서 왼쪽으로이동
            //1이면 행성이 왼쪽에서 오른쪽으로 이동
    Planet(int x, int y){
        this.x=x;
        this.y=y;

    }


    public void move(){
      y+=planetSpeed;
    }
}
