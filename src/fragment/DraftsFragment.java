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
	
//	编辑短信的textview
	private TextView add_draftsfragment;
//	显示草稿箱的listview
	private ListView drafts_list;
//	存储_id的数组
	private ArrayList<String> id_list = new ArrayList<String>();
	
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		获得布局
		View view = inflater.inflate(R.layout.draftsfragment, null);
//		绑定编辑短信
		add_draftsfragment=(TextView) view.findViewById(R.id.add_draftsfragment);
//		绑定监听器
		add_draftsfragment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				若activity实现了DraftsFragmentCallBack回调
				if(getActivity() instanceof DraftsFragmentCallBack)
				{
//					回调函数
					((DraftsFragmentCallBack) getActivity()).draftsfragment_callback(R.id.add_draftsfragment,null);
				}
			}
		});
		
//		绑定草稿箱
		drafts_list = (ListView) view.findViewById(R.id.drafts_listview);
		
		
		return view;
	}
	
	
	
	
	
@Override
	public void onResume() {
	
//	查询草稿箱数据表
	Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from drafts order by _id desc", null);
//	清空数组
	id_list.clear();
//	将查询结果放入数组中
	while(cursor.moveToNext())
	{
//		将id放入数组中
		id_list.add(cursor.getString(cursor.getColumnIndex("_id")));
	}
	
	
//	给列表项设置适配器
	drafts_list.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.listview_item_withoutcheck,cursor, new String[]{"title","content"}, new int[]{R.id.textview_one_withoutcheck,R.id.textview_two_withoutcheck} ));
//	给列表项设置监听器
	drafts_list.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
//			如果实现了接口
			if(getActivity() instanceof DraftsFragmentCallBack)
			{
//				回调函数
				((DraftsFragmentCallBack) getActivity()).draftsfragment_callback(R.id.drafts_listview,id_list.get(arg2));
			}
			
			
		}
	});
	
	super.onResume();
	}





	//	回调接口
	public interface DraftsFragmentCallBack
	{
//		点击事件回调
		public void draftsfragment_callback(int id,String _id);
	}
	
	
}
