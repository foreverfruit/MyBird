package com.example.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.example.mybird.R;
import com.example.mybird.Util;

public class Scores {

	private static final float POSITION_Y = 1/7f;
	private static final float SIZE_SCORE = 20;		// 分数宽高20dp
	
	private Context context;
	
	// 相对位置
	private int x;
	private int y;
	private int mGameWidth;
	private int mGameHeight;
	
	// 本身尺寸
	private int mSingleWidth;
	private int mSingleHeight;
	private RectF rect;
	
	private int score;
	
	private Bitmap [] numbers;
	private int [] num_ids = {R.drawable.n0, R.drawable.n1,  
            R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,  
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9 };
	
	public Scores(Context context,int gameWidth,int gameHeight){
		this.context = context;
		
		mGameWidth = gameWidth;
		mGameHeight = gameHeight;
		
		y = (int) (mGameHeight * POSITION_Y);
		setScore(50);
		
		mSingleWidth = Util.dp2px(context, SIZE_SCORE);
		mSingleHeight = Util.dp2px(context, SIZE_SCORE);
				
		// 初始化数字图片
		numbers = new Bitmap[num_ids.length];
		for(int i=0;i<num_ids.length;i++){
			numbers[i] = loadImageByResId(num_ids[i]);
		}
		
		rect = new RectF(0, 0, mSingleWidth, mSingleHeight);
	}
	
	public void draw(Canvas canvas){
		String scoreStr = score + "";  
		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);  
		canvas.translate(x,y);  
		
        for (int i = 0; i < scoreStr.length(); i++)  {  
            int num = Integer.parseInt(String.valueOf(scoreStr.charAt(i)));
            canvas.drawBitmap(numbers[num], null, rect, null);  
            canvas.translate(mSingleWidth, 0);  
        }  
        canvas.restore();  
	}
	
	public void setScore(int score){
		this.score = score;
		// 计算绘制分数的x位置
		String scoreStr = score + "";
		if(scoreStr.length() % 2 == 0){
			x = mGameWidth/2 - mSingleWidth*scoreStr.length()/2;
		}else{
			x = mGameWidth/2 - mSingleWidth*scoreStr.length()/2 - mSingleWidth/2 ;
		}
		
	}
	
	public void refreshScore(Canvas canvas,int score){
		setScore(score);
		draw(canvas);
	}
	public void refreshScore(Canvas canvas){
		draw(canvas);
	}
	
	private Bitmap loadImageByResId(int id) {
		return BitmapFactory.decodeResource(context.getResources(),id);
	}
}
