package from.mrw.yimessage;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import from.mrw.yimssage.R;

public class SentDetailActivity extends Activity implements OnClickListener{
	
//	返回按钮
	private ImageView backup_sent;
//	删除按钮
	private ImageView delete_sent;
//	发送按钮
	private ImageView send_sent;
//	title
	private TextView title_sent;
//	短信内容
	private TextView msg_content_sent;
//	收件人列表框
	private ListView receiver_sent;
	
//	已发送的信息的_id
	private String _id;
	
	
//	短信内容
	private String msg_content = null;
//	短信时间
	private String msg_time = null;
//	收件人的json数据
	private String receiver = null;
//	收件人姓名
	private ArrayList<String> receiver_name = new ArrayList<String>();
//	收件人电话
	private ArrayList<String> receiver_number = new ArrayList<String>();
	
//	适配器
	private MyAdapter myAdapter = new MyAdapter();
	
//	进度条对话框
	private ProgressDialog progressDialog ;
	
//	SmsManager
	private SmsManager smsManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sent);
		
//		获得SmsManager
		smsManager = SmsManager.getDefault();
		
//		获得_id
		_id = getIntent().getStringExtra("_id");
		
		getData();
		
		initview();
		
	}
	
	
//	初始化函数
	private void initview()
	{
//		返回按钮
		backup_sent = (ImageView) findViewById(R.id.backup_sent);
		backup_sent.setOnClickListener(this);
//		删除按钮
		delete_sent = (ImageView) findViewById(R.id.delete_sent);
		delete_sent.setOnClickListener(this);
//		发送按钮
		send_sent = (ImageView) findViewById(R.id.send_sent);
		send_sent.setOnClickListener(this);
//		title
		title_sent =(TextView) findViewById(R.id.title_sent);
		title_sent.setText(msg_time);
//		短信内容
		msg_content_sent=(TextView) findViewById(R.id.msg_content_sent);
		msg_content_sent.setText(msg_content);
//		列表
		receiver_sent = (ListView) findViewById(R.id.receiver_sent);
		receiver_sent.setAdapter(myAdapter);
	}

//点击事件监听函数
@Override
public void onClick(View arg0) {
	switch(arg0.getId())
	{
//	返回
	case R.id.backup_sent:
		SentDetailActivity.this.finish();
		break;
//	删除按钮
	case R.id.delete_sent:
		new AlertDialog.Builder(SentDetailActivity.this).setIcon(null).setTitle(null).setMessage("是否删除该短信？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				MainActivity.db.getReadableDatabase().execSQL("delete from sent where _id = ?",new String[]{_id});
				SentDetailActivity.this.finish();
			}
		}).setNegativeButton("取消", null).show();
		break;
//	发送按钮
	case R.id.send_sent:
		new AlertDialog.Builder(SentDetailActivity.this).setIcon(null).setTitle(null).setMessage("是否再次发送该信息？").setPositiveButton("立刻发送", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
//				显示进度条对话框
				progressDialog = ProgressDialog.show(SentDetailActivity.this, null, "正在发送短信....");
//				发送短信
				for(int i = 0 ; i <receiver_name.size();i++)
				{
//					创建一个PendingIntent
					PendingIntent pendingIntent = PendingIntent.getActivity(SentDetailActivity.this, 0, new Intent(), 0);
//					发送短信
					smsManager.sendTextMessage(receiver_number.get(i), null, receiver_name.get(i)+msg_content, pendingIntent, null);
				}
//				获得当前时间
				Calendar calendar = Calendar.getInstance();
				StringBuilder time = new StringBuilder();
//				分钟
				int m = calendar.get(Calendar.MINUTE);
				String minute = null;
				if(m<10)
				{
					minute = "0"+m;
				}
				else
				{
					minute = Integer.toString(m);
				}
				time.append(calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"  "+calendar.get(Calendar.HOUR_OF_DAY)+":"+minute);
//				将数据插入已发送数据表
				MainActivity.db.getReadableDatabase().execSQL("insert into sent values(null,?,?,?)",new String[]{msg_content,receiver,time.toString()});
//				取消进度条对话框
				progressDialog.dismiss();
//				提示短信发送成功
				Toast.makeText(SentDetailActivity.this, "短信已发送成功", Toast.LENGTH_LONG).show();
				SentDetailActivity.this.finish();
			}
		}).setNegativeButton("取消", null).show();
		
		
		break;
	}
}
	

//获得数据
private void getData()
{
	Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from sent where _id = ?", new String[]{_id});
	while(cursor.moveToNext())
	{
		msg_time = cursor.getString(cursor.getColumnIndex("time"));
		msg_content = cursor.getString(cursor.getColumnIndex("msg_content"));
		receiver = cursor.getString(cursor.getColumnIndex("receiver"));
		System.out.println(receiver);
	}
//	解析json
	try {
		JSONObject jsonObject = new JSONObject(receiver);
//		获得姓名数组
		JSONArray name_array = jsonObject.getJSONArray("name");
//		获得电话数组
		JSONArray number_array = jsonObject.getJSONArray("number");
		for(int i = 0 ;i <name_array.length();i++)
		{
			receiver_name.add(name_array.getString(i));
			receiver_number.add(number_array.getString(i));
		}
	} catch (JSONException e) {
		e.printStackTrace();
	}
}


//适配器
private class MyAdapter extends BaseAdapter
{

	@Override
	public int getCount() {
		return receiver_name.size();
	}

	@Override
	public Object getItem(int arg0) {
		return receiver_name.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listview_for_receiver, null);
//		获得姓名文本框
		TextView one_listitem_for_receiver = (TextView) ll.findViewById(R.id.one_listitem_for_receiver);
		one_listitem_for_receiver.setText(receiver_name.get(arg0));
//		获得电话文本框
		TextView two_listitem_for_receiver = (TextView) ll.findViewById(R.id.two_listitem_for_receiver);
		two_listitem_for_receiver.setText(receiver_number.get(arg0));
		return ll;
	}
	
}

}
