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
	
//	�������ı༭��
	private EditText title_edittext;
//	ȡ����ť
	private Button cancel_bn;
//	ȷ����ť
	private Button ok_bn;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_title_activity);
//		��ʼ������
		initview();
		
	}

//	��ʼ������
	private void initview()
	{
//		�༭��
		title_edittext = (EditText) findViewById(R.id.title_edittext);
//		ȡ����ť
		cancel_bn = (Button) findViewById(R.id.cancel_bn);
//		ȷ����ť
		ok_bn = (Button) findViewById(R.id.ok_bn);
		
//		���ü�����
		cancel_bn.setOnClickListener(this);
		ok_bn.setOnClickListener(this);
		
	}

@Override
public void onClick(View arg0) {
//	���intent
	Intent intent = getIntent();
	switch (arg0.getId()) {

	case R.id.cancel_bn:
//		���ø�activity��resultcode
		InputTitleActivity.this.setResult(1,intent);
		InputTitleActivity.this.finish();
		break;

	case R.id.ok_bn:
if(title_edittext.getText().toString().equals(""))
{
		Toast.makeText(InputTitleActivity.this, "���ⲻ��Ϊ��", Toast.LENGTH_SHORT).show();
}
else
{
//		������
		intent.putExtra("title", title_edittext.getText().toString());
//		���ø�activity��resultcode
		InputTitleActivity.this.setResult(0,intent);
		InputTitleActivity.this.finish();
}
	}
}

//�������ذ�ť����¼�
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
