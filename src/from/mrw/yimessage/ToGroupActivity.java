package from.mrw.yimessage;

import java.util.ArrayList;

import from.mrw.yimssage.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ToGroupActivity extends Activity implements OnClickListener
{
	
//	用来装载群组对象的数组
	private ArrayList<ContactGroup> group_list = new ArrayList<ContactGroup>();
//	用来传递对象的数组
	private ArrayList<ContactGroup> intent_list = new ArrayList<ContactGroup>();
//	适配器
	private MyAdapter myAdapter = new MyAdapter();

	
//	listview
	private ListView listview_togroupactivity;
//	确定按钮
	private Button ok_togroupactivity;
//	取消按钮
	private Button cancel_togroupactivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.togroup_activity);
		
		
//		findviewbyid
		listview_togroupactivity = (ListView) findViewById(R.id.listview_togroupactivity);
		ok_togroupactivity = (Button) findViewById(R.id.ok_togroupactivity);
		ok_togroupactivity.setOnClickListener(this);
		cancel_togroupactivity = (Button) findViewById(R.id.cancel_togroupactivity);
		cancel_togroupactivity.setOnClickListener(this);
		
		
//		查询数据库
		query();
		
//		设置适配器
		listview_togroupactivity.setAdapter(myAdapter);
//		设置监听器
		listview_togroupactivity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				更改选中状态
				group_list.get(arg2).setChecked(!group_list.get(arg2).getChecked());
//				通知适配器更新
				myAdapter.notifyDataSetChanged();
			}
		});
		
	}
	
	
//	查询群组表函数
	private void query()
	{
//		查询数据库获得群组数据
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from contact_group", null);
		
//		清空数组
		group_list.clear();
		
		while(cursor.moveToNext())
		{
//			获得标题
			String title = cursor.getString(cursor.getColumnIndex("group_name"));
//			获得id
			String id = cursor.getString(cursor.getColumnIndex("_id"));
//			新建对象
			ContactGroup contactGroup = new ContactGroup();
			contactGroup.setTitle(title);
			contactGroup.setId(id);
//			将对象加入数组中
			group_list.add(contactGroup);
		}
		
		cursor.close();
	}
	
	

//	适配器
	private class MyAdapter extends BaseAdapter
	{

	@Override
	public int getCount() {
		return group_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return group_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listview_single, null);
		TextView textview_one =(TextView)ll.findViewById(R.id.textview);
		textview_one.setText(group_list.get(arg0).getTitle());
		CheckBox checkbox = (CheckBox) ll.findViewById(R.id.checkbox_one);
		checkbox.setChecked(group_list.get(arg0).getChecked());
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg, boolean arg1) {
				group_list.get(arg0).setChecked(arg1);
			}
		});
		return ll;
	}
		
	}



@Override
public void onClick(View arg0) {
	switch(arg0.getId())
	{
//	确定按钮
	case R.id.ok_togroupactivity:
//		获得intent
		Intent intent = getIntent();

		for(int i=0;i<group_list.size();i++)
		{
			if(group_list.get(i).getChecked())
			{
				intent_list.add(group_list.get(i));
			}
		}
		
		if(intent_list.size() == 0)
		{
			Toast.makeText(ToGroupActivity.this, "请选择群组", Toast.LENGTH_SHORT).show();
		}
		else
		{
			intent.putParcelableArrayListExtra("group", intent_list);
			ToGroupActivity.this.setResult(10,intent);
			ToGroupActivity.this.finish();
		}
		
		break;
//		取消
	case R.id.cancel_togroupactivity:
		ToGroupActivity.this.finish();
		break;
	
	}
}
	
	
	
}
