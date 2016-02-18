package com.example.view;

import com.example.mybird.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Bird {

	private static final float POSTION_HEIGHT = 1/2f;
	private static final int BIRD_SIZE = 30;			// 鸟尺寸，宽30dp
	
	private int x;
	private int y;
	private int mWidth;
	private int mHeight;
	
	private int gameHeight;
	
	private Bitmap imgBird;
	
	// 绘制范围
	private RectF rect = new RectF();
	
	public Bird(Context context, int gameWith, int gameHeight, Bitmap bitmap){
		imgBird = bitmap;
		this.gameHeight = gameHeight;
		
		// 鸟的位置
		x = gameWith / 2 - bitmap.getWidth() / 2;  
        y = (int) (gameHeight * POSTION_HEIGHT);  
        
        // 计算鸟的宽度和高度  
        mWidth = Util.dp2px(context, BIRD_SIZE);  
        mHeight = (int) (mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());  // 与宽度等比缩放得到高度尺寸
	}
	
	public void draw(Canvas canvas){
		rect.set(x, y, x + mWidth, y + mHeight);  
        canvas.drawBitmap(imgBird, null, rect, null); 
	}
	
	public int getY()  {  
        return y;  
    }  
  
	public int getX(){
		return x;
	}
	
	// 鸟的绘制只在y轴上变化，所以仅提供y方向值
    public void setY(int y)  {  
        this.y = y;  
    } 
    public void resetY(){
    	this.y = (int) (gameHeight * POSTION_HEIGHT);
    }
    
    public void refreshBirdPosY(int y,Canvas canvas){
    	if(y >= 0 ){
    		setY(y);
    	}
    	draw(canvas);
    }
    
    public int getWidth()   {  
        return mWidth;  
    }  
  
    public int getHeight()  {  
        return mHeight;  
    }  
}
