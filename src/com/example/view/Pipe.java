package com.example.view;

import java.util.Random;

import com.example.mybird.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 *  管道分上下两节
 * @author TopSage
 *
 */
public class Pipe {
	public static int PIPE_WIDTH = 60;	// 宽度60dp
	/** 
     * 上下管道间的距离 
     */  
    private static final float SPACE_BETWEEN_UP_DOWN = 1 / 5F;  
    /** 
     * 上管道的最大高度 
     */  
    private static final float SPACE_MAX_HEIGHT = 1 / 2F;  
    /** 
     * 上管道的最小高度 
     */  
    private static final float SPACE_MIN_HEIGHT = 1 / 6F;  
    
    private Context context;
    
    private int x;  
    private int topHeight;  // 上管道的高度 
    private int margin;     // 上下管道间的距离 
    private int mGameHeight;
    
    private Random random = new Random(); 
    
    private Bitmap mTop;  
    private Bitmap mBottom; 
    
    public Pipe(Context context, int gameWidth, int gameHeight, Bitmap top, Bitmap bottom) {
    	this.context = context;
    	
    	mGameHeight = gameHeight;
    	margin = (int) (gameHeight * SPACE_BETWEEN_UP_DOWN);  
        x = gameWidth;  // 默认从最左边出现  
        
        mTop = top;  
        mBottom = bottom;  
        
        topHeight = randomHeight();  // 随机一个上管道的长度
    }

	private int randomHeight() {
		// 上管道高度
		return (int) (random.nextInt((int) (mGameHeight *(SPACE_MAX_HEIGHT - SPACE_MIN_HEIGHT))) + mGameHeight*SPACE_MIN_HEIGHT);
	}
	
	public void draw(Canvas mCanvas,RectF rect) {
		mCanvas.save(Canvas.MATRIX_SAVE_FLAG);  
        // 上管道 
        mCanvas.translate(x, -(rect.bottom - topHeight));  
        mCanvas.drawBitmap(mTop, null, rect, null);  
        // 下管道
        mCanvas.translate(0, rect.bottom + margin);  // 此处translate偏移的起始点为画上管道时的原点
        mCanvas.drawBitmap(mBottom, null, rect, null);  
        mCanvas.restore();  
	}
	
	public int getX() {  
        return x;  
    }  
  
    public void setX(int x) {  
        this.x = x;  
    }  
    
    public void refreshPipePosX(Canvas mCanvas,int x,RectF rect){
    	setX(x);
    	draw(mCanvas,rect);
    }
    public void refreshPipePosX(Canvas mCanvas,RectF rect){
    	draw(mCanvas,rect);
    }

    // 判断该小鸟是否撞柱子
	public boolean touchBird(Bird bird) {
		if(  ( /*鸟的x坐标在管道的x范围内*/ bird.getX() + bird.getWidth() > x && bird.getX() < x + Util.dp2px(context, PIPE_WIDTH) )  && 
			 ( (/*鸟的y坐标在上管道y内*/ bird.getY() < topHeight )  || (/*鸟的y坐标在下管道的y坐标内*/ bird.getY() + bird.getHeight() > topHeight + margin)  )	){
			return true;
		}
		return false;
	}
}
