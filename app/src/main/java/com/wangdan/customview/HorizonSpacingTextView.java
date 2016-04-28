package com.wangdan.customview;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.TextView;

import com.example.blueberry.customview.R;

/**
 * 控制水平文本间距的TextView
 * 
 * @author BlueBerry
 * 
 */
public class HorizonSpacingTextView extends TextView
{

	private String text = "";
	private int spacing = 0;

	public HorizonSpacingTextView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( attrs );
	}

	public HorizonSpacingTextView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( attrs );
	}

	public HorizonSpacingTextView( Context context )
	{
		super( context );
	}

	public void init( AttributeSet attrs )
	{

		TypedArray t = getContext().obtainStyledAttributes( attrs,
						R.styleable.HorizonSpacingTextView );

		this.spacing = ( int ) t
						.getDimension(
								R.styleable.HorizonSpacingTextView_HorizonSpacing, 0 );

		CharSequence orginalText = super.getText();
		this.text = ( String ) orginalText;
		setText( orginalText, BufferType.NORMAL );
	}

	@Override
	public void setText( CharSequence text, BufferType type )
	{
		String sText = applyLetterSpacing( text, getSpacing() );
		super.setText( sText, type );
	}

	@Override
	@CapturedViewProperty
	public CharSequence getText()
	{
		return text;
	}

	public void setTextSpacing( int spacing )
	{
		this.spacing = spacing;
	}

	public int getSpacing()
	{
		return spacing;
	}

	/**
	 * 给文本添加空格,eg:spacing=1,则添加1个空格
	 * 
	 * @param originalText
	 * @return
	 */
	private String applyLetterSpacing( CharSequence originalText, int spacing )
	{
		if ( TextUtils.isEmpty( originalText ) )
			return "";
		StringBuilder builder = new StringBuilder();
		for ( int i = 0; i < originalText.length(); i++ )
		{
			String c = "" + originalText.charAt( i );
			builder.append( c );
			if ( i < originalText.length() - 1 )
			{
				for ( int m = 0; m < spacing; m++ )
				{
					builder.append( "\u0020" );
				}
			}
		}
		return builder.toString();
	}

}
