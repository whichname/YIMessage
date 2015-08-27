package from.mrw.yimessage;

import java.util.ArrayList;

import from.mrw.yimssage.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SelectGroupActivity extends BaseActivity implements OnClickListener
{
	
//	backup
	private ImageView backup_selectgroup_activity;
	
//	��������
	private String msg_content;
	
//	�鿴������ϵ��
	private TextView all_selectgroup_activity;
	
//	Ⱥ���б�
	private ListView listview_selectgroup_activity;
	
//	�洢Ⱥ��_id������
	private ArrayList<String> id_list = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectgroup_activity);
		
//		��ȡ��������
		msg_content=getIntent().getStringExtra("msg_content");
		
		initview();
		
		
	}

	private void initview()
	{
//		����
		backup_selectgroup_activity=(ImageView)findViewById(R.id.backup_selectgroup_activity);
		backup_selectgroup_activity.setOnClickListener(this);
//		�鿴������ϵ��
		all_selectgroup_activity = (TextView) findViewById(R.id.all_selectgroup_activity);
		all_selectgroup_activity.setOnClickListener(this);
//		Ⱥ���б�
		listview_selectgroup_activity = (ListView) findViewById(R.id.listview_selectgroup_activity);
		
		
//		��ѯ��ϵ��Ⱥ���
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from contact_group", null);
//		�������
		id_list.clear();
//		��_id��������
		while(cursor.moveToNext())
		{
			id_list.add(cursor.getString(cursor.getColumnIndex("_id")));
		}
		
//		�½�������
		final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(SelectGroupActivity.this, R.layout.listview_single_without_checkbox,cursor, new String[]{"group_name"}, new int[]{R.id.textview_without_checkbox} );
		
//		����������
		listview_selectgroup_activity.setAdapter(simpleCursorAdapter);
//		���õ���¼�������
		listview_selectgroup_activity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent group_intent = new Intent(SelectGroupActivity.this,SelectContactActivity.class);
				group_intent.putExtra("msg_content", msg_content);
				group_intent.putExtra("switchId", ((YIMssageApplication)getApplication()).getGroupContacts());
				group_intent.putExtra("id", id_list.get(arg2));
				startActivityForResult(group_intent, 0x12);
			}
		});
		
	}

	@Override
	public void onClick(View arg0) {
		
		switch(arg0.getId())
		{
		case R.id.backup_selectgroup_activity:
			SelectGroupActivity.this.finish();
			break;
		case R.id.all_selectgroup_activity:
			Intent all_contacts_intent = new Intent(SelectGroupActivity.this,SelectContactActivity.class);
			all_contacts_intent.putExtra("msg_content", msg_content);
			all_contacts_intent.putExtra("switchId",((YIMssageApplication) SelectGroupActivity.this.getApplication()).getSeeAllContacts());
			startActivity(all_contacts_intent);
			break;
		}
		
	}
}
