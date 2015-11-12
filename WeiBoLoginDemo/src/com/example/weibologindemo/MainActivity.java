package com.example.weibologindemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		regToWb();
	}

	@Override
	public void onClick(View v) {
		wbLogin();
	}

	// 微博
	private static final String WB_APP_ID = "您应用的appid";
	/**
	 * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
	 * 
	 * <p>
	 * 注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响， 但是没有定义将无法使用 SDK 认证登录。
	 * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
	 * </p>
	 */
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */
	private Oauth2AccessToken mAccessToken;
	private AuthInfo mAuthInfo;
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;

	/** 微博登录start **/
	private void regToWb() {//初始化
		// 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
		mAuthInfo = new AuthInfo(this, WB_APP_ID, REDIRECT_URL, SCOPE);
		mSsoHandler = new SsoHandler(this, mAuthInfo);
	}
	/**
	 * 登录
	 */
	private void wbLogin() {
		mSsoHandler.authorize(new AuthListener());
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			// 从这里获取用户输入的 电话号码信息
			// String phoneNum = mAccessToken.getPhoneNum();
			if (mAccessToken.isSessionValid()) {
				// 显示 Token
				// 保存 Token 到 SharedPreferences
				// AccessTokenKeeper.writeAccessToken(LoginActivity.this,
				// mAccessToken);
				Toast.makeText(MainActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
				String uid = mAccessToken.getUid();//即openid
//				thirdLogin(uid);//根据得到的uid进行用户登录,自己实现
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				String message = "授权失败";
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nObtained the code: " + code;
				}
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onCancel() {
			Toast.makeText(MainActivity.this, "取消授权", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(MainActivity.this,
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/** 微博登录end **/

}
