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
	
//	���ذ�ť
	private ImageView backup_sent;
//	ɾ����ť
	private ImageView delete_sent;
//	���Ͱ�ť
	private ImageView send_sent;
//	title
	private TextView title_sent;
//	��������
	private TextView msg_content_sent;
//	�ռ����б��
	private ListView receiver_sent;
	
//	�ѷ��͵���Ϣ��_id
	private String _id;
	
	
//	��������
	private String msg_content = null;
//	����ʱ��
	private String msg_time = null;
//	�ռ��˵�json����
	private String receiver = null;
//	�ռ�������
	private ArrayList<String> receiver_name = new ArrayList<String>();
//	�ռ��˵绰
	private ArrayList<String> receiver_number = new ArrayList<String>();
	
//	������
	private MyAdapter myAdapter = new MyAdapter();
	
//	�������Ի���
	private ProgressDialog progressDialog ;
	
//	SmsManager
	private SmsManager smsManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sent);
		
//		���SmsManager
		smsManager = SmsManager.getDefault();
		
//		���_id
		_id = getIntent().getStringExtra("_id");
		
		getData();
		
		initview();
		
	}
	
	
//	��ʼ������
	private void initview()
	{
//		���ذ�ť
		backup_sent = (ImageView) findViewById(R.id.backup_sent);
		backup_sent.setOnClickListener(this);
//		ɾ����ť
		delete_sent = (ImageView) findViewById(R.id.delete_sent);
		delete_sent.setOnClickListener(this);
//		���Ͱ�ť
		send_sent = (ImageView) findViewById(R.id.send_sent);
		send_sent.setOnClickListener(this);
//		title
		title_sent =(TextView) findViewById(R.id.title_sent);
		title_sent.setText(msg_time);
//		��������
		msg_content_sent=(TextView) findViewById(R.id.msg_content_sent);
		msg_content_sent.setText(msg_content);
//		�б�
		receiver_sent = (ListView) findViewById(R.id.receiver_sent);
		receiver_sent.setAdapter(myAdapter);
	}

//����¼���������
@Override
public void onClick(View arg0) {
	switch(arg0.getId())
	{
//	����
	case R.id.backup_sent:
		SentDetailActivity.this.finish();
		break;
//	ɾ����ť
	case R.id.delete_sent:
		new AlertDialog.Builder(SentDetailActivity.this).setIcon(null).setTitle(null).setMessage("�Ƿ�ɾ���ö��ţ�").setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				MainActivity.db.getReadableDatabase().execSQL("delete from sent where _id = ?",new String[]{_id});
				SentDetailActivity.this.finish();
			}
		}).setNegativeButton("ȡ��", null).show();
		break;
//	���Ͱ�ť
	case R.id.send_sent:
		new AlertDialog.Builder(SentDetailActivity.this).setIcon(null).setTitle(null).setMessage("�Ƿ��ٴη��͸���Ϣ��").setPositiveButton("���̷���", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
//				��ʾ�������Ի���
				progressDialog = ProgressDialog.show(SentDetailActivity.this, null, "���ڷ��Ͷ���....");
//				���Ͷ���
				for(int i = 0 ; i <receiver_name.size();i++)
				{
//					����һ��PendingIntent
					PendingIntent pendingIntent = PendingIntent.getActivity(SentDetailActivity.this, 0, new Intent(), 0);
//					���Ͷ���
					smsManager.sendTextMessage(receiver_number.get(i), null, receiver_name.get(i)+msg_content, pendingIntent, null);
				}
//				��õ�ǰʱ��
				Calendar calendar = Calendar.getInstance();
				StringBuilder time = new StringBuilder();
//				����
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
//				�����ݲ����ѷ������ݱ�
				MainActivity.db.getReadableDatabase().execSQL("insert into sent values(null,?,?,?)",new String[]{msg_content,receiver,time.toString()});
//				ȡ���������Ի���
				progressDialog.dismiss();
//				��ʾ���ŷ��ͳɹ�
				Toast.makeText(SentDetailActivity.this, "�����ѷ��ͳɹ�", Toast.LENGTH_LONG).show();
				SentDetailActivity.this.finish();
			}
		}).setNegativeButton("ȡ��", null).show();
		
		
		break;
	}
}
	

//�������
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
//	����json
	try {
		JSONObject jsonObject = new JSONObject(receiver);
//		�����������
		JSONArray name_array = jsonObject.getJSONArray("name");
//		��õ绰����
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


//������
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
//		��������ı���
		TextView one_listitem_for_receiver = (TextView) ll.findViewById(R.id.one_listitem_for_receiver);
		one_listitem_for_receiver.setText(receiver_name.get(arg0));
//		��õ绰�ı���
		TextView two_listitem_for_receiver = (TextView) ll.findViewById(R.id.two_listitem_for_receiver);
		two_listitem_for_receiver.setText(receiver_number.get(arg0));
		return ll;
	}
	
}

}
