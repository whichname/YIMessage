package from.mrw.yimessage;

import java.util.ArrayList;

import android.database.Cursor;

public class GroupDBUtils {
	
//	新建联系人群组表
	public void CreateGroupTable(String _id)
	{
		MainActivity.db.getReadableDatabase().execSQL("create table group_"+_id+"(_id integer primary key autoincrement , id,sort_key)");
	}
	
//	查询是否存在该群组
	public boolean ifGroupExist(String group_name)
	{
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from contact_group where group_name = ?", new String[]{group_name});
		int i = 0;
		while(cursor.moveToNext())
		{
			i++;
		}
		if(i==0)
		{
			return false;
		}
		return true;
	}
	
//	根据群组名称获取id,返回：_id
	public String NameToId(String group_name)
	{
		String id = null;
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from contact_group where group_name = ?", new String[]{group_name});
		while(cursor.moveToNext())
		{
			id = cursor.getString(cursor.getColumnIndex("_id"));
		}
		return id;
	}
	
//	插入群组
	public void InsertGroup(String group_name)
	{
		MainActivity.db.getReadableDatabase().execSQL("insert into contact_group values(null,?)",new String[]{group_name});
	}
	
//	删除群组
	public void DeleteGroup(String _id)
	{
		MainActivity.db.getReadableDatabase().execSQL("delete from contact_group where _id = ?" ,new String[]{_id});
		MainActivity.db.getReadableDatabase().execSQL("drop table group_"+_id);
	}
	
//	插入对应群组表
	public void InsertToGroup(String _id , String id,String sort_key)
	{
		String group_name = "group_" + _id;
		Cursor cursor = MainActivity.db.getReadableDatabase().rawQuery("select * from " +group_name +" where id = ? ", new String[]{id});
		int i = 0 ;
		while(cursor.moveToNext())
		{
			i++;
		}
		if(i==0)
		{
			MainActivity.db.getReadableDatabase().execSQL("insert into " + group_name +" values(null,?,?)",new String[]{id,sort_key});
		}
	}
	
//	获得群组中所有联系人id
	public ArrayList<String> GetContactsFromGroup(String group_id)
	{
		ArrayList<String> contact_id = new ArrayList<String>();
//		查询群组中所有联系人
		Cursor cursor  = MainActivity.db.getReadableDatabase().rawQuery("select * from group_"+group_id+" order by sort_key", null);
		while(cursor.moveToNext())
		{
			String id = cursor.getString(cursor.getColumnIndex("id"));
			contact_id.add(id);
		}
		cursor.close();
		return contact_id;
	}
	
//	从群组表中删除指定联系人
	public void DeleteFromGroup(String group_id,String contact_id)
	{
		MainActivity.db.getReadableDatabase().execSQL("delete from group_"+group_id+" where id = ?",new String[]{contact_id});
		System.out.println("ad");
	}
	
	
}
