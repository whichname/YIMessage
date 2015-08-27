package from.mrw.yimessage;

import from.mrw.yimssage.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InputTitleActivity extends Activity implements OnClickListener {
	
//	输入标题的编辑框
	private EditText title_edittext;
//	取消按钮
	private Button cancel_bn;
//	确定按钮
	private Button ok_bn;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_title_activity);
//		初始化函数
		initview();
		
	}

//	初始化函数
	private void initview()
	{
//		编辑框
		title_edittext = (EditText) findViewById(R.id.title_edittext);
//		取消按钮
		cancel_bn = (Button) findViewById(R.id.cancel_bn);
//		确定按钮
		ok_bn = (Button) findViewById(R.id.ok_bn);
		
//		设置监听器
		cancel_bn.setOnClickListener(this);
		ok_bn.setOnClickListener(this);
		
	}

@Override
public void onClick(View arg0) {
//	获得intent
	Intent intent = getIntent();
	switch (arg0.getId()) {

	case R.id.cancel_bn:
//		设置该activity的resultcode
		InputTitleActivity.this.setResult(1,intent);
		InputTitleActivity.this.finish();
		break;

	case R.id.ok_bn:
if(title_edittext.getText().toString().equals(""))
{
		Toast.makeText(InputTitleActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
}
else
{
//		放数据
		intent.putExtra("title", title_edittext.getText().toString());
//		设置该activity的resultcode
		InputTitleActivity.this.setResult(0,intent);
		InputTitleActivity.this.finish();
}
	}
}

//监听返回按钮点击事件
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if(keyCode == KeyEvent.KEYCODE_BACK)
	{
		Intent intent = getIntent();
		InputTitleActivity.this.setResult(1,intent);
		InputTitleActivity.this.finish();
	}
	return super.onKeyDown(keyCode, event);
}



	
	
}
