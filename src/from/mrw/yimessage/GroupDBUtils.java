package from.mrw.yimessage;

import java.util.ArrayList;

import android.database.Cursor;

public class GroupDBUtils {
	
//	�½���ϵ��Ⱥ���
	public void CreateGroupTable(String _id)
	{
		MainActivity.db.getReadableDatabase().execSQL("create table group_"+_id+"(_id integer primary key autoincrement , id,sort_key)");
	}
	
//	��ѯ�Ƿ���ڸ�Ⱥ��
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
	
//	����Ⱥ�����ƻ�ȡid,���أ�_id
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
	
//	����Ⱥ��
	public void InsertGroup(String group_name)
	{
		MainActivity.db.getReadableDatabase().execSQL("insert into contact_group values(null,?)",new String[]{group_name});
	}
	
//	ɾ��Ⱥ��
	public void DeleteGroup(String _id)
	{
		MainActivity.db.getReadableDatabase().execSQL("delete from contact_group where _id = ?" ,new String[]{_id});
		MainActivity.db.getReadableDatabase().execSQL("drop table group_"+_id);
	}
	
//	�����ӦȺ���
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
	
//	���Ⱥ����������ϵ��id
	public ArrayList<String> GetContactsFromGroup(String group_id)
	{
		ArrayList<String> contact_id = new ArrayList<String>();
//		��ѯȺ����������ϵ��
		Cursor cursor  = MainActivity.db.getReadableDatabase().rawQuery("select * from group_"+group_id+" order by sort_key", null);
		while(cursor.moveToNext())
		{
			String id = cursor.getString(cursor.getColumnIndex("id"));
			contact_id.add(id);
		}
		cursor.close();
		return contact_id;
	}
	
//	��Ⱥ�����ɾ��ָ����ϵ��
	public void DeleteFromGroup(String group_id,String contact_id)
	{
		MainActivity.db.getReadableDatabase().execSQL("delete from group_"+group_id+" where id = ?",new String[]{contact_id});
		System.out.println("ad");
	}
	
	
}
