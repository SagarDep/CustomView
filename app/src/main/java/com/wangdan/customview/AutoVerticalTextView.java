package com.wangdan.customview;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 重写TextView，实现文字垂直滚动效果
 * 
 * @author blueberry
 * 
 */

public class AutoVerticalTextView extends TextView implements Runnable
{

	private static final String TAG = AutoVerticalTextView.class.getSimpleName();

	private Handler handler = new Handler();
	private Context context;
	private String text;// 显示文本
	private int windowWidth = 0;// 全屏宽度
	private int windowHeight = 0;// 全屏高度
	private int count = 50000;// 滚动次数，默认支持，为很大
	private int textheight = 0;
	private long delayMillis = 0;

	private int scrollSpeed = 0;// 每隔1毫秒次增加的像素

	/** 当前位置坐标 */

	private int currentScrollY = -windowHeight;

	/** 停止标志 */

	private boolean isStop = false;

	/** 文本长度 */

	private int textWidth;

	/** 可测量标志 */

	private boolean isMeasure = false;

	/** 三个构造函数 */

	public AutoVerticalTextView( Context context )
	{

		super( context );
		this.context = context;
		getWindow();
	}

	public AutoVerticalTextView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		this.context = context;
		getWindow();
	}

	public AutoVerticalTextView( Context context, AttributeSet attrs, int defStyle )
	{

		super( context, attrs, defStyle );
		this.context = context;
		getWindow();
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );
		getTextWidth();
	}

	/**
	 * 显示时间
	 * 
	 */
	public void showTime( long DelayMillis )
	{
		delayMillis = DelayMillis;
		handler.removeCallbacks( delayHideview );
		handler.postDelayed( delayHideview, DelayMillis );
	}

	private Runnable delayHideview = new Runnable()
	{

		@Override
		public void run()
		{
			stopScroll();
			setVisibility( View.INVISIBLE );
		}
	};

	/**
	 * 延迟加载文字
	 * 
	 * @param text
	 */
	public void setStringText( String text )
	{
		this.text = text2vertical( text );
		handler.postDelayed( delayShowText, 1000 );

	}

	private Runnable delayShowText = new Runnable()
	{

		@Override
		public void run()
		{
			setText( text );
		}
	};

	/**
	 * 判断字符串包含数字多少
	 * 
	 * @param s
	 * @return
	 */
	public static int textIsContainNum( String s )
	{
		int count = 0;
		for ( int i = 0; i < s.length(); i++ )
		{
			if ( Character.isDigit( s.charAt( i ) ) )
			{
				count += 1;
			}
		}

		return count;

	}

	/**
	 * 文字转成竖直
	 * 
	 * @param text
	 * @return
	 */
	public static String text2vertical( String text )
	{
		if ( !TextUtils.isEmpty( text ) )
		{
			String result = "";
			String[] textArray = text.split( "" );
			int len = textArray.length;
			for ( int i = 0; i < len; i++ )
			{
				result += textArray[i];
				if ( i < len - 1 )
				{
					result += System.getProperty( "line.separator" );
				}
			}
			return result.trim();
		}
		return null;
	}

	/** 测量文本的宽度 */

	private void getTextWidth()
	{
		Paint paint = this.getPaint();
		String str = this.getText().toString();

		// Rect rect= new Rect();
		// paint.getTextBounds( str, 0, str.length(), rect );
		// int textWidth2 = rect.width();

		int munLength = ( int ) ( textIsContainNum( str ) * this.getTextSize() * 0.4 );
		textWidth = ( int ) paint.measureText( str ) + munLength;
	}

	@Override
	public void run()
	{

		if ( delayMillis != 0 )
		{
			long speed = ( textWidth / ( 5 * delayMillis ) );
			long temp = textWidth % ( 5 * delayMillis );
			if ( temp > 0 )
			{
				speed += 1;
			}

			scrollSpeed = ( int ) Math.ceil( speed );
			currentScrollY += scrollSpeed;
		}
		else
		{
			currentScrollY += 1;
		}

		scrollTo( 0, currentScrollY );// 偏移至(0, currentScrollY)
		if ( isStop )
		{
			setVisibility( View.INVISIBLE );
			return;
		}
		if ( getScrollY() >= textWidth )
		{
			scrollTo( 0, -windowHeight );// 移动到最底部位置
			currentScrollY = -windowHeight;// 改变当前位置-windowHeight
			count -= 1;// 次数减一
		}

		if ( count <= 0 )
		{
			setVisibility( View.INVISIBLE );
			return;
		}

		postDelayed( this, 15 );// 第二个参数控制滚动速度，数值越大滚动越慢

	}

	/** 开始滚动 */

	public void startScroll()
	{
		getTextWidth();
		getWindow();
		currentScrollY = -windowHeight;// 起始坐标在最底部

		isStop = false;// 停止标志=false,标示开始滚动

		this.removeCallbacks( this );

		post( this );// 开始滚动

	}

	/** 停止滚动 */

	public void stopScroll()
	{
		isStop = true;// 停止
	}

	public void showCount( int Count )
	{
		count = Count;
	}

	/** 获取屏幕宽高 */
	@SuppressWarnings( "deprecation" )
	private void getWindow()
	{
		WindowManager wm = ( WindowManager ) context.getSystemService( Context.WINDOW_SERVICE );
		windowWidth = wm.getDefaultDisplay().getWidth();
		windowHeight = wm.getDefaultDisplay().getHeight();
	}

}