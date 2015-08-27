package fragment;


import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import from.mrw.yimessage.MainActivity;
import from.mrw.yimssage.R;

public class DraftsFragment extends Fragment 
{
	
//	�༭���ŵ�textview
	private TextView add_draftsfragment;
//	��ʾ�ݸ����listview
	private ListView drafts_list;
//	�洢_id������
	private ArrayList<String> id_list = new ArrayList<String>();
	
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		��ò���
		View view = inflater.inflate(R.layout.draftsfragment, null);
//		�󶨱༭����
		add_draftsfragment=(TextView) view.findViewById(R.id.add_draftsfragment);
//		�󶨼�����
		add_draftsfragment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				��activityʵ����DraftsFragmentCallBack�ص�
				if(getActivity() instanceof DraftsFragmentCallBack)
				{
//					�ص�����
					((DraftsFragmentCallBack) getActivity()).draftsfragment_callback(R.id.add_draftsfragment,null);
				}
			}
		});
		
//		�󶨲ݸ���
		drafts_list = (ListView) view.findViewById(R.id.drafts_listview);
		
		
		return view;
	}
	
	
	
	
	
@Override
	public void onResume() {
	
//	��ѯ�ݸ������ݱ�
	Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from drafts order by _id desc", null);
//	�������
	id_list.clear();
//	����ѯ�������������
	while(cursor.moveToNext())
	{
//		��id����������
		id_list.add(cursor.getString(cursor.getColumnIndex("_id")));
	}
	
	
//	���б�������������
	drafts_list.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.listview_item_withoutcheck,cursor, new String[]{"title","content"}, new int[]{R.id.textview_one_withoutcheck,R.id.textview_two_withoutcheck} ));
//	���б������ü�����
	drafts_list.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
//			���ʵ���˽ӿ�
			if(getActivity() instanceof DraftsFragmentCallBack)
			{
//				�ص�����
				((DraftsFragmentCallBack) getActivity()).draftsfragment_callback(R.id.drafts_listview,id_list.get(arg2));
			}
			
			
		}
	});
	
	super.onResume();
	}





	//	�ص��ӿ�
	public interface DraftsFragmentCallBack
	{
//		����¼��ص�
		public void draftsfragment_callback(int id,String _id);
	}
	
	
}
