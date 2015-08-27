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
	
//	�鿴������ϵ��
	private TextView all_contactsgroupfragment;
//	��ӷ���
	private TextView add_contactsgroupfragment;
//	��ʾ�����listview
	private ListView contact_group_listview;
//	�洢Ⱥ��_id������
	private ArrayList<String> id_list = new ArrayList<String>();
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contacts_group_fragment, null);
//		findviewbyid
		all_contactsgroupfragment = (TextView) view.findViewById(R.id.all_contactsgroupfragment);
		add_contactsgroupfragment = (TextView) view.findViewById(R.id.add_contactsgroupfragment);
		contact_group_listview = (ListView) view.findViewById(R.id.contact_group_listview);
		
//		�󶨼�����
		all_contactsgroupfragment.setOnClickListener(this);
		add_contactsgroupfragment.setOnClickListener(this);
		
		
		return view;
	}
	
	
	

	@Override
	public void onResume() {
		super.onResume();
		
		getData();
		
//		��listview���ü�����
		contact_group_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				���ʵ���˽ӿ�
				if(getActivity() instanceof ContactsGroupFragmentCallBack)
				{
//					�ص�����
					((ContactsGroupFragmentCallBack)getActivity()).contactgroupfragment_callback(R.id.contact_group_listview, id_list.get(arg2));
				}	
			}
		});
		
//		������������ɾ��Ⱥ��
		contact_group_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				new AlertDialog.Builder(getActivity()).setIcon(null).setTitle(null).setMessage("�Ƿ�ɾ����Ⱥ�飿").setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg) {
//						ɾ������
						new GroupDBUtils().DeleteGroup(id_list.get(arg2));
						getData();
					}
				}).setNegativeButton("ȡ��", null).show();
				return true;
			}
		});
		
	}




	@Override
	public void onClick(View arg0) {
//			���ʵ���˽ӿ�
			if(getActivity() instanceof ContactsGroupFragmentCallBack)
			{
//				�ص�����
				((ContactsGroupFragmentCallBack)getActivity()).contactgroupfragment_callback(arg0.getId(), null);
			}	
	}

//	�ص��ӿ�
	public interface ContactsGroupFragmentCallBack
	{
//		����¼��ص�����
		public void contactgroupfragment_callback(int id,String _id);
	}
	
//	������
	private void getData()
	{
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
		final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_single_without_checkbox,cursor, new String[]{"group_name"}, new int[]{R.id.textview_without_checkbox} );
		
//		����������
		contact_group_listview.setAdapter(simpleCursorAdapter);
	}
	
	
}
