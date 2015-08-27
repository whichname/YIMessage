package fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import from.mrw.yimessage.GroupDBUtils;
import from.mrw.yimessage.MainActivity;
import from.mrw.yimssage.R;

public class ContactsGroupFragment extends Fragment implements OnClickListener
{
	
//	查看所有联系人
	private TextView all_contactsgroupfragment;
//	添加分组
	private TextView add_contactsgroupfragment;
//	显示分组的listview
	private ListView contact_group_listview;
//	存储群组_id的数组
	private ArrayList<String> id_list = new ArrayList<String>();
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contacts_group_fragment, null);
//		findviewbyid
		all_contactsgroupfragment = (TextView) view.findViewById(R.id.all_contactsgroupfragment);
		add_contactsgroupfragment = (TextView) view.findViewById(R.id.add_contactsgroupfragment);
		contact_group_listview = (ListView) view.findViewById(R.id.contact_group_listview);
		
//		绑定监听器
		all_contactsgroupfragment.setOnClickListener(this);
		add_contactsgroupfragment.setOnClickListener(this);
		
		
		return view;
	}
	
	
	

	@Override
	public void onResume() {
		super.onResume();
		
		getData();
		
//		给listview设置监听器
		contact_group_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				如果实现了接口
				if(getActivity() instanceof ContactsGroupFragmentCallBack)
				{
//					回调函数
					((ContactsGroupFragmentCallBack)getActivity()).contactgroupfragment_callback(R.id.contact_group_listview, id_list.get(arg2));
				}	
			}
		});
		
//		长按监听器，删除群组
		contact_group_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				new AlertDialog.Builder(getActivity()).setIcon(null).setTitle(null).setMessage("是否删除该群组？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg) {
//						删除操作
						new GroupDBUtils().DeleteGroup(id_list.get(arg2));
						getData();
					}
				}).setNegativeButton("取消", null).show();
				return true;
			}
		});
		
	}




	@Override
	public void onClick(View arg0) {
//			如果实现了接口
			if(getActivity() instanceof ContactsGroupFragmentCallBack)
			{
//				回调函数
				((ContactsGroupFragmentCallBack)getActivity()).contactgroupfragment_callback(arg0.getId(), null);
			}	
	}

//	回调接口
	public interface ContactsGroupFragmentCallBack
	{
//		点击事件回调函数
		public void contactgroupfragment_callback(int id,String _id);
	}
	
//	适配器
	private void getData()
	{
//		查询联系人群组表
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from contact_group", null);
//		清空数组
		id_list.clear();
//		将_id存入数组
		while(cursor.moveToNext())
		{
			id_list.add(cursor.getString(cursor.getColumnIndex("_id")));
		}
		
//		新建适配器
		final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_single_without_checkbox,cursor, new String[]{"group_name"}, new int[]{R.id.textview_without_checkbox} );
		
//		设置适配器
		contact_group_listview.setAdapter(simpleCursorAdapter);
	}
	
	
}
