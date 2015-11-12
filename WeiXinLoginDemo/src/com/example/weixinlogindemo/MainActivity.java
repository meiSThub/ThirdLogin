package com.example.weixinlogindemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		regToWx();//微信登录初始化
	}

	@Override
	public void onClick(View v) {
		weixinLogin();
	}

	//微信
	private static final String WX_APP_ID="您应用的appid";
	private IWXAPI api;
	
	/*微信登录start*/
	private final SendAuth.Req req = new SendAuth.Req();
	
	private void weixinLogin() {
		if (api == null) {
			 api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
			 api.registerApp(WX_APP_ID);
	    }
        if (!api.isWXAppInstalled()) {
            // 提醒用户没有按照微信
        	Toast.makeText(this, "尊敬的用户你好，您手机上未检测到微信，请去安卓应用市场下载安装微信！", Toast.LENGTH_LONG).show();
            return;
        }
		Log.i("wx", "wx weixinLogin");
	    req.scope = "snsapi_userinfo";
	    req.state = "wechat_sdk_demo_test";
	    api.sendReq(req);
	}

	/**
	 * 微信登录初始化
	 */
	private void regToWx(){
		api=WXAPIFactory.createWXAPI(this, WX_APP_ID,true);//获取IWXAPI实例
		api.registerApp(WX_APP_ID);//将应用的api注册到微信
	}
	/*微信登录end*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
	}
}
