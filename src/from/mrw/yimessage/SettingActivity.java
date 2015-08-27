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
//	更新按钮
	private Button update_settingactivity;
//	url
	private String update_url = "http://git.oschina.net/mrwww/CallBackW/raw/master/msg_update.txt";
//	下载地址
	private String address = null;
//	更新信息
	private String update_msg = null;
	
//	DownloadManager
	private DownloadManager downloadManager;
//	DownloadManager.Request
	private DownloadManager.Request request;
	
	
//	用于访问网络的线程池
	ExecutorService threads = Executors.newFixedThreadPool(2);
	
//	进度条对话框
	private ProgressDialog progressDialog;
	
//	versionName
	private String versionName = null;
//	versionCode
	private int versionCode=0;
	
//	加载中
	public final  int LOADING = 73330;
//	加载完成
	private final  int DONE_LOAD = 73331;
//	有更新
	private final int HAS_UPDATE = 72230;
//	无更新
	private final int NO_UPDATE = 72231;
//	出错
	private final int ERROR = 71130;
	
	
//	handler
	private Handler myHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what)
			{
//			加载中
			case LOADING:
				progressDialog=ProgressDialog.show(SettingActivity.this,null, "正在检查更新....");
				break;
//			加载完成
			case DONE_LOAD:
				progressDialog.dismiss();
				break;
//			有更新
			case HAS_UPDATE:
				new AlertDialog.Builder(SettingActivity.this).setIcon(null).setTitle("发现新版本，是否立即更新？").setMessage(update_msg).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
//						下载软件
						downApp();
					}
				}).setNegativeButton("下次再说", null).show();
				break;
//			无更新
			case NO_UPDATE:
				Toast.makeText(SettingActivity.this, "当前已是最新版本", Toast.LENGTH_LONG).show();
				break;
//				出错
				case ERROR:
					progressDialog.dismiss();
					Toast.makeText(SettingActivity.this, "检查更新出错", Toast.LENGTH_SHORT).show();
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
	
//	初始化函数
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
		
//		获得versionName
		versionName = getVersionName();
//		获得versionCode
		versionCode = getVersionCode();
		
//		设置about
		about_settingactivity.setText(this.getResources().getText(R.string.local_version) + versionName +'\n' +this.getResources().getText(R.string.about_author));
		
	}

	
//	点击事件
	@Override
	public void onClick(View arg0) {
		
		switch(arg0.getId())
		{
//		返回按钮
		case R.id.backup_settingactivity:
			SettingActivity.this.finish();
			break;
//		更新程序
		case R.id.update_settingactivity:
//		分配线程检查更新
			checkUpdate();
			break;
		}
		
	}
	
//	检查更新
	private void checkUpdate()
	{
//		分配线程访问网络
		threads.execute(new Runnable() {
			
			@Override
			public void run() {
				try
				{
//				发送正在更新的消息
				myHandler.sendEmptyMessage(LOADING);
//				新建URL对象
				URL Url = new URL(update_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) Url.openConnection();
//				获得输入流
				InputStream inputStream = httpURLConnection.getInputStream();
//				每一行数据
				String line = null;
//				最后得到的数据
				StringBuilder data = new StringBuilder();
//				新建BufferedReader对象将输入流中数据提取出来
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//				提取输入流中数据
				while((line = bufferedReader.readLine()) != null)
				{
					data.append(line + "\n");
				}
//				关闭输入流
				inputStream.close();
				
//				获得jsonobject
				JSONObject jsonObject = new JSONObject(data.toString());
//				获得下载地址
				address = jsonObject.getString("address");
//				获得更新信息
				update_msg = jsonObject.getString("msg");
//				获得版本号
				int server_version = jsonObject.getInt("version");
				
//				发送检查更新完成消息
				myHandler.sendEmptyMessage(DONE_LOAD);
//				检查是否有更新
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
//					发送出错消息
					myHandler.sendEmptyMessage(ERROR);
				}
			}
		});
	}
	
	
//	下载函数
	private void downApp()
	{
//		获得downmanager
		downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
//		获得DownloadManager.Request
		request = new DownloadManager.Request(Uri.parse(address));
//		设置下载路径
		request.setDestinationInExternalPublicDir("YIMessage", "YIMessage.apk");
//		设置文件可被扫描
		request.allowScanningByMediaScanner();
//		设置通知栏标题
		request.setTitle("一短信更新");
//		设置通知栏内容
		request.setDescription("保存路径："+getPath());
//		设置通知栏可见
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//		开始下载,获得此次下载的id
		Long downId =  downloadManager.enqueue(request);
	}
	
//	创建文件夹函数
	private String getPath()
	{
		File sdDir = Environment.getExternalStoragePublicDirectory("YIMessage");
		String path = sdDir.getAbsolutePath() + "/YIMessage";
//		创建文件夹
		File file = new File(path);
		if(!file.exists())
		{
			file.mkdirs();
		}
		return path;
	}
	

//	获得当前版本
	private String getVersionName()
	{
		try {
			String versionName = null;
//			获得packageManager
			PackageManager packageManager = this.getPackageManager();
//			获得packageinfo
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
//			获得版本号
			versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "获取版本号失败";
		}
	}
	
//	获取versioncode
	private int getVersionCode()
	{
		try{
			int versionCode = 0 ;
//			获得packagemanager
			PackageManager packageManager = this.getPackageManager();
//			获得packageinfo
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
//			获得versioncode
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
