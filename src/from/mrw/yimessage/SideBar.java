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
//	������
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
//		��ÿؼ�����߶�
		int height = getHeight();
//		��ÿؼ�������
		int width = getWidth();
//		���ÿһ����ĸ�ĸ߶�
		int singleHeight = height / b.length;
//		ѭ��������ÿһ����ĸ
		for(int i = 0 ; i<b.length;i++)
		{
//			���û�����ɫ
			mPaint.setColor(getResources().getColor(R.color.title_bg));
//			��������Ϊ����
			mPaint.setTypeface(Typeface.DEFAULT_BOLD);
//			���ÿ����
			mPaint.setAntiAlias(true);
//			���������С
			mPaint.setTextSize(25);
//			�����ǰ��ĸ��ѡ��
			if(choose == i)
			{
//				���û�����ɫ
				mPaint.setColor(Color.parseColor("#000000"));
//				��������Ӵ�
				mPaint.setFakeBoldText(true);
			}
//			������ĸ��ʼλ��
//			x����Ϊ�ؼ��м�-�ַ�����ȵ�һ��
			float xPos = width/2 - mPaint.measureText(b[i])/2;
//			y����
			float yPos = singleHeight*i+singleHeight/2;
//			����
			canvas.drawText(b[i], xPos, yPos, mPaint);
//			���û���
			mPaint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
//		����¼�
		final int action = event.getAction();
//		��õ��y����
		final float y = event.getY();
//		ѡ��
		final int mChoose = choose;
//		�������ĸΪb�еڼ���=���y����/�ܸ߶� * b���ܸ���
		final int position = (int) (y/getHeight() * b.length);
//		����¼�������
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		
		
//		�жϵ�ǰ����¼�
		switch(action)
		{
//		���������
		case MotionEvent.ACTION_UP:
//			���ÿؼ�����͸��
			setBackgroundColor(0x00000000);
//			δѡ��
			choose = -1 ;
//			�ػ�ؼ�
			invalidate();
//			����ǰ��ʾ�Ի������
			if(mTextDialog != null)
			{
//				����ʾ�Ի�����������
				mTextDialog.setVisibility(View.GONE);
			}
			break;
//			���������֮�����е��¼�
		default:
//			���ÿؼ�����
			setBackgroundColor(0x55000000);
//			��������ѡ�еĲ��ǵ�ǰ��
			if(mChoose != position)
			{
//				���position>0����position<b.length
				if(position >= 0 && position <b.length)
				{
//					����������Ϊ��
					if(listener != null)
					{
//						���ݵ�����ַ�
						listener.onTouchingLetterChanged(b[position]);
					}
//					����ʾ�Ի���Ϊ��
					if(mTextDialog != null)
					{
//						���öԻ����ַ�
						mTextDialog.setText(b[position]);
//						���öԻ���ɼ�
						mTextDialog.setVisibility(View.VISIBLE);
					}
//					ѡ�е�Ϊ��ǰ�ַ�
					choose = position;
//					�ػ�
					invalidate();
				}
			}
		}
		
		
//		����true��˵�����¼��ɱ�view��Ӧ
		return true;
	}

	
//	�ӿڣ����ڴ��ݵ�����ĸ���ĸ
	public interface OnTouchingLetterChangedListener
	{
//		���ݵ�������ĸ���ĸ
		public void onTouchingLetterChanged(String s);
	}
	
	
//	�������õ�ǰ�������ĺ���
	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
	{
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}
	
	
	
}
