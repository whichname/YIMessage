package from.mrw.yimessage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	
//	建立草稿箱数据表
	private String drafts_sql = "create table drafts(_id integer primary key autoincrement,title,content)";
//	建立联系人群组数据表
	private String group_sql = "create table contact_group(_id integer primary key autoincrement,group_name)";
//	建立已发送数据表
	private String sent_sql = "create table sent(_id integer primary key autoincrement,msg_content,receiver,time)";
	

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
			arg0.execSQL(drafts_sql);
			arg0.execSQL(group_sql);
			arg0.execSQL(sent_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
