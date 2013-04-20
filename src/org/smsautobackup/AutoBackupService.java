package org.smsautobackup;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AutoBackupService extends Service {
	
	public static String receiver = "";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override  
    public void onCreate() {  
    }  
  
    @Override  
    public void onDestroy() {  
    }  
  
    @Override  
    public void onStart(Intent intent, int startid) {
//    	long time = 24 * 60 * 60 * 1000;
    	long time = 10 * 1000;
    	while(true){
    		try{
    			Thread.sleep(time);
    			sendSms();
//    			Toast.makeText(this, "My Service Stoped", Toast.LENGTH_LONG).show();  
    		}catch(Exception e){
    			
    		}
    	}
    }  
    
	private void sendSms() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		Date lastDate = new Date(curDate.getYear(), curDate.getMonth(),
				curDate.getDate() - 1);
		String startDate = formatter.format(curDate);
		String endDate = formatter.format(lastDate);
		String content = SmsUtil.getSmsInPhone(lastDate, curDate, this.getContentResolver());
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("plain/text");
		// intent.setType("message/rfc822") ; // 真机上使用这行
		String[] strEmailReciver = new String[] { receiver};
		intent.putExtra(android.content.Intent.EXTRA_EMAIL, strEmailReciver); // 设置收件人
		intent.putExtra(Intent.EXTRA_SUBJECT, "["+ startDate + "至" + endDate + "]短信备份");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, content); // 设置内容
		startActivity(Intent.createChooser(intent,
				"send SMS to your mail success"));
	}

}
