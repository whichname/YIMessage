 package from.mrw.yimessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.pinyin4j.PinyinHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import from.mrw.yimessage.SideBar.OnTouchingLetterChangedListener;
import from.mrw.yimssage.R;

public class SelectContactActivity extends BaseActivity implements OnClickListener{
	
//	switchId
	private int switchId ;
//	Ⱥ��id
	private String group_id = null;
//	��������
	private String msg_content;
	
//	�̳߳�
	private ExecutorService threads = Executors.newFixedThreadPool(2);

//	backup
	private ImageView backup_selectcontact_activity;
//	ok
	private TextView ok_selectcontact_activity;
//	��ѡ
	private TextView RS_selectcontact_activity;
//	sidebar
	private SideBar sidebar;
//	textDialog
	private TextView textdialog_selectcontact_activity;
	
	
//	SmsManager
	private SmsManager smsManager;
//	��ϵ���б�
	private ListView listview_selectcontact_activity;
//	��ϵ������
	private ArrayList<Contact> contact_list = new ArrayList<Contact>();
//	������
	private MyAdapter myAdapter = new MyAdapter();
//	�������Ի���
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcontact_activity);
//		��ö�������
		msg_content=getIntent().getStringExtra("msg_content");
//		��ȡSmsManager
		smsManager = SmsManager.getDefault();
		
//		���switchId
		switchId = getIntent().getIntExtra("switchId", -1);
//		��ʼ������
		initview();
		
//		�����������Ի���
		progressDialog = ProgressDialog.show(SelectContactActivity.this, null, "���ڼ�������...");
		
		
//		handler
		final Handler myHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 10)
				{
					contact_list=(ArrayList<Contact>) msg.obj;
//					������listview
					listview_selectcontact_activity.setAdapter(myAdapter);
					progressDialog.dismiss();
				}
			}
			
		};
		
//		�ж��ǲ鿴ȫ����ϵ�˻���Ⱥ��
		if(switchId == ((YIMssageApplication) getApplication()).getSeeAllContacts())
		{
//			�������߳�
			threads.execute(new Runnable() {
				
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=10;
//					���Ⱥ������ϵ��
					msg.obj = getContact("all");
					myHandler.sendMessage(msg);
				}
			});

		}
		else if (switchId == ((YIMssageApplication) getApplication()).getGroupContacts())
		{
			group_id = getIntent().getStringExtra("id");
			System.out.println(group_id);
//			�������߳�
			threads.execute(new Runnable() {
				
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=10;
//					���Ⱥ������ϵ��
					msg.obj = getContact(group_id);
					myHandler.sendMessage(msg);
				}
			});
	}
		
	}

//	��ʼ������
	private void initview()
	{
//		backup
		backup_selectcontact_activity=(ImageView) findViewById(R.id.backup_selectcontact_activity);
		backup_selectcontact_activity.setOnClickListener(this);
//		ok
		ok_selectcontact_activity=(TextView) findViewById(R.id.ok_selectcontact_activity);
		ok_selectcontact_activity.setOnClickListener(this);
//		��ѡ
		RS_selectcontact_activity=(TextView) findViewById(R.id.RS_selectcontact_activity);
		RS_selectcontact_activity.setOnClickListener(this);
		
//		��ϵ���б�
		listview_selectcontact_activity=(ListView) findViewById(R.id.listview_selectcontact_activity);
		listview_selectcontact_activity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				contact_list.get(arg2).setChecked(!contact_list.get(arg2).getChecked());
				myAdapter.notifyDataSetChanged();
			}
		});
//		textdialog
		textdialog_selectcontact_activity = (TextView) findViewById(R.id.textdialog_selectcontact_activity);
		
		
//		sidebar
		sidebar = (SideBar) findViewById(R.id.sidebar_selectcontact_activity);
		sidebar.setTextView(textdialog_selectcontact_activity);
		sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = myAdapter.getPositionForSection(s.charAt(0));
				if(position != -1)
				{
					listview_selectcontact_activity.setSelection(position);
				}
			}
		});
		
	}

//	����¼�
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
//		backup
		case R.id.backup_selectcontact_activity:
			SelectContactActivity.this.finish();
			break;
//			����
		case R.id.ok_selectcontact_activity:
			new AlertDialog.Builder(SelectContactActivity.this).setIcon(null).setTitle(null).setMessage("�Ƿ��Ͷ��ţ�").setPositiveButton("��������", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					progressDialog=ProgressDialog.show(SelectContactActivity.this, null, "���ڷ��Ͷ���.....");
//					���ڴ洢������
					StringBuilder receiver = new StringBuilder();
					receiver.append("{\"name\":[");
//					����ϵ�˵����ִ���
					for(int i = 0 ; i<contact_list.size();i++)
					{
						if(contact_list.get(i).getChecked())
						{
							receiver.append("\""+contact_list.get(i).getDisplayName()+"\",");
						}
					}
					receiver.substring(0, receiver.length()-2);
					receiver.append("],\"number\":[");
//					���ڴ洢ʱ��
					StringBuilder time = new StringBuilder();
					for(int i = 0 ; i<contact_list.size();i++)
					{
//						�����ϵ�˱�ѡ��
						if(contact_list.get(i).getChecked())
						{
//						��ϵ������
						receiver.append("\""+contact_list.get(i).getNumber()+"\",");	
//						����һ��PendingIntent
						PendingIntent pendingIntent = PendingIntent.getActivity(SelectContactActivity.this, 0, new Intent(), 0);
//						���Ͷ���
						smsManager.sendTextMessage(contact_list.get(i).getNumber(), null, contact_list.get(i).getDisplayName()+msg_content, pendingIntent, null);
						}
					}
					receiver.substring(0, receiver.length()-2);
					receiver.append("]}");
//					Calendar
					Calendar calendar = Calendar.getInstance();
//					����
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
//					�����ݲ����ѷ������ݱ�
					MainActivity.db.getReadableDatabase().execSQL("insert into sent values(null,?,?,?)",new String[]{msg_content,receiver.toString(),time.toString()});
					progressDialog.dismiss();
					Toast.makeText(SelectContactActivity.this, "�����ѷ��ͳɹ�", Toast.LENGTH_LONG).show();
					Exit();
				}
			}).setNegativeButton("ȡ��", null).show();
			break;
//			��ѡ��ť
		case R.id.RS_selectcontact_activity:
			for(int i = 0 ;i <contact_list.size();i++)
			{
				contact_list.get(i).setChecked(!contact_list.get(i).getChecked());
			}
			myAdapter.notifyDataSetChanged();
			break;
		}
}	
//	
	private ArrayList<Contact> getContact(String group_id)
	{
//		�洢��ϵ�˵�����
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		contacts.clear();
//		����ǲ鿴ȫ����ϵ��
		if(group_id.equals("all"))
		{
//			���uri
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//			��ѯ����ѯ�������sort_key����
			Cursor cursor = getContentResolver().query(uri, new String[]{"display_name","sort_key","contact_id","data1"}, null, null, "sort_key");
			if(cursor.moveToFirst())
			{
				do
				{
//					�½�contact����
					Contact contact = new Contact();
//					�����ϵ��������sort_key��number��contact_id
					String display_name = cursor.getString(0);
					String sort_key;
//					ƴ��
					String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(display_name.charAt(0));
//					�������Ӣ�Ļ���
					if(pinyin == null)
					{
						sort_key = "#";
					}
					else
					{
						sort_key = pinyin[0];
					}
					String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//					���ö���Ĳ���
					contact.setDisplayName(display_name);
					contact.setSortKey(sort_key);
					contact.setNumber(number);
					contact.setContactId(contact_id);
//					�������������
					contacts.add(contact);
				}
				while(cursor.moveToNext());
			}
			cursor.close();
		}
//		������ҵ���Ⱥ�����ϵ��
		else 
		{
//			���Ⱥ����������ϵ��
			ArrayList<String> id = new ArrayList<String>();
			id = new GroupDBUtils().GetContactsFromGroup(group_id);
			for(int i = 0 ; i <id.size();i++)
			{
			Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"display_name","sort_key","contact_id","data1"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id.get(i), null, "sort_key");
			if(phone.moveToFirst())
			{
				do
				{
//					�½�contact����
					Contact contact = new Contact();
//					�����ϵ��������sort_key��number��contact_id
					String display_name = phone.getString(0);
					String sort_key;
//					ƴ��
					String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(display_name.charAt(0));
//					�������Ӣ�Ļ���
					if(pinyin == null)
					{
						sort_key = "#";
					}
					else
					{
						sort_key = pinyin[0];
					}
					String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					String contact_id = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//					���ö���Ĳ���
					contact.setDisplayName(display_name);
					contact.setSortKey(sort_key);
					contact.setContactId(contact_id);
					contact.setNumber(number);
//					�������������
					contacts.add(contact);
				}
				while(phone.moveToNext());
			}
			else
			{
				new GroupDBUtils().DeleteFromGroup(group_id, id.get(i));
			}
			phone.close();
		}
	}
		return contacts;
	}
	
	
	
	
//	������
	private class MyAdapter extends BaseAdapter implements SectionIndexer
	{

	@Override
	public int getCount() {
		return contact_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return contact_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
//		������Բ���
		LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listview_item, null);
//		���textone
		TextView textview_one = (TextView) ll.findViewById(R.id.textview_one);
		textview_one.setText(contact_list.get(arg0).getDisplayName());
//		���texttwo
		TextView textview_two = (TextView)ll.findViewById(R.id.textview_two);
		textview_two.setText(contact_list.get(arg0).getNumber());
//		��ö�ѡ��
		CheckBox checkbox = (CheckBox) ll.findViewById(R.id.checkbox);
		checkbox.setChecked(contact_list.get(arg0).getChecked());
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg, boolean arg1) {
				contact_list.get(arg0).setChecked(arg1);
			}
		});
//		�����ʾ��ĸ���ı���
		TextView title_one = (TextView) ll.findViewById(R.id.title_one);
//		��õ�ǰ��������ĸ
		int setion = getSectionForPosition(arg0) ;
//		�����ǰλ���Ƿ�������ĸ��һ�γ��ֵĵط�
		if(arg0 == getPositionForSection(setion))
		{
			title_one.setText(contact_list.get(arg0).getSortKey().substring(0,1).toUpperCase());
			title_one.setVisibility(View.VISIBLE);
		}
		return ll;
	}

	@Override
	public int getPositionForSection(int arg0) {
		for(int i = 0 ; i<getCount();i++)
		{
			if(contact_list.get(i).getSortKey().toUpperCase().charAt(0) == arg0)
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		return contact_list.get(arg0).getSortKey().toUpperCase().charAt(0);
	}

	@Override
	public Object[] getSections() {
		return null;
	}
		
	}
	
}
