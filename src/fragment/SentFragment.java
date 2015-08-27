package fragment;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import from.mrw.yimessage.MainActivity;
import from.mrw.yimssage.R;

public class SentFragment extends Fragment {

//	listview
	private ListView sent_listview;
//	TextView
	private TextView no_sent_tv;
//	存储已发送短信_id的数组
	private ArrayList<String> id_list = new ArrayList<String>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.sent_fragment, null);
		
		sent_listview=(ListView)view.findViewById(R.id.sent_listview);
		no_sent_tv=(TextView)view.findViewById(R.id.no_sent_tv);
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
//		查询
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from sent order by _id DESC", null);
		
//		清空数组
		id_list.clear();
		
		int count=0;
//		若查询为空
		while(cursor.moveToNext())
		{
			count++;
			id_list.add(cursor.getString(cursor.getColumnIndex("_id")));
		}
//		如果没有数据
		if(count == 0)
		{
//			隐藏列表框
			sent_listview.setVisibility(View.GONE);
//			显示提示
			no_sent_tv.setVisibility(View.VISIBLE);
		}
		
//		显示
		sent_listview.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.listview_item_withoutcheck, cursor, new String[]{"msg_content","time"}, new int[]{R.id.textview_one_withoutcheck,R.id.textview_two_withoutcheck}));
		
//		设置监听器
		sent_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(getActivity() instanceof SentCallBack)
				{
					((SentCallBack) getActivity()).sentCallBack(id_list.get(arg2));
				}
			}
		});
		
	}

//	回调接口
	public interface SentCallBack
	{
		public void sentCallBack(String id);
	}
	
	
	
}
