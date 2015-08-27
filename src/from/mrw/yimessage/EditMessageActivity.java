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
	
//	内容
	private String content = null;
//	字数
	private int text_count = 0;
//	标题
	private String title = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editmessage_activity);
		
//		获得intent
		Intent intent = getIntent();
//		获得_id
		_id = intent.getStringExtra("_id");
//		如果不是null
		if(_id !=null)
		{
//			根据_id查询
			Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from drafts where _id = ?", new String[]{_id});
//			获得短信内容和内容长度
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
//		返回
		backup_editmessageactivity = (ImageView) findViewById(R.id.backup_editmessageactivity);
		backup_editmessageactivity.setOnClickListener(this);
		
//		删除
		delete_editmessageactivity = (ImageView) findViewById(R.id.delete_editmessageactivity);
		delete_editmessageactivity.setOnClickListener(this);
		
//		保存
		save_editmessageactivity = (Button) findViewById(R.id.save_editmessageactivity);
		save_editmessageactivity.setOnClickListener(this);
		
//		发送
		send_editmessageactivity = (Button) findViewById(R.id.send_editMessageactivity);
		send_editmessageactivity.setOnClickListener(this);
		
		textcount = (TextView) findViewById(R.id.textcount);
		
		msg_editmessageactivity = (EditText) findViewById(R.id.msg_editmessageactivity);
		msg_editmessageactivity.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				获得短信长度
				text_count  = msg_editmessageactivity.getText().toString().length();
				textcount.setText("当前已输入"+ text_count + "字");
				
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
		textcount.setText("当前已输入"+ text_count + "字");
		
	}
	
	

	
	
	
//	点击事件监听函数
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
//		后退
		case R.id.backup_editmessageactivity:
//			弹出对话框询问是否放弃修改
			new AlertDialog.Builder(EditMessageActivity.this).setTitle(null).setMessage("是否放弃本次修改？").setIcon(null).setPositiveButton("放弃", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					EditMessageActivity.this.finish();
					
				}
			}).setNegativeButton("取消", null).show();
			
			break;
			
//			删除
		case R.id.delete_editmessageactivity:
//			弹出对话框提示是否删除
			new AlertDialog.Builder(EditMessageActivity.this).setIcon(null).setTitle(null).setMessage("是否删除该短信？").setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					如果_id不为空，即当前短信已在数据库中
					if(_id != null)
					{
//						删除短信
						MainActivity.db.getReadableDatabase().execSQL("delete from drafts where _id = " + _id);
					}
					EditMessageActivity.this.finish();
					}
			}).setNegativeButton("暂不删除", null).show();
			
			break;
			
//			保存
		case R.id.save_editmessageactivity:
			
			Intent intent = new Intent(EditMessageActivity.this,InputTitleActivity.class);
			startActivityForResult(intent, 0);
			
			break;
			
//			发送
		case R.id.send_editMessageactivity:
			Intent send_intent = new Intent(EditMessageActivity.this,SelectGroupActivity.class);
			send_intent.putExtra("msg_content", msg_editmessageactivity.getText().toString() );
			startActivity(send_intent);
			break;
			
			
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		按下返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
//			弹出对话框询问是否放弃修改
			new AlertDialog.Builder(EditMessageActivity.this).setTitle(null).setMessage("是否放弃本次修改？").setIcon(null).setPositiveButton("放弃", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					EditMessageActivity.this.finish();
					
				}
			}).setNegativeButton("取消", null).show();
		}
		
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		请求码为0
		if(requestCode == 0)
		{
			switch(resultCode)
			{
//			返回码为0，点击了确定按钮
			case 0:
				title = data.getStringExtra("title");
//				如果_id不为空，即当前短信已在数据库中
				if(_id != null)
				{
//					删除短信
					MainActivity.db.getReadableDatabase().execSQL("delete from drafts where _id = " + _id);
				}
//				存入数据库
				MainActivity.db.getReadableDatabase().execSQL("insert into drafts values(null,?,?)",new String[]{title,msg_editmessageactivity.getText().toString()});
				EditMessageActivity.this.finish();
				break;
//				返回码为1，点击了取消按钮
			case 1:
				break;
			}
		}
	}
	
	
	
	

}
