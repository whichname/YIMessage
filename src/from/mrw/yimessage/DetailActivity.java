package from.mrw.yimessage;

import java.util.ArrayList;

import from.mrw.yimssage.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailActivity extends Activity {
	
	
	private String title = null;
	private String id = null;
	
	private ArrayList<String> phone_list = new ArrayList<String>();
	
	
	private TextView title_detail_dialog;
	private ListView listview_detail_dialog;
	private Button ok_detail_dialog;
	private ProgressBar progressbar_detail_dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_dialog);
		
		title = getIntent().getStringExtra("title");
		id = getIntent().getStringExtra("id");
		
		title_detail_dialog = (TextView) findViewById(R.id.title_detail_dialog);
		listview_detail_dialog=(ListView)findViewById(R.id.listview_detail_dialog);
		ok_detail_dialog=(Button) findViewById(R.id.ok_detail_dialog);
		ok_detail_dialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DetailActivity.this.finish();
			}
		});
		progressbar_detail_dialog=(ProgressBar) findViewById(R.id.progressbar_detail_dialog);
		
		title_detail_dialog.setText(title);
//		查询特定联系人
		Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"data1"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id, null, null);
		
		phone_list.clear();
		while(phone.moveToNext())
		{
			String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			phone_list.add(number);
		}
		phone.close();
		
		listview_detail_dialog.setAdapter(new MyAdapter());
		
		progressbar_detail_dialog.setVisibility(View.GONE);
		
	}
	
	private class MyAdapter extends BaseAdapter
	{

		@Override
		public int getCount() {
			return phone_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return phone_list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.listview_single, null);
			TextView textview = (TextView) ll.findViewById(R.id.textview);
			textview.setText(phone_list.get(arg0));
			CheckBox checkbox_one = (CheckBox) ll.findViewById(R.id.checkbox_one);
			checkbox_one.setVisibility(View.GONE);
			return ll;
		}
		
	}
	
	

}
