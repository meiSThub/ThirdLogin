package com.example.qqlogindemo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class MainActivity extends Activity implements OnClickListener{

	private Tencent mTencent;
	private UserInfo mInfo;
	
	private final String QQ_APP_ID = "您应用的appid";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		mTencent = Tencent.createInstance(QQ_APP_ID, this.getApplicationContext());//初始化
	}
	
	/***qq登录start***/
	private void qqLogin() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", listener);//调出授权登录的界面，并返回用户信息
		} else {
			mTencent.logout(this);
		}
	}
	/**
	 * qq授权成功之后的回调接口，并携带qq用户的相关信息,如需要的：openid
	 */
	IUiListener listener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {//授权成功后返回的json格式的用户信息
            try {
            	thirdLogin(values.getString("openid"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	/**
	 * 根据授权后得到的openid进行登录
	 * @param string
	 */
	private void thirdLogin(String openid) {
		Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
		//执行登录的网络操作，登录成后，跳转到首页，如果要做自动登录的话，需要保存openid
		//....................
	}
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			Log.i("tag", "qq_result"+response.toString());
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
		}

		@Override
		public void onCancel() {
		}
	}
	/**
	 * 获取用户的信息
	 */
	public void getUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError e) {
				}
				@Override
				public void onComplete(final Object response) {
				}

				@Override
				public void onCancel() {
				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);
		} else {
		}
	}
	
	/***qq登录end***/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Tencent.onActivityResultData(requestCode,resultCode,data,listener);
	}

	@Override
	public void onClick(View v) {
		qqLogin();
	}
}
