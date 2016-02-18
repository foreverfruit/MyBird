package com.example.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;

public class Floor {

	private static final float POSTION_HEIGHT = 4/5f;
	
	// 地板坐标
    private int x;  
    private int y;  
    
    // 整体尺寸
    private int mGameWidth;  
    private int mGameHeight;
  
    // 填充物 
    private BitmapShader mFloorShader;  
    
    public Floor(int gameWidth,int gameHeight,Bitmap floorBg){
    	
    	mGameWidth = gameWidth;
    	mGameHeight = gameHeight;
    	
    	y = (int) (mGameHeight * POSTION_HEIGHT);
    	mFloorShader = new BitmapShader(floorBg, TileMode.REPEAT,TileMode.CLAMP);	// x轴方向重复，y轴方向拉伸(拉伸最后一个元素)
    }
    
    public void draw(Canvas canvas,Paint mPaint){
    	if(-x > mGameWidth){	// 不要也不影响结果，这里仅为优化绘图
    		x = x%mGameWidth;
    	}
    	canvas.save(Canvas.MATRIX_SAVE_FLAG);
    	canvas.translate(x, y);  
    	mPaint.setShader(mFloorShader);  
    	canvas.drawRect(x, 0, -x + mGameWidth, mGameHeight - y, mPaint);  
        canvas.restore();  
        mPaint.setShader(null);  
    }
    
    public int getX()   {  
        return x;  
    } 
    public int getY(){
    	return y;
    }
    public void setX(int x)   {  
        this.x = x;  
    } 
    public void refreshFloorPosX(Canvas canvas,Paint mPaint,int x){
    	setX(x);
    	draw(canvas, mPaint);
    }
    public void refreshFloorPosX(Canvas canvas,Paint mPaint){
    	draw(canvas, mPaint);
    }
}
