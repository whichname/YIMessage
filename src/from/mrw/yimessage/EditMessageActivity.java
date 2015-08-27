package from.mrw.yimessage;

import from.mrw.yimssage.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditMessageActivity extends BaseActivity implements OnClickListener
{

//	backup
	private ImageView backup_editmessageactivity;
	
//	delete
	private ImageView delete_editmessageactivity;
	
//	save
	private Button save_editmessageactivity;
	
//	send
	private Button send_editmessageactivity;
	
//	msg,edittext
	private EditText msg_editmessageactivity;
	
//	textcount
	private TextView textcount;
	
//	_id
	private String _id = null;
	
//	����
	private String content = null;
//	����
	private int text_count = 0;
//	����
	private String title = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editmessage_activity);
		
//		���intent
		Intent intent = getIntent();
//		���_id
		_id = intent.getStringExtra("_id");
//		�������null
		if(_id !=null)
		{
//			����_id��ѯ
			Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from drafts where _id = ?", new String[]{_id});
//			��ö������ݺ����ݳ���
			while(cursor.moveToNext())
			{
				content = cursor.getString(cursor.getColumnIndex("content"));
				text_count = content.length();
			}
		}
		
		
		
		initview();
		
	}
	
	private void initview()
	{
//		����
		backup_editmessageactivity = (ImageView) findViewById(R.id.backup_editmessageactivity);
		backup_editmessageactivity.setOnClickListener(this);
		
//		ɾ��
		delete_editmessageactivity = (ImageView) findViewById(R.id.delete_editmessageactivity);
		delete_editmessageactivity.setOnClickListener(this);
		
//		����
		save_editmessageactivity = (Button) findViewById(R.id.save_editmessageactivity);
		save_editmessageactivity.setOnClickListener(this);
		
//		����
		send_editmessageactivity = (Button) findViewById(R.id.send_editMessageactivity);
		send_editmessageactivity.setOnClickListener(this);
		
		textcount = (TextView) findViewById(R.id.textcount);
		
		msg_editmessageactivity = (EditText) findViewById(R.id.msg_editmessageactivity);
		msg_editmessageactivity.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				��ö��ų���
				text_count  = msg_editmessageactivity.getText().toString().length();
				textcount.setText("��ǰ������"+ text_count + "��");
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
		msg_editmessageactivity.setText(content);
		textcount.setText("��ǰ������"+ text_count + "��");
		
	}
	
	

	
	
	
//	����¼���������
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
//		����
		case R.id.backup_editmessageactivity:
//			�����Ի���ѯ���Ƿ�����޸�
			new AlertDialog.Builder(EditMessageActivity.this).setTitle(null).setMessage("�Ƿ���������޸ģ�").setIcon(null).setPositiveButton("����", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					EditMessageActivity.this.finish();
					
				}
			}).setNegativeButton("ȡ��", null).show();
			
			break;
			
//			ɾ��
		case R.id.delete_editmessageactivity:
//			�����Ի�����ʾ�Ƿ�ɾ��
			new AlertDialog.Builder(EditMessageActivity.this).setIcon(null).setTitle(null).setMessage("�Ƿ�ɾ���ö��ţ�").setPositiveButton("ȷ��ɾ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					���_id��Ϊ�գ�����ǰ�����������ݿ���
					if(_id != null)
					{
//						ɾ������
						MainActivity.db.getReadableDatabase().execSQL("delete from drafts where _id = " + _id);
					}
					EditMessageActivity.this.finish();
					}
			}).setNegativeButton("�ݲ�ɾ��", null).show();
			
			break;
			
//			����
		case R.id.save_editmessageactivity:
			
			Intent intent = new Intent(EditMessageActivity.this,InputTitleActivity.class);
			startActivityForResult(intent, 0);
			
			break;
			
//			����
		case R.id.send_editMessageactivity:
			Intent send_intent = new Intent(EditMessageActivity.this,SelectGroupActivity.class);
			send_intent.putExtra("msg_content", msg_editmessageactivity.getText().toString() );
			startActivity(send_intent);
			break;
			
			
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		���·��ذ�ť
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
//			�����Ի���ѯ���Ƿ�����޸�
			new AlertDialog.Builder(EditMessageActivity.this).setTitle(null).setMessage("�Ƿ���������޸ģ�").setIcon(null).setPositiveButton("����", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					EditMessageActivity.this.finish();
					
				}
			}).setNegativeButton("ȡ��", null).show();
		}
		
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		������Ϊ0
		if(requestCode == 0)
		{
			switch(resultCode)
			{
//			������Ϊ0�������ȷ����ť
			case 0:
				title = data.getStringExtra("title");
//				���_id��Ϊ�գ�����ǰ�����������ݿ���
				if(_id != null)
				{
//					ɾ������
					MainActivity.db.getReadableDatabase().execSQL("delete from drafts where _id = " + _id);
				}
//				�������ݿ�
				MainActivity.db.getReadableDatabase().execSQL("insert into drafts values(null,?,?)",new String[]{title,msg_editmessageactivity.getText().toString()});
				EditMessageActivity.this.finish();
				break;
//				������Ϊ1�������ȡ����ť
			case 1:
				break;
			}
		}
	}
	
	
	
	

}
