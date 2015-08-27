package from.mrw.yimessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import from.mrw.yimssage.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener{
	
//	backup
	private ImageView backup_settingactivity;
//	about
	private TextView about_settingactivity;
//	���°�ť
	private Button update_settingactivity;
//	url
	private String update_url = "http://git.oschina.net/mrwww/CallBackW/raw/master/msg_update.txt";
//	���ص�ַ
	private String address = null;
//	������Ϣ
	private String update_msg = null;
	
//	DownloadManager
	private DownloadManager downloadManager;
//	DownloadManager.Request
	private DownloadManager.Request request;
	
	
//	���ڷ���������̳߳�
	ExecutorService threads = Executors.newFixedThreadPool(2);
	
//	�������Ի���
	private ProgressDialog progressDialog;
	
//	versionName
	private String versionName = null;
//	versionCode
	private int versionCode=0;
	
//	������
	public final  int LOADING = 73330;
//	�������
	private final  int DONE_LOAD = 73331;
//	�и���
	private final int HAS_UPDATE = 72230;
//	�޸���
	private final int NO_UPDATE = 72231;
//	����
	private final int ERROR = 71130;
	
	
//	handler
	private Handler myHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
//			������
			case LOADING:
				progressDialog=ProgressDialog.show(SettingActivity.this,null, "���ڼ�����....");
				break;
//			�������
			case DONE_LOAD:
				progressDialog.dismiss();
				break;
//			�и���
			case HAS_UPDATE:
				new AlertDialog.Builder(SettingActivity.this).setIcon(null).setTitle("�����°汾���Ƿ��������£�").setMessage(update_msg).setPositiveButton("��������", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
//						�������
						downApp();
					}
				}).setNegativeButton("�´���˵", null).show();
				break;
//			�޸���
			case NO_UPDATE:
				Toast.makeText(SettingActivity.this, "��ǰ�������°汾", Toast.LENGTH_LONG).show();
				break;
//				����
				case ERROR:
					progressDialog.dismiss();
					Toast.makeText(SettingActivity.this, "�����³���", Toast.LENGTH_SHORT).show();
					break;
			}
		}
		
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		
		initview();
	}
	
//	��ʼ������
	private void initview()
	{
//		backup
		backup_settingactivity = (ImageView) findViewById(R.id.backup_settingactivity);
		backup_settingactivity.setOnClickListener(this);
//		textview
		about_settingactivity = (TextView) findViewById(R.id.about_settingactivity);
//		update
		update_settingactivity = (Button) findViewById(R.id.update_settingactivity);
		update_settingactivity.setOnClickListener(this);
		
//		���versionName
		versionName = getVersionName();
//		���versionCode
		versionCode = getVersionCode();
		
//		����about
		about_settingactivity.setText(this.getResources().getText(R.string.local_version) + versionName +'\n' +this.getResources().getText(R.string.about_author));
		
	}

	
//	����¼�
	@Override
	public void onClick(View arg0) {
		
		switch(arg0.getId())
		{
//		���ذ�ť
		case R.id.backup_settingactivity:
			SettingActivity.this.finish();
			break;
//		���³���
		case R.id.update_settingactivity:
//		�����̼߳�����
			checkUpdate();
			break;
		}
		
	}
	
//	������
	private void checkUpdate()
	{
//		�����̷߳�������
		threads.execute(new Runnable() {
			
			@Override
			public void run() {
				try
				{
//				�������ڸ��µ���Ϣ
				myHandler.sendEmptyMessage(LOADING);
//				�½�URL����
				URL Url = new URL(update_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) Url.openConnection();
//				���������
				InputStream inputStream = httpURLConnection.getInputStream();
//				ÿһ������
				String line = null;
//				���õ�������
				StringBuilder data = new StringBuilder();
//				�½�BufferedReader������������������ȡ����
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//				��ȡ������������
				while((line = bufferedReader.readLine()) != null)
				{
					data.append(line + "\n");
				}
//				�ر�������
				inputStream.close();
				
//				���jsonobject
				JSONObject jsonObject = new JSONObject(data.toString());
//				������ص�ַ
				address = jsonObject.getString("address");
//				��ø�����Ϣ
				update_msg = jsonObject.getString("msg");
//				��ð汾��
				int server_version = jsonObject.getInt("version");
				
//				���ͼ����������Ϣ
				myHandler.sendEmptyMessage(DONE_LOAD);
//				����Ƿ��и���
				if(versionCode < server_version)
				{
					myHandler.sendEmptyMessage(HAS_UPDATE);
				}
				else
				{
					myHandler.sendEmptyMessage(NO_UPDATE);
				}
				
				
				
				}
				catch(Exception e)
				{
					e.printStackTrace();
//					���ͳ�����Ϣ
					myHandler.sendEmptyMessage(ERROR);
				}
			}
		});
	}
	
	
//	���غ���
	private void downApp()
	{
//		���downmanager
		downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
//		���DownloadManager.Request
		request = new DownloadManager.Request(Uri.parse(address));
//		��������·��
		request.setDestinationInExternalPublicDir("YIMessage", "YIMessage.apk");
//		�����ļ��ɱ�ɨ��
		request.allowScanningByMediaScanner();
//		����֪ͨ������
		request.setTitle("һ���Ÿ���");
//		����֪ͨ������
		request.setDescription("����·����"+getPath());
//		����֪ͨ���ɼ�
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//		��ʼ����,��ô˴����ص�id
		Long downId =  downloadManager.enqueue(request);
	}
	
//	�����ļ��к���
	private String getPath()
	{
		File sdDir = Environment.getExternalStoragePublicDirectory("YIMessage");
		String path = sdDir.getAbsolutePath() + "/YIMessage";
//		�����ļ���
		File file = new File(path);
		if(!file.exists())
		{
			file.mkdirs();
		}
		return path;
	}
	

//	��õ�ǰ�汾
	private String getVersionName()
	{
		try {
			String versionName = null;
//			���packageManager
			PackageManager packageManager = this.getPackageManager();
//			���packageinfo
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
//			��ð汾��
			versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "��ȡ�汾��ʧ��";
		}
	}
	
//	��ȡversioncode
	private int getVersionCode()
	{
		try{
			int versionCode = 0 ;
//			���packagemanager
			PackageManager packageManager = this.getPackageManager();
//			���packageinfo
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
//			���versioncode
			versionCode = packageInfo.versionCode;
			return versionCode;
		}
		catch(NameNotFoundException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	
	
}
