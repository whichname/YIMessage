package from.mrw.yimessage;

import android.app.Application;

public class YIMssageApplication extends Application {
	
//	����鿴������ϵ��������activity
	private final static int SEE_ALL_CONTACTS = 75535;
	public int getSeeAllContacts()
	{
		return this.SEE_ALL_CONTACTS;
	}
	
	
//	�����Ⱥ��������activity
	private final static int GROUP_CONTACTS = 75534;
	public int getGroupContacts()
	{
		return this.GROUP_CONTACTS;
	}
	
}
