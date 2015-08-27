package from.mrw.yimessage;

import from.mrw.yimssage.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SideBar extends View {
	
	private TextView mTextDialog;
	private Paint mPaint = new Paint();
	private int choose = -1;
//	监听器
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	
	private static final String[] b = {"#","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBar(Context context) {
		super(context);
	}

	public void setTextView(TextView mTextDialog)
	{
		this.mTextDialog = mTextDialog;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		获得控件总体高度
		int height = getHeight();
//		获得控件总体宽度
		int width = getWidth();
//		获得每一个字母的高度
		int singleHeight = height / b.length;
//		循环，绘制每一个字母
		for(int i = 0 ; i<b.length;i++)
		{
//			设置画笔颜色
			mPaint.setColor(getResources().getColor(R.color.title_bg));
//			设置字体为黑体
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
//			设置抗锯齿
			mPaint.setAntiAlias(true);
//			设置字体大小
			mPaint.setTextSize(25);
//			如果当前字母是选中
			if(choose == i)
			{
//				设置画笔颜色
				mPaint.setColor(Color.parseColor("#000000"));
//				设置字体加粗
				mPaint.setFakeBoldText(true);
			}
//			设置字母起始位置
//			x坐标为控件中间-字符串宽度的一半
			float xPos = width/2 - mPaint.measureText(b[i])/2;
//			y坐标
			float yPos = singleHeight*i+singleHeight/2;
//			绘制
			canvas.drawText(b[i], xPos, yPos, mPaint);
//			重置画笔
			mPaint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
//		获得事件
		final int action = event.getAction();
//		获得点击y坐标
		final float y = event.getY();
//		选中
		final int mChoose = choose;
//		点击的字母为b中第几个=点击y坐标/总高度 * b的总个数
		final int position = (int) (y/getHeight() * b.length);
//		点击事件监听器
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		
		
//		判断当前点击事件
		switch(action)
		{
//		当点击结束
		case MotionEvent.ACTION_UP:
//			设置控件背景透明
			setBackgroundColor(0x00000000);
//			未选中
			choose = -1 ;
//			重绘控件
			invalidate();
//			若当前提示对话框存在
			if(mTextDialog != null)
			{
//				将提示对话框设置隐藏
				mTextDialog.setVisibility(View.GONE);
			}
			break;
//			除点击结束之外所有的事件
		default:
//			设置控件背景
			setBackgroundColor(0x55000000);
//			如果保存的选中的不是当前的
			if(mChoose != position)
			{
//				如果position>0而且position<b.length
				if(position >= 0 && position <b.length)
				{
//					若监听器不为空
					if(listener != null)
					{
//						传递点击的字符
						listener.onTouchingLetterChanged(b[position]);
					}
//					若提示对话框不为空
					if(mTextDialog != null)
					{
//						设置对话框字符
						mTextDialog.setText(b[position]);
//						设置对话框可见
						mTextDialog.setVisibility(View.VISIBLE);
					}
//					选中的为当前字符
					choose = position;
//					重绘
					invalidate();
				}
			}
		}
		
		
//		返回true，说明该事件由本view响应
		return true;
	}

	
//	接口，用于传递点击了哪个字母
	public interface OnTouchingLetterChangedListener
	{
//		传递点击的是哪个字母
		public void onTouchingLetterChanged(String s);
	}
	
	
//	用于设置当前监听器的函数
	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
	{
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}
	
	
	
}
