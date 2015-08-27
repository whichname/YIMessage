package from.mrw.yimessage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class BaseActivity extends Activity {

	protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			finish();
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter intentFilter = new IntentFilter("from.mrw.yimessage.exit");
		registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	protected void Exit()
	{
		sendBroadcast(new Intent("from.mrw.yimessage.exit"));
	}
	
}
