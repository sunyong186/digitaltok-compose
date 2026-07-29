package com.yourcompany.digitaltok.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordResetActivity : AppCompatActivity() {

    private lateinit var btnBackIcon: ImageView
    private lateinit var tvTopTitle: TextView

    private lateinit var etResetEmail: EditText
    private lateinit var btnSendLink: Button
    private lateinit var btnCancel: Button
    private lateinit var tvResetStatus: TextView

    private var isEmailValid = false

    // Repository
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        btnBackIcon = findViewById(R.id.btnBackIcon)
        tvTopTitle = findViewById(R.id.tvTopTitle)

        etResetEmail = findViewById(R.id.etResetEmail)
        btnSendLink = findViewById(R.id.btnSendLink)
        btnCancel = findViewById(R.id.btnCancel)
        tvResetStatus = findViewById(R.id.tvResetStatus)

        // 상단 타이틀
        tvTopTitle.text = "이메일로 로그인"

        // 뒤로가기
        btnBackIcon.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        // 시작은 비활성
        setSendEnabled(false)
        tvResetStatus.visibility = View.GONE

        etResetEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = etResetEmail.text.toString().trim()
                isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
                setSendEnabled(isEmailValid)
                if (!isEmailValid) tvResetStatus.visibility = View.GONE
            }
        })

        btnSendLink.setOnClickListener {
            val email = etResetEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requestPasswordReset(email)
        }
    }

    private fun requestPasswordReset(email: String) {
        // 로딩 느낌: 버튼 비활성
        btnSendLink.isEnabled = false

        lifecycleScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    authRepository.resetPassword(email)
                }

                if (!res.isSuccessful) {
                    val msg = "요청 실패 (HTTP ${res.code()})"
                    tvResetStatus.visibility = View.VISIBLE
                    tvResetStatus.text = msg
                    Toast.makeText(this@PasswordResetActivity, msg, Toast.LENGTH_SHORT).show()
                    setSendEnabled(isEmailValid)
                    return@launch
                }

                val body = res.body()
                if (body == null) {
                    val msg = "응답이 비어있어요."
                    tvResetStatus.visibility = View.VISIBLE
                    tvResetStatus.text = msg
                    Toast.makeText(this@PasswordResetActivity, msg, Toast.LENGTH_SHORT).show()
                    setSendEnabled(isEmailValid)
                    return@launch
                }

                if (body.isSuccess == true) {
                    val msg = body.result ?: body.message ?: "재설정 요청이 완료됐어요."
                    tvResetStatus.visibility = View.VISIBLE
                    tvResetStatus.text = msg
                    Toast.makeText(this@PasswordResetActivity, msg, Toast.LENGTH_SHORT).show()
                } else {
                    val msg = body.message ?: "요청 실패"
                    tvResetStatus.visibility = View.VISIBLE
                    tvResetStatus.text = msg
                    Toast.makeText(this@PasswordResetActivity, msg, Toast.LENGTH_SHORT).show()
                }

                // 성공/실패 후 버튼 상태 복구
                setSendEnabled(isEmailValid)

            } catch (e: Exception) {
                val msg = "네트워크 오류: ${e.message}"
                tvResetStatus.visibility = View.VISIBLE
                tvResetStatus.text = msg
                Toast.makeText(this@PasswordResetActivity, msg, Toast.LENGTH_SHORT).show()
                setSendEnabled(isEmailValid)
            }
        }
    }

    private fun setSendEnabled(enabled: Boolean) {
        btnSendLink.isEnabled = enabled
        btnSendLink.setBackgroundResource(
            if (enabled) R.drawable.bg_btn_blue else R.drawable.bg_btn_gray
        )
    }
}
