package com.example.weixinlogindemo.wxapi;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信授权登录后的回调
 * @author RandyXiong
 *
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler,
		OnClickListener {

	// 微信appid
	public static final String WX_APP_ID = "您应用的appid";
	public static final String WX_SECRET = "您应用的secret";

	public static String WEXIN_SCOPE = "";// 这里填的snsapi_userinfo，用snsapi_base提示没权限。
	public static String WEIXIN_STATE = "";// 用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验。
	public static String WX_CODE = "";
	public static String accessToken = "";// 调用凭证
	public static String openId = "";// 普通用户的标识，对当前开发者帐号唯一

	/*
	 * 获取个人信息 access_token="+ accessToken+ "&openid="+ openId;
	 */
	public static String userUrl = "https://api.weixin.qq.com/sns/userinfo";

	// IWXAPI 是第三方app和微信通信的openapi接口
	public static IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 注册微信
		api = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
		api.registerApp(WX_APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
		finish();
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	/**
	 * 授权登录，成功后会回调该方法
	 */
	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Bundle bundle = new Bundle();
			resp.toBundle(bundle);
			Resp sp = new Resp(bundle);
			WX_CODE = sp.code;//根据返回的数据，取得code，可以根据code得到accessToken凭证
			getAccessToken();
			finish();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(this, "被拒绝", Toast.LENGTH_LONG).show();
			break;
		default:
			Toast.makeText(this, "失败!", Toast.LENGTH_LONG).show();
			break;
		}
		finish();
	}

	/**
	 * @methods: 根据code获取用户调用凭证:accessToken
	 */
	private void getAccessToken() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
				//这里用XUtils框架来执行网络操作
				RequestParams params = new RequestParams();
				params.addBodyParameter("appid", WX_APP_ID);
				params.addBodyParameter("secret", WX_SECRET);
				params.addBodyParameter("code", WX_CODE);
				params.addBodyParameter("grant_type", "authorization_code");
				new HttpUtils().send(HttpMethod.POST, accessTokenUrl,
						params, new RequestCallBack<String>() {

							@Override
							public void onSuccess(ResponseInfo<String> responseInfo) {
								String tokenResult = responseInfo.result;
								if (null != tokenResult) {
									JSONObject tokenObj;
									try {
										tokenObj = new JSONObject(tokenResult);
										accessToken = tokenObj.optString("access_token");
										openId = tokenObj.optString("openid");
										thirdLogin(openId);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							@Override
							public void onFailure(com.lidroid.xutils.exception.HttpException e,
									String msg) {
								
							}
						});
			}
		}).start();
	}

	/**
	 * 第三方登录
	 */
	private void thirdLogin(final String openid) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("logisticsUserWechatRecognition", openid);
		String url="根据openid登录的后台接口地址";
		new HttpUtils().send(HttpMethod.POST,url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						try {
							String result = responseInfo.result;
							JSONObject object = new JSONObject(result);
							//授权登录成功跳转
							if (object.has("data")) {
//								Intent intent = new Intent(WXEntryActivity.this,FragmentHomeActivity.class);
//								startActivity(intent);
							} else {
//								Intent intent = new Intent(WXEntryActivity.this,BindingActivity.class);
//								intent.putExtra("openid", openid);
//								startActivity(intent);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(
							com.lidroid.xutils.exception.HttpException arg0,
							String arg1) {
						
					}
				});
	}

}
