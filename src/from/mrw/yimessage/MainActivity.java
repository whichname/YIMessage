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
	
//	setting按钮
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
	
//	获得数据库
	public static DataBaseHelper db;
//	获得GroupDBUtils对象
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
    
    
//    检查是否第一次打开程序
    private void isFristOpen()
    {
    	sharedPreferences = getSharedPreferences("yimessage", Context.MODE_PRIVATE);
    	editor = sharedPreferences.edit();
    	boolean isFrist = sharedPreferences.getBoolean("isFrist", true);
    	if(isFrist)
    	{
    		db.getReadableDatabase().execSQL("insert into drafts values(null,?,?)",new String[]{"示例短信",",祝您新年快乐，万事如意"});
    		groupDBUtils.InsertGroup("家庭");
    		groupDBUtils.CreateGroupTable(groupDBUtils.NameToId("家庭"));
    		groupDBUtils.InsertGroup("朋友");
    		groupDBUtils.CreateGroupTable(groupDBUtils.NameToId("朋友"));
    		editor.putBoolean("isFrist", false);
    		editor.commit();
    	}
    	
    	
    	
    }
    
    
    
//    适配器
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
    
//   页面更改监听器
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

    
//    单选框监听
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


//	来自DraftsFragment的回调接口
	@Override
	public void draftsfragment_callback(int id,String _id) {
		Intent edit_intent = new Intent(MainActivity.this,EditMessageActivity.class);
		edit_intent.putExtra("_id", _id);
		startActivity(edit_intent);
			
	}
    
//	草稿箱
	public Cursor getDrafts()
	{
		Cursor cursor =db.getReadableDatabase().rawQuery("select * from drafts", null);
		return cursor;
	}


//	点击事件
@Override
public void onClick(View arg0) {
	
	switch (arg0.getId()) {
	case R.id.setting:
		Intent setting_intent = new Intent(MainActivity.this,SettingActivity.class);
		startActivity(setting_intent);
		break;
	}
	
	
}

//来自ContactsGroupFragment的回调接口
@Override
public void contactgroupfragment_callback(int id, String _id) {
	switch(id)
	{
//	点击了查看全部联系人
	case R.id.all_contactsgroupfragment:
		Intent all_contacts_intent = new Intent(MainActivity.this,ContactsActivity.class);
		all_contacts_intent.putExtra("switchId",((YIMssageApplication) MainActivity.this.getApplication()).getSeeAllContacts());
		startActivity(all_contacts_intent);
		break;
//	点击了添加分组
	case R.id.add_contactsgroupfragment:
		Intent add_group_intent = new Intent(MainActivity.this,InputTitleActivity.class);
		startActivityForResult(add_group_intent, 0x11);
		break;
//	点击了群组
	case R.id.contact_group_listview:
		Intent groupcontact_intent = new Intent(MainActivity.this,ContactsActivity.class);
		groupcontact_intent.putExtra("switchId", ((YIMssageApplication)getApplication()).getGroupContacts());
		groupcontact_intent.putExtra("id", _id);
		startActivity(groupcontact_intent);
	}
}


@Override
protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//	如果请求码是0x11
	if(arg0==0x11)
	{
		switch(arg1)
		{
//			返回码为0，点击了确定按钮
			case 0:
//				获得标题
				String title = arg2.getStringExtra("title");
//				插入
				groupDBUtils.InsertGroup(title);
//				创建表
				groupDBUtils.CreateGroupTable(groupDBUtils.NameToId(title));
				break;
//			返回码为1，点击了取消按钮
			case 1:
				break;
				
		}
	}
		
}


//来自已发送的回调
@Override
public void sentCallBack(String id) {
	Intent sent_intent = new Intent(MainActivity.this,SentDetailActivity.class);
	sent_intent.putExtra("_id", id);
	startActivity(sent_intent);
}



	

}
