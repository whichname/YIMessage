package from.mrw.yimessage;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.pinyin4j.PinyinHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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

public class ContactsActivity extends Activity implements OnClickListener
{
//	switchId
	private int switchId ;
//	Ⱥ��id
	private String group_id = null;
	
//	sidebar
	private SideBar sidebar;
//	textDialog
	private TextView textdialog_contacts_activity;
	

//	�洢��ϵ�˵�arraylist
	private ArrayList<Contact> contact_list = new ArrayList<Contact>();
//	�洢Ⱥ���arraylist
	private ArrayList<ContactGroup> group_list = new ArrayList<ContactGroup>();
	
//	��ʾ��ϵ�˵��б�
	private ListView listview;
//	������
	private MyAdapter myAdapter = new MyAdapter();
	
//	Cursor
	private Cursor cursor = null;
	
//backup
	private ImageView backup_contactsactivity;
//	add
	private ImageView add_contactsactivity;
//	��ѡ
	private TextView RS_contactsactivity;
//	delete
	private ImageView delete_contactsactivity;
	
//	�̳߳�
	private ExecutorService threads = Executors.newFixedThreadPool(2);
	
//	�������Ի���
	private ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_activity);
		
//		���switchId
		switchId = getIntent().getIntExtra("switchId", -1);
		
		
//		findviewbyid
		findviewbyid();
//		�������Ի���
		progressDialog=ProgressDialog.show(ContactsActivity.this, null, "���ڼ�������...");		
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
					listview.setAdapter(myAdapter);
					progressDialog.dismiss();
				}
			}
			
		};
		
		
//		�ж��ǲ鿴ȫ����ϵ�˻���Ⱥ��
		if(switchId == ((YIMssageApplication) getApplication()).getSeeAllContacts())
		{
			delete_contactsactivity.setVisibility(View.GONE);
//			�������߳�
			threads.execute(new Runnable() {
				
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=10;
//					�����ϵ��
					msg.obj = getContact();
					myHandler.sendMessage(msg);
				}
			});

		}
		else if (switchId == ((YIMssageApplication) getApplication()).getGroupContacts())
		{
			add_contactsactivity.setVisibility(View.GONE);
			group_id = getIntent().getStringExtra("id");
//			�������߳�
			threads.execute(new Runnable() {
				
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=10;
//					���Ⱥ������ϵ��
					msg.obj = getGroupContact(group_id);
					myHandler.sendMessage(msg);
				}
			});
			
		}

	}
	
//	findviewbyid
	private void findviewbyid()
	{
		
//		backup
		backup_contactsactivity = (ImageView) findViewById(R.id.backup_contactsactivity);
		backup_contactsactivity.setOnClickListener(this);
		
//		add
		add_contactsactivity =(ImageView) findViewById(R.id.add_contactsactivity);
		add_contactsactivity.setOnClickListener(this);
		
//		��ѡ
		RS_contactsactivity = (TextView) findViewById(R.id.RS_contactsactivity);
		RS_contactsactivity.setOnClickListener(this);
		
//		delete
		delete_contactsactivity = (ImageView) findViewById(R.id.delete_contactsactivity);
		delete_contactsactivity.setOnClickListener(this);
		
//		listview
		listview = (ListView) findViewById(R.id.listview_contactsactivity);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

					Intent detail_intent = new Intent(ContactsActivity.this,DetailActivity.class);
					detail_intent.putExtra("title", contact_list.get(arg2).getDisplayName());
					detail_intent.putExtra("id", contact_list.get(arg2).getContactId());
					System.out.println(contact_list.get(arg2).getContactId());
					startActivity(detail_intent);
			}
		});
//		textdialog
		textdialog_contacts_activity = (TextView) findViewById(R.id.textdialog_contacts_activity);
		
//		sidebar
		sidebar = (SideBar) findViewById(R.id.sidebar_contacts_activity);
		sidebar.setTextView(textdialog_contacts_activity);
		sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = myAdapter.getPositionForSection(s.charAt(0));
				if(position != -1)
				{
					listview.setSelection(position);
				}
			}
		});
	}
	
	
//	���ȫ����ϵ�˵ĺ���
	private ArrayList<Contact> getContact()
	{
		ArrayList<Contact> contact_list = new ArrayList<Contact>();
//		���arraylist
		contact_list.clear();
		try
		{
			String id = " ";
			
//			���uri
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//			��ѯ����ѯ�������sort_key����
			cursor = ContactsActivity.this.getContentResolver().query(uri, new String[]{"display_name","sort_key","contact_id","data1"}, null, null, "sort_key");
			if(cursor.moveToFirst())
			{
				do
				{
//					�½�contact����
					Contact contact = new Contact();
//					�����ϵ��������sort_key��number��contact_id
					String display_name = cursor.getString(0);
//					sort_key
					String sort_key = null;
//					���ƴ������ĸ
					String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(display_name.charAt(0));
					if(pinyin == null)
					{
						sort_key = "#";
					}
					else
					{
						sort_key = pinyin[0];
					}
					
					
					String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
					if(contact_id.equals(id))
						continue;
//					���ö���Ĳ���
					contact.setDisplayName(display_name);
					contact.setSortKey(sort_key);
//					contact.setNumber(number);
					contact.setContactId(contact_id);
//					�������������
					contact_list.add(contact);
					id = contact_id;
				}
				while(cursor.moveToNext());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
//			�ر�cursor
			cursor.close();
			return contact_list;
		}
	}
	
//	���Ⱥ������ϵ�˺���
	private ArrayList<Contact> getGroupContact(String group_id)
	{
//		���Ⱥ����������ϵ��
		ArrayList<String> id = new ArrayList<String>();
		id = new GroupDBUtils().GetContactsFromGroup(group_id);
		
		String contact_id_a = " ";
		
		ArrayList<Contact> contact_list = new ArrayList<Contact>();
//		�����ϵ�˵�arraylist
		contact_list.clear();
		for(int i = 0 ; i <id.size();i++)
		{
		Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"display_name","sort_key","contact_id","data1"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id.get(i), null, "sort_key");
		if(phone.moveToFirst())
		{
			do
			{
//				�½�contact����
				Contact contact = new Contact();
//				�����ϵ��������sort_key��number��contact_id
				String display_name = phone.getString(0);
//				sort_key
				String sort_key=null;
//				���ƴ������ĸ
				String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(display_name.charAt(0));
				
				if(pinyin == null)
				{
					sort_key = "#";
				}
				else
				{
					sort_key = pinyin[0];
				}
				System.out.println(sort_key);
				String contact_id = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				if(contact_id.equals(contact_id_a))
					continue;
//				���ö���Ĳ���
				contact.setDisplayName(display_name);
				contact.setSortKey(sort_key);
				contact.setContactId(contact_id);
//				�������������
				contact_list.add(contact);
				contact_id_a = contact_id;
			}
			while(phone.moveToNext());
		}
		else
		{
			new GroupDBUtils().DeleteFromGroup(group_id, id.get(i));
		}
		phone.close();
		}
		return contact_list;
	}
	
	
	
	
	public class MyAdapter extends BaseAdapter implements SectionIndexer
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
//			������Բ���
			LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listview_single, null);
//			�����textview��������ϵ������
			TextView textview = (TextView) ll.findViewById(R.id.textview);
			textview.setText(contact_list.get(arg0).getDisplayName());
//			��ö�ѡ������״̬
			CheckBox checkbox_one = (CheckBox) ll.findViewById(R.id.checkbox_one);
			checkbox_one.setChecked(contact_list.get(arg0).getChecked());
//			��ѡ�����
			checkbox_one.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg, boolean arg1) {
//					������Ӧ�����ѡ��״̬
					contact_list.get(arg0).setChecked(arg1);
				}
			});
//			��ʾ��ĸ��title
			TextView title = (TextView) ll.findViewById(R.id.title);
//			����position���setion
			int setion = getSectionForPosition(arg0);
			if(arg0 == getPositionForSection(setion))
			{
				title.setText(contact_list.get(arg0).getSortKey().substring(0,1).toUpperCase());
				title.setVisibility(View.VISIBLE);
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

	

//	�������
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
//		���ذ�ť
		case R.id.backup_contactsactivity:
			ContactsActivity.this.finish();
			break;
//		��Ӱ�ť
		case R.id.add_contactsactivity:
			Intent togroup_intent = new Intent(ContactsActivity.this,ToGroupActivity.class);
			startActivityForResult(togroup_intent,10);
			break;
//		ɾ����ť
		case R.id.delete_contactsactivity:
//			�����Ի���ѯ���Ƿ��Ƴ�Ⱥ���
			new AlertDialog.Builder(ContactsActivity.this).setTitle(null).setIcon(null).setMessage("�Ƿ�ѡ����ϵ���Ƴ���Ⱥ�飿").setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					����arrylist
					for(int i = 0 ;i<contact_list.size();i++)
					{
//						���ѡ��
						if(contact_list.get(i).getChecked())
						{
//							��Ⱥ�����ɾ������ϵ��
							new GroupDBUtils().DeleteFromGroup(group_id, contact_list.get(i).getContactId());
						}
					}
//					���Ⱥ������ϵ��
					contact_list = getGroupContact(group_id);
//					֪ͨ������ˢ��
					myAdapter.notifyDataSetChanged();
				}
			}).setNegativeButton("ȡ��", null).create().show();;

			break;
//		��ѡ��ť
		case R.id.RS_contactsactivity: 
//			�������Ի���
			progressDialog=ProgressDialog.show(ContactsActivity.this, null, "���ڼ�������...");
//			�����������ж����ѡ��״̬��ѡ
			for(int i = 0 ; i<contact_list.size();i++)
			{
				contact_list.get(i).setChecked(!contact_list.get(i).getChecked());
			}
//			ȡ���������Ի���
			progressDialog.dismiss();
//			֪ͨ����������
			myAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		���������Ϊ10
		if(requestCode == 10)
		{
//			���������Ϊ10
			if(resultCode == 10)
			{
//				���Ⱥ��
				group_list = data.getParcelableArrayListExtra("group");
//				��ȡÿ��Ⱥ��
				for(int i = 0 ; i <group_list.size() ; i++ )
				{
//					��ȡȺ�����
					ContactGroup contactGroup = group_list.get(i);
					for(int j = 0 ; j<contact_list.size();j++)
					{
//						�����ǰ��ϵ��ѡ��
					if(contact_list.get(j).getChecked())
//						�������ݿ�
					new GroupDBUtils().InsertToGroup(contactGroup.getId(),contact_list.get(j).getContactId(),contact_list.get(j).getSortKey());
					}
				}
//				��ʾ����ɹ�
				Toast.makeText(ContactsActivity.this, "����ɹ�", Toast.LENGTH_SHORT).show();
//				������ǰactivity
				ContactsActivity.this.finish();
			}
		}
		
		
	}
	
	
	
	
	

}
