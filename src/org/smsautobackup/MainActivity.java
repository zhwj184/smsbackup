package org.smsautobackup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		Date lastDate = new Date(curDate.getYear(), curDate.getMonth(),
				curDate.getDate() - 1);
		((EditText) findViewById(R.id.endDate)).setText(formatter
				.format(curDate));
		((EditText) findViewById(R.id.startDate)).setText(formatter
				.format(lastDate));
		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendSms(getSmsInPhone());
			}

		});
		Button btn1 = (Button) findViewById(R.id.sendLocation);
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendLocaltion();
			}

		});
	}
	
	private void sendLocaltion(){
		new LocationMonitor(MainActivity.this,this,getPhone()).run();
	}
	
	private String getPhone(){
		return  ((EditText) findViewById(R.id.phone)).getText().toString();
	}

	private String getSmsInPhone() {
		StringBuilder smsBuilder = new StringBuilder();
		EditText startDatePicker = (EditText) findViewById(R.id.startDate);
		EditText endDatePicker = (EditText) findViewById(R.id.endDate);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = df.parse(startDatePicker.getText().toString());
			Date endDate = df.parse(endDatePicker.getText().toString());
			ContentResolver cr = getContentResolver();
			return SmsUtil.getSmsInPhone(startDate, endDate, cr);
		}catch(Exception e){
			Log.d("Exception in getSmsInPhone", e.getMessage());
		}
		return "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
		ActivityManager activityMgr= (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		activityMgr.restartPackage(getPackageName());
	}

	private void sendSms(String content) {
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//		intent.setType("plain/text");
		intent.setType("message/rfc822") ; // 真机上使用这行
		EditText txtContent = (EditText) findViewById(R.id.editText1);
		String[] strEmailReciver = new String[] { txtContent.getText()
				.toString() };
		intent.putExtra(android.content.Intent.EXTRA_EMAIL, strEmailReciver); // 设置收件人
		EditText startDatePicker = (EditText) findViewById(R.id.startDate);
		EditText endDatePicker = (EditText) findViewById(R.id.endDate);
		intent.putExtra(Intent.EXTRA_SUBJECT, "["
				+ startDatePicker.getText().toString() + "至"
				+ endDatePicker.getText().toString() + "]短信备份");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, content); // 设置内容
		startActivity(Intent.createChooser(intent,
				"send SMS to your mail success"));
	}
}
