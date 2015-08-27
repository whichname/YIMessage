package from.mrw.yimessage;

import java.util.ArrayList;

import android.R.anim;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import fragment.ContactsGroupFragment;
import fragment.ContactsGroupFragment.ContactsGroupFragmentCallBack;
import fragment.DraftsFragment;
import fragment.DraftsFragment.DraftsFragmentCallBack;
import fragment.SentFragment;
import fragment.SentFragment.SentCallBack;
import from.mrw.yimssage.R;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener , DraftsFragmentCallBack,OnClickListener,ContactsGroupFragmentCallBack,SentCallBack
{
	
//	setting��ť
	private ImageView setting;
	
//	divider
	private TextView divider_one;
	private TextView divider_two;
	
//	RadioGroup
	private RadioGroup radiogroup;
	
//	RadioButton
	private RadioButton drafts;
	private RadioButton contacts_group;
	private RadioButton sent;
	
//	ViewPager
	private ViewPager viewpager;
	
//	Fragment
	private DraftsFragment draftsFragment;
	private ContactsGroupFragment contactsGroupFragment;
	private SentFragment sentFragment;
	
//	Fragments' ArrayList
	private ArrayList<Fragment> fragments ;
	
//	������ݿ�
	public static DataBaseHelper db;
//	���GroupDBUtils����
	private static GroupDBUtils groupDBUtils = new GroupDBUtils();
	
//	SharedPreferences
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initview();
        
        
        
    }
    
    
    private void initview()
    {
    	setting = (ImageView) findViewById(R.id.setting);
    	setting.setOnClickListener(this);
    	
    	divider_one = (TextView) findViewById(R.id.divider_one);
    	divider_two = (TextView) findViewById(R.id.divider_two);
    	
    	radiogroup = (RadioGroup)findViewById(R.id.radiogroup);
    	radiogroup.setOnCheckedChangeListener(this);
    	
    	drafts = (RadioButton) findViewById(R.id.drafts);
    	contacts_group = (RadioButton) findViewById(R.id.contacts_group);
    	sent = (RadioButton) findViewById(R.id.sent);
    	
    	if(draftsFragment == null)
    	{
    		draftsFragment = new DraftsFragment();
    	}
    	if(contactsGroupFragment == null)
    	{
    		contactsGroupFragment = new ContactsGroupFragment();
    	}
    	if(sentFragment == null)
    	{
    		sentFragment = new SentFragment();
    	}
    	
    	fragments = new ArrayList<Fragment>();
    	
    	fragments.add(draftsFragment);
    	fragments.add(contactsGroupFragment);
    	fragments.add(sentFragment);
    	
    	viewpager = (ViewPager) findViewById(R.id.viewpager);
    	viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments));
    	viewpager.setCurrentItem(0);
    	viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
    	
    	db = new DataBaseHelper(MainActivity.this, "YIMessage.db3", null, 1);
    	
    	isFristOpen();
    	
    }
    
    
//    ����Ƿ��һ�δ򿪳���
    private void isFristOpen()
    {
    	sharedPreferences = getSharedPreferences("yimessage", Context.MODE_PRIVATE);
    	editor = sharedPreferences.edit();
    	boolean isFrist = sharedPreferences.getBoolean("isFrist", true);
    	if(isFrist)
    	{
    		db.getReadableDatabase().execSQL("insert into drafts values(null,?,?)",new String[]{"ʾ������",",ף��������֣���������"});
    		groupDBUtils.InsertGroup("��ͥ");
    		groupDBUtils.CreateGroupTable(groupDBUtils.NameToId("��ͥ"));
    		groupDBUtils.InsertGroup("����");
    		groupDBUtils.CreateGroupTable(groupDBUtils.NameToId("����"));
    		editor.putBoolean("isFrist", false);
    		editor.commit();
    	}
    	
    	
    	
    }
    
    
    
//    ������
    public class MyPagerAdapter extends FragmentPagerAdapter
    {
    	ArrayList<Fragment> list;
		public MyPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) 
		{
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}
    	
    }
    
//   ҳ����ļ�����
    public class MyOnPageChangeListener implements  OnPageChangeListener
    {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			int current = viewpager.getCurrentItem();
			switch(current)
			{
			case 0:
				radiogroup.check(R.id.drafts);
				break;
			case 1:
				radiogroup.check(R.id.contacts_group);
				break;
			case 2:
				radiogroup.check(R.id.sent);
				break;
			
			}
			
		}
    	
    	
    }

    
//    ��ѡ�����
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		int current = 0;
		switch(arg1)
		{
		case R.id.drafts:
			divider_one.setVisibility(View.GONE);
			divider_two.setVisibility(View.VISIBLE);
			current = 0;
			break;
		case R.id.contacts_group:
			divider_one.setVisibility(View.GONE);
			divider_two.setVisibility(View.GONE);
			current = 1;
			break;
		case R.id.sent:
			divider_one.setVisibility(View.VISIBLE);
			divider_two.setVisibility(View.GONE);
			current = 2;
			break;
		}
		if(viewpager.getCurrentItem() != current)
		{
			viewpager.setCurrentItem(current);
		}
		
		
	}


//	����DraftsFragment�Ļص��ӿ�
	@Override
	public void draftsfragment_callback(int id,String _id) {
		Intent edit_intent = new Intent(MainActivity.this,EditMessageActivity.class);
		edit_intent.putExtra("_id", _id);
		startActivity(edit_intent);
			
	}
    
//	�ݸ���
	public Cursor getDrafts()
	{
		Cursor cursor =db.getReadableDatabase().rawQuery("select * from drafts", null);
		return cursor;
	}


//	����¼�
@Override
public void onClick(View arg0) {
	
	switch (arg0.getId()) {
	case R.id.setting:
		Intent setting_intent = new Intent(MainActivity.this,SettingActivity.class);
		startActivity(setting_intent);
		break;
	}
	
	
}

//����ContactsGroupFragment�Ļص��ӿ�
@Override
public void contactgroupfragment_callback(int id, String _id) {
	switch(id)
	{
//	����˲鿴ȫ����ϵ��
	case R.id.all_contactsgroupfragment:
		Intent all_contacts_intent = new Intent(MainActivity.this,ContactsActivity.class);
		all_contacts_intent.putExtra("switchId",((YIMssageApplication) MainActivity.this.getApplication()).getSeeAllContacts());
		startActivity(all_contacts_intent);
		break;
//	�������ӷ���
	case R.id.add_contactsgroupfragment:
		Intent add_group_intent = new Intent(MainActivity.this,InputTitleActivity.class);
		startActivityForResult(add_group_intent, 0x11);
		break;
//	�����Ⱥ��
	case R.id.contact_group_listview:
		Intent groupcontact_intent = new Intent(MainActivity.this,ContactsActivity.class);
		groupcontact_intent.putExtra("switchId", ((YIMssageApplication)getApplication()).getGroupContacts());
		groupcontact_intent.putExtra("id", _id);
		startActivity(groupcontact_intent);
	}
}


@Override
protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//	�����������0x11
	if(arg0==0x11)
	{
		switch(arg1)
		{
//			������Ϊ0�������ȷ����ť
			case 0:
//				��ñ���
				String title = arg2.getStringExtra("title");
//				����
				groupDBUtils.InsertGroup(title);
//				������
				groupDBUtils.CreateGroupTable(groupDBUtils.NameToId(title));
				break;
//			������Ϊ1�������ȡ����ť
			case 1:
				break;
				
		}
	}
		
}


//�����ѷ��͵Ļص�
@Override
public void sentCallBack(String id) {
	Intent sent_intent = new Intent(MainActivity.this,SentDetailActivity.class);
	sent_intent.putExtra("_id", id);
	startActivity(sent_intent);
}



	

}
