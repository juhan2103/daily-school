package com.daily_school.daily_school.ui

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.daily_school.daily_school.R
import com.daily_school.daily_school.databinding.ActivityLoginBinding
import com.daily_school.daily_school.utils.KakaoRef
import com.daily_school.daily_school.utils.NaverRef
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class LoginActivity : AppCompatActivity(){

    private lateinit var binding : ActivityLoginBinding

    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")

        // 카카오 로그인 초기 설정
        KakaoSdk.init(this, KakaoRef.APP_KEY)
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error == null) {
                    schoolInfoActivity()
                }
            }
        }

        // 네이버 로그인 초기 설정
        NaverIdLoginSDK.initialize(this,
            NaverRef.naverClientId,
            NaverRef.naverClientSecret,
            NaverRef.naverClientName
        )

        // 카카오 로그인 함수 호출
        kakaoLogin()
        // 네이버 로그인 함수 호출
        naverLogin()

    }

    private fun schoolInfoActivity(){
        startActivity(Intent(this, SchoolInfoActivity::class.java))
        finish()
    }

    // 카카오 로그인 함수
    private fun kakaoLogin(){
        // 카카오 로그인 버튼을 눌렀을 때
        binding.loginKakaoBtn.setOnClickListener {
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                    schoolInfoActivity()
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    }
                    // 카카오톡으로 로그인 성공하면 SchoolInfoActivity로 이동
                    else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                        schoolInfoActivity()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    // 네이버 로그인 함수
    private fun naverLogin(){
        // 네이버 로그인 버튼을 눌렀을 때
        binding.loginNaverBtn.setOnClickListener {
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    schoolInfoActivity()
                }
                // 로그인 실패 했을 때
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                }
                // 로그인 에러가 떴을 때
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }
            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }
    }
}