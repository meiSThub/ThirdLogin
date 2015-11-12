package com.example.sharesdklogindemo;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.moments.WechatMoments;

public class MainActivity extends Activity implements OnClickListener ,PlatformActionListener,Callback{

	private Platform platform;

	
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ShareSDK.initSDK(this);//初始化
		
		handler = new Handler(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_qq:
			Platform qq = ShareSDK.getPlatform(QQ.NAME);
			authorize(qq);
			break;
		case R.id.btn_wx:
			Platform wechat = ShareSDK.getPlatform(WechatMoments.NAME);
			authorize(wechat);
			break;
		case R.id.btn_wb:
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(sina);
			break;

		default:
			break;
		}
	}
	
	private void authorize(Platform plat) {
		platform=plat;
		plat.setPlatformActionListener(this);
		// true不使用SSO授权，false使用SSO授权。使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
		plat.SSOSetting(true);
		// 获取用户资料
		plat.showUser(null);
	}

	//授权登录的回调函数start//
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR = 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	@Override
	public void onCancel(Platform arg0, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}
	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] { platform.getName(), res };
			handler.sendMessage(msg);
		}else {
			Toast.makeText(getApplicationContext(), "onComplete 授权失败", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onError(Platform arg0, int action, Throwable throwable) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		throwable.printStackTrace();
	}
	//授权登录的回调函数end//
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_AUTH_CANCEL: {
			// 取消授权
			Toast.makeText(MainActivity.this, "授权操作已取消", Toast.LENGTH_SHORT)
					.show();
			if (platform!=null) {
				platform.removeAccount();
				ShareSDK.removeCookieOnAuthorize(true);
			}
		}
			break;
		case MSG_AUTH_ERROR: {
			// 授权失败
			Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_SHORT)
					.show();
			if (platform!=null) {
				platform.removeAccount();
				ShareSDK.removeCookieOnAuthorize(true);
			}
		}
			break;
		case MSG_AUTH_COMPLETE: {
			// 授权成功
			Toast.makeText(MainActivity.this, "授权成功，正在跳转登录操作…",
					Toast.LENGTH_SHORT).show();
			Object[] objs = (Object[]) msg.obj;
			String platform = (String) objs[0];
			HashMap<String, Object> res = (HashMap<String, Object>) objs[1];//返回的数据
			Log.v("111", res.toString());
			//登录成功，携带相关数据进行跳转
		}
			break;
		}
		return false;
	}
	
}
