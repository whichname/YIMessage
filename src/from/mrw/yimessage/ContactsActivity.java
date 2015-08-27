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
//	群组id
	private String group_id = null;
	
//	sidebar
	private SideBar sidebar;
//	textDialog
	private TextView textdialog_contacts_activity;
	

//	存储联系人的arraylist
	private ArrayList<Contact> contact_list = new ArrayList<Contact>();
//	存储群组的arraylist
	private ArrayList<ContactGroup> group_list = new ArrayList<ContactGroup>();
	
//	显示联系人的列表
	private ListView listview;
//	适配器
	private MyAdapter myAdapter = new MyAdapter();
	
//	Cursor
	private Cursor cursor = null;
	
//backup
	private ImageView backup_contactsactivity;
//	add
	private ImageView add_contactsactivity;
//	反选
	private TextView RS_contactsactivity;
//	delete
	private ImageView delete_contactsactivity;
	
//	线程池
	private ExecutorService threads = Executors.newFixedThreadPool(2);
	
//	进度条对话框
	private ProgressDialog progressDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_activity);
		
//		获得switchId
		switchId = getIntent().getIntExtra("switchId", -1);
		
		
//		findviewbyid
		findviewbyid();
//		进度条对话框
		progressDialog=ProgressDialog.show(ContactsActivity.this, null, "正在加载数据...");		
//		handler
		final Handler myHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 10)
				{
					contact_list=(ArrayList<Contact>) msg.obj;
//					加载入listview
					listview.setAdapter(myAdapter);
					progressDialog.dismiss();
				}
			}
			
		};
		
		
//		判断是查看全部联系人还是群组
		if(switchId == ((YIMssageApplication) getApplication()).getSeeAllContacts())
		{
			delete_contactsactivity.setVisibility(View.GONE);
//			启动多线程
			threads.execute(new Runnable() {
				
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=10;
//					获得联系人
					msg.obj = getContact();
					myHandler.sendMessage(msg);
				}
			});

		}
		else if (switchId == ((YIMssageApplication) getApplication()).getGroupContacts())
		{
			add_contactsactivity.setVisibility(View.GONE);
			group_id = getIntent().getStringExtra("id");
//			启动多线程
			threads.execute(new Runnable() {
				
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=10;
//					获得群组内联系人
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
		
//		反选
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
	
	
//	获得全部联系人的函数
	private ArrayList<Contact> getContact()
	{
		ArrayList<Contact> contact_list = new ArrayList<Contact>();
//		清空arraylist
		contact_list.clear();
		try
		{
			String id = " ";
			
//			获得uri
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//			查询，查询结果根据sort_key排序
			cursor = ContactsActivity.this.getContentResolver().query(uri, new String[]{"display_name","sort_key","contact_id","data1"}, null, null, "sort_key");
			if(cursor.moveToFirst())
			{
				do
				{
//					新建contact对象
					Contact contact = new Contact();
//					获得联系人姓名、sort_key、number、contact_id
					String display_name = cursor.getString(0);
//					sort_key
					String sort_key = null;
//					获得拼音首字母
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
//					设置对象的参数
					contact.setDisplayName(display_name);
					contact.setSortKey(sort_key);
//					contact.setNumber(number);
					contact.setContactId(contact_id);
//					将对象加入数组
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
//			关闭cursor
			cursor.close();
			return contact_list;
		}
	}
	
//	获得群组内联系人函数
	private ArrayList<Contact> getGroupContact(String group_id)
	{
//		获得群组内所有联系人
		ArrayList<String> id = new ArrayList<String>();
		id = new GroupDBUtils().GetContactsFromGroup(group_id);
		
		String contact_id_a = " ";
		
		ArrayList<Contact> contact_list = new ArrayList<Contact>();
//		清空联系人的arraylist
		contact_list.clear();
		for(int i = 0 ; i <id.size();i++)
		{
		Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"display_name","sort_key","contact_id","data1"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id.get(i), null, "sort_key");
		if(phone.moveToFirst())
		{
			do
			{
//				新建contact对象
				Contact contact = new Contact();
//				获得联系人姓名、sort_key、number、contact_id
				String display_name = phone.getString(0);
//				sort_key
				String sort_key=null;
//				获得拼音首字母
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
//				设置对象的参数
				contact.setDisplayName(display_name);
				contact.setSortKey(sort_key);
				contact.setContactId(contact_id);
//				将对象加入数组
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
//			获得线性布局
			LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listview_single, null);
//			获得主textview并设置联系人姓名
			TextView textview = (TextView) ll.findViewById(R.id.textview);
			textview.setText(contact_list.get(arg0).getDisplayName());
//			获得多选框并设置状态
			CheckBox checkbox_one = (CheckBox) ll.findViewById(R.id.checkbox_one);
			checkbox_one.setChecked(contact_list.get(arg0).getChecked());
//			多选框监听
			checkbox_one.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg, boolean arg1) {
//					设置响应对象的选择状态
					contact_list.get(arg0).setChecked(arg1);
				}
			});
//			显示字母的title
			TextView title = (TextView) ll.findViewById(R.id.title);
//			根据position获得setion
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

	

//	点击函数
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
//		返回按钮
		case R.id.backup_contactsactivity:
			ContactsActivity.this.finish();
			break;
//		添加按钮
		case R.id.add_contactsactivity:
			Intent togroup_intent = new Intent(ContactsActivity.this,ToGroupActivity.class);
			startActivityForResult(togroup_intent,10);
			break;
//		删除按钮
		case R.id.delete_contactsactivity:
//			弹出对话框询问是否移出群组表
			new AlertDialog.Builder(ContactsActivity.this).setTitle(null).setIcon(null).setMessage("是否将选定联系人移出该群组？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					遍历arrylist
					for(int i = 0 ;i<contact_list.size();i++)
					{
//						如果选中
						if(contact_list.get(i).getChecked())
						{
//							从群组表中删除该联系人
							new GroupDBUtils().DeleteFromGroup(group_id, contact_list.get(i).getContactId());
						}
					}
//					获得群组中联系人
					contact_list = getGroupContact(group_id);
//					通知设配器刷新
					myAdapter.notifyDataSetChanged();
				}
			}).setNegativeButton("取消", null).create().show();;

			break;
//		反选按钮
		case R.id.RS_contactsactivity: 
//			进度条对话框
			progressDialog=ProgressDialog.show(ContactsActivity.this, null, "正在加载数据...");
//			将数组中所有对象的选择状态反选
			for(int i = 0 ; i<contact_list.size();i++)
			{
				contact_list.get(i).setChecked(!contact_list.get(i).getChecked());
			}
//			取消进度条对话框
			progressDialog.dismiss();
//			通知适配器更新
			myAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		如果请求码为10
		if(requestCode == 10)
		{
//			如果返回码为10
			if(resultCode == 10)
			{
//				获得群组
				group_list = data.getParcelableArrayListExtra("group");
//				获取每个群组
				for(int i = 0 ; i <group_list.size() ; i++ )
				{
//					获取群组对象
					ContactGroup contactGroup = group_list.get(i);
					for(int j = 0 ; j<contact_list.size();j++)
					{
//						如果当前联系人选中
					if(contact_list.get(j).getChecked())
//						插入数据库
					new GroupDBUtils().InsertToGroup(contactGroup.getId(),contact_list.get(j).getContactId(),contact_list.get(j).getSortKey());
					}
				}
//				提示插入成功
				Toast.makeText(ContactsActivity.this, "插入成功", Toast.LENGTH_SHORT).show();
//				结束当前activity
				ContactsActivity.this.finish();
			}
		}
		
		
	}
	
	
	
	
	

}
