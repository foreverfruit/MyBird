package com.example.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.mybird.R;
import com.example.mybird.Util;

public class GameContainer extends SurfaceView implements Callback, Runnable {
	
	private static final int BIRD_FLY = 12;		  // 一次触摸屏幕小鸟上升的距离
	private static final int BIDR_DOWN_SPEED = 2; // 小鸟掉下来的速度
	private final int mBirdUpDis = Util.dp2px(getContext(), BIRD_FLY); 
	private final int mBirdDown = Util.dp2px(getContext(), BIDR_DOWN_SPEED);
	private int birdDy = 0; 	// 小鸟下一次高度的变化量
	
	private SurfaceHolder mHolder;  
    private Canvas mCanvas;  
    private Thread mThread;  
    private boolean isRunning; 
    
    private Bitmap mBg;	// 背景图
    
    // view的宽高
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();   // 游戏绘图区域
    
    // 小鸟相关
    private Bird bird;
    private Bitmap bmpBird;
    
    // 地板相关
    private Floor floor;
    private int mFspeed = 3;	// 地板运动速度
    private Bitmap bmpFloor;
    private Paint mPaint;
    
    // 管道相关
	private final int mPipeSpace = Util.dp2px(getContext(), 200); // 管道间隔
    private ArrayList<Pipe> pipes = new ArrayList<Pipe>();
    private int mPspeed = 5;	// 管道速度，相当于就是游戏速度（难度）
    private RectF rectPipe;
    
    // 分数
    private Scores score;
    private int scoreInt;
    
    // 游戏状态
    private GameStatus mStatus = GameStatus.WAITING;
  
    public GameContainer(Context context)  {  
        this(context, null);  
    }  
  
    public GameContainer(Context context, AttributeSet attrs)  {  
        super(context, attrs);  
  
        setZOrderOnTop(true);// 设置画布 背景透明  
        // 设置可获得焦点  
        setFocusable(true);  
        setFocusableInTouchMode(true);  
        // 设置常亮  
        setKeepScreenOn(true);  
        
        mHolder = getHolder();  
        mHolder.addCallback(this);  
        mHolder.setFormat(PixelFormat.TRANSLUCENT);  
        
        // 地板的paint
        mPaint = new Paint();  
        mPaint.setAntiAlias(true);  
        mPaint.setDither(true); 
        
        initBitmaps();
    }
    
    private void initBitmaps() {
    	mBg = loadImageByResId(R.drawable.bg1);
    	bmpBird = loadImageByResId(R.drawable.b1);
    	bmpFloor = loadImageByResId(R.drawable.floor_bg2);
	}

	private Bitmap loadImageByResId(int id) {
		return BitmapFactory.decodeResource(getResources(),id);
	}

	// 初始化尺寸
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	
    	// 游戏窗体尺寸参数
    	mWidth = w;
    	mHeight = h;
    	mGamePanelRect.set(0, 0, mWidth, mHeight);
    	
    	// 小鸟参数
    	bird = new Bird(getContext(), mWidth, mHeight, bmpBird);
    	
    	// 地板
    	floor = new Floor(mWidth, mHeight, bmpFloor);
    	
    	// 管道
    	Pipe pipe = new Pipe(getContext(), mWidth, mHeight, loadImageByResId(R.drawable.g2), loadImageByResId(R.drawable.g1));
    	rectPipe = new RectF(0, 0, Util.dp2px(getContext(), Pipe.PIPE_WIDTH), mHeight); 
    	pipes.add(pipe);
    	
    	// 分数
    	score = new Scores(getContext(), mWidth, mHeight);
	}
  
    @Override  
    public void run()  {  
        while (isRunning)  {  
            long start = System.currentTimeMillis();  
            draw(); 
            logic();
            long end = System.currentTimeMillis();  
  
            try   {  
                if (end - start < 50)   {  
                    Thread.sleep(50 - (end - start));  
                }  
            } catch (InterruptedException e)  {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    // 游戏绘图
    private void draw()   {  
        try  {  
            mCanvas = mHolder.lockCanvas();  // 获得canvas
            if (mCanvas != null)  {  
            	// 1、绘制背景
            	drawBg();
            	// 2、画管道
            	drawPipe();
            	// 3、画地板
            	drawFloor();
            	// 4、画小鸟
            	drawBird();
            	// 5、画分数
            	drawScore();
            	// 检测游戏是否over
            	checkGameOver();
            }  
        } catch (Exception e)  {
        	e.printStackTrace();
        } finally  {  
            if (mCanvas != null)  {
            	mHolder.unlockCanvasAndPost(mCanvas);  
            }
        }  
    }
    
    // 最右管道距离屏幕右侧边界距离
    private int pipeMoveDis = 0;
    private ArrayList<Pipe> removePipes = new ArrayList<Pipe>();	// 本次刷新待删除的管道
    // 游戏逻辑
    private void logic(){
    	switch (mStatus) {
		case RUNNING:
			scoreInt = 0;
			// 刷新管道（同时负责添加、移除管道）
			pipeMoveDis += mPspeed;
			if(pipeMoveDis >= mPipeSpace){
				// 增加一个管道
				pipes.add(new Pipe(getContext(), mWidth, mHeight, loadImageByResId(R.drawable.g2), loadImageByResId(R.drawable.g1)));
				pipeMoveDis = 0;
			}
			for (Pipe pipe : pipes)  {
				if(pipe.getX() + Util.dp2px(getContext(), Pipe.PIPE_WIDTH) < 0 ){
					removePipes.add(pipe);
					continue;
				}
				if( pipe.getX() + Util.dp2px(getContext(), Pipe.PIPE_WIDTH) < bird.getX()){
					scoreInt ++;
				}
                pipe.setX(pipe.getX() - mPspeed);  
			} 
			// 分数
			scoreInt += removePipes.size();
			pipes.removeAll(removePipes);
			
			// 刷新地板
			floor.setX(floor.getX() - mFspeed);
			
			// 小鸟：默认下落
			birdDy += mBirdDown;
			bird.setY(bird.getY() + birdDy);
			
			break;
		case STOP:
			// 如果鸟还在空中，先让它掉下来  
            if (bird.getY() < mHeight)  {  
            	birdDy += mBirdDown;
            	bird.setY(bird.getY() + birdDy);
            } else  {  
                mStatus = GameStatus.WAITING;  
                initPos();  
            }  
			break;
		case WAITING:
			
			break;
		}
    }
    
    private void checkGameOver(){
    	// 检测小鸟与地板的状态
    	if(bird.getY() > floor.getY() - bird.getHeight()){
    		mStatus = GameStatus.STOP;
    	}
    	
    	// 检测小鸟与管道的碰撞
    	for(Pipe pipe : pipes){
    		// 穿过的管道
    		if( pipe.getX() + Util.dp2px(getContext(), Pipe.PIPE_WIDTH) < bird.getX()){
    			continue;
    		}
    		if (pipe.touchBird(bird)){  
                mStatus = GameStatus.STOP;  
                break;  
            }  
    	}
    }
    
    // 触摸操作
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	int action = event.getAction();  
    	  
        if (action == MotionEvent.ACTION_DOWN) {  
            switch (mStatus){  
            case WAITING:  
                mStatus = GameStatus.RUNNING;  
                break;  
            case RUNNING:  
            	birdDy = -mBirdUpDis;  
            	if(bird.getY() + birdDy + mBirdDown < 0 ){	// 禁止超出屏幕
            		birdDy = 0 - bird.getY() - mBirdDown;
            	}
                break; 
            case STOP:	
                break;  
            }  
        }  
        return true;  
    }
    
    // 重置游戏参数
    private void initPos() {
    	score.setScore(0);
    	pipes.clear();
    	pipeMoveDis = 0;
    	birdDy = 0;
    	bird.resetY();
    	scoreInt = 0;
    	removePipes.clear();
    	
    	Pipe pipe = new Pipe(getContext(), mWidth, mHeight, loadImageByResId(R.drawable.g2), loadImageByResId(R.drawable.g1));
    	pipes.add(pipe);
	}

	private void drawScore() {
    	score.refreshScore(mCanvas, scoreInt);
	}

	private void drawPipe() {
		for (Pipe temp : pipes) {
			temp.refreshPipePosX(mCanvas, rectPipe);
		}
	}

	private void drawFloor() {
    	floor.refreshFloorPosX(mCanvas, mPaint);
	}

	private void drawBird() {
    	bird.refreshBirdPosY(-1, mCanvas);	// y = -1 表示默认位置
	}

	private void drawBg() {
		mCanvas.drawBitmap(mBg, null, mGamePanelRect, null);
	}

	@Override  
    public void surfaceCreated(SurfaceHolder holder)   {  
        // 开启线程  
        isRunning = true;  
        mThread = new Thread(this);  
        mThread.start();  
    }  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width,   int height)  {  
    }  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder)   {  
        // 通知关闭线程  
        isRunning = false;  
    } 
    
    private enum GameStatus{
    	WAITING,RUNNING,STOP;
    }
}
