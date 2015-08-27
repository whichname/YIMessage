package from.mrw.yimessage;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactGroup implements Parcelable
{

//	群组标题
	private String title;
//	群组id
	private String id;
//	群组联系人数量
	private int count;
//	选中状态
	private boolean checked;
	
//	构造函数
	public ContactGroup()
	{
		this.title = null;
		this.id = null;
		this.count = -1;
		this.checked = false;
	}
	
	public ContactGroup(String id , String title , int count)
	{
		this.id=id;
		this.title=title;
		this.count=count;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getId()
	{
		return this.id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	
	public int getCount()
	{
		return this.count;
	}
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public Boolean getChecked()
	{
		return this.checked;
	}
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeInt(count);
	}
	
	public static final Parcelable.Creator<ContactGroup> CREATOR = new Creator<ContactGroup>() {
		
		@Override
		public ContactGroup[] newArray(int size) {
			return new ContactGroup[size];
		}
		
		@Override
		public ContactGroup createFromParcel(Parcel source) {
			return new ContactGroup(source.readString(), source.readString(), source.readInt());
		}
	};
	
}
