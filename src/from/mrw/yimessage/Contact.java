package from.mrw.yimessage;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable
{

//	姓名
	private String display_name;
//	id
	private String contact_id;
//	电话
	private String number;
//	是否选中
	private Boolean checked;
//	sort_key
	private String sort_key;
	
//	构造函数
	public Contact()
	{
		display_name = null;
		contact_id = null;
		number = null;
		checked = false;
		sort_key = null;
	}
	
	public Contact(String contact_id,String display_name,String number)
	{
		this.contact_id = contact_id ;
		this.display_name = display_name ;
		this.number = number;
		
	}
	
	public String getDisplayName()
	{
		return this.display_name;
	}
	
	public void setDisplayName(String display_name)
	{
		this.display_name = display_name;
	}
	
	public String getContactId()
	{
		return this.contact_id;
	}
	
	public void setContactId(String contact_id)
	{
		this.contact_id = contact_id;
	}
	
	public String getNumber()
	{
		return this.number;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	public String getSortKey()
	{
		return this.sort_key;
	}
	
	public void setSortKey(String sort_key)
	{
		this.sort_key = sort_key;
	}
	
	public boolean getChecked()
	{
		return this.checked;
	}
	
	public void setChecked(Boolean checked)
	{
		this.checked = checked;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(contact_id);
		dest.writeString(display_name);
		dest.writeString(number);
	}
	
	public static final Parcelable.Creator<Contact> CREATOR = new Creator<Contact>() {
		
		@Override
		public Contact[] newArray(int size) {
			return new Contact[size];
		}
		
		@Override
		public Contact createFromParcel(Parcel source) {
			return new Contact(source.readString(),source.readString(),source.readString());
		}
	};
	
}
