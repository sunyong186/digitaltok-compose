package com.yourcompany.digitaltok.ui.auth

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private val GREEN = Color.parseColor("#00C950")
    private val RED = Color.parseColor("#FB2C36")

    private var isEmailChecked = false
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isPasswordMatch = false

    private lateinit var btnBack: ImageView
    private lateinit var tvGoLogin: TextView
    private lateinit var etEmail: EditText
    private lateinit var btnCheckEmail: Button
    private lateinit var tvEmailStatus: TextView
    private lateinit var etPassword: EditText
    private lateinit var tvPwStatus: TextView
    private lateinit var etPasswordConfirm: EditText
    private lateinit var tvPwConfirmStatus: TextView
    private lateinit var cbTerms1: CheckBox
    private lateinit var cbTerms2: CheckBox
    private lateinit var cbTerms3: CheckBox
    private lateinit var btnSignup: Button

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        bindViews()

        // background 강제 적용되게
        btnCheckEmail.backgroundTintList = null
        btnSignup.backgroundTintList = null

        btnBack.setOnClickListener { finish() }
        tvGoLogin.setOnClickListener { finish() }

        setupRequiredTagColor()
        setupInitialUi()
        setupListeners()
        updateSignupButton()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        tvGoLogin = findViewById(R.id.tvGoLogin)
        etEmail = findViewById(R.id.etEmail)
        btnCheckEmail = findViewById(R.id.btnCheckEmail)
        tvEmailStatus = findViewById(R.id.tvEmailStatus)
        etPassword = findViewById(R.id.etPassword)
        tvPwStatus = findViewById(R.id.tvPwStatus)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        tvPwConfirmStatus = findViewById(R.id.tvPwConfirmStatus)
        cbTerms1 = findViewById(R.id.cbTerms1)
        cbTerms2 = findViewById(R.id.cbTerms2)
        cbTerms3 = findViewById(R.id.cbTerms3)
        btnSignup = findViewById(R.id.btnSignup)
    }

    private fun setupInitialUi() {
        // 기본: 중복확인(파랑) / 이메일 유효할 때만 enabled
        btnCheckEmail.isEnabled = false
        btnCheckEmail.text = "중복확인"
        btnCheckEmail.setBackgroundResource(R.drawable.bg_btn_blue)
        btnCheckEmail.setTextColor(Color.WHITE)

        tvEmailStatus.visibility = View.GONE
        tvPwStatus.visibility = View.GONE
        tvPwConfirmStatus.visibility = View.GONE

        btnSignup.isEnabled = false
        btnSignup.setBackgroundResource(R.drawable.bg_btn_gray)
        btnSignup.setTextColor(Color.WHITE)
    }

    private fun setupRequiredTagColor() {
        setRequiredColor(cbTerms1)
        setRequiredColor(cbTerms2)
    }

    private fun setRequiredColor(cb: CheckBox) {
        val text = cb.text.toString()
        val required = "[필수]"
        val start = text.indexOf(required)
        if (start == -1) return
        val end = start + required.length

        val spannable = SpannableString(text)
        spannable.setSpan(
            ForegroundColorSpan(RED),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        cb.text = spannable
    }

    private fun resetEmailCheckState() {
        isEmailChecked = false
        btnCheckEmail.text = "중복확인"
        btnCheckEmail.setBackgroundResource(R.drawable.bg_btn_blue)
        tvEmailStatus.visibility = View.GONE
    }

    private fun setupListeners() {
        etEmail.addTextChangedListener(SimpleTextWatcher { raw ->
            val email = raw.trim()

            // 이메일 수정하면 다시 중복확인(파랑) 상태로
            resetEmailCheckState()

            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            btnCheckEmail.isEnabled = isEmailValid

            updateSignupButton()
        })

        btnCheckEmail.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showEmailStatus("이메일 형식을 확인해주세요.", false)
                resetEmailCheckState()
                return@setOnClickListener
            }
            checkEmailDuplicate(email)
        }

        etPassword.addTextChangedListener(SimpleTextWatcher { pw ->
            isPasswordValid = pw.length >= 6
            tvPwStatus.visibility = View.VISIBLE
            tvPwStatus.setTextColor(if (isPasswordValid) GREEN else RED)
            tvPwStatus.text = if (isPasswordValid) "사용 가능한 비밀번호예요." else "비밀번호는 6자 이상이어야 해요."
            updateSignupButton()
        })

        etPasswordConfirm.addTextChangedListener(SimpleTextWatcher {
            isPasswordMatch = etPassword.text.toString() == etPasswordConfirm.text.toString()
            tvPwConfirmStatus.visibility = View.VISIBLE
            tvPwConfirmStatus.setTextColor(if (isPasswordMatch) GREEN else RED)
            tvPwConfirmStatus.text = if (isPasswordMatch) "비밀번호가 일치해요." else "비밀번호가 일치하지 않아요."
            updateSignupButton()
        })

        cbTerms1.setOnCheckedChangeListener { _, _ -> updateSignupButton() }
        cbTerms2.setOnCheckedChangeListener { _, _ -> updateSignupButton() }
        cbTerms3.setOnCheckedChangeListener { _, _ -> updateSignupButton() }

        btnSignup.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pw = etPassword.text.toString()
            val phoneNumber = "000-0000-0000"
            signup(email, pw, phoneNumber)
        }
    }

    private fun updateSignupButton() {
        val requiredAgreed = cbTerms1.isChecked && cbTerms2.isChecked
        val enabled = isEmailChecked && isPasswordValid && isPasswordMatch && requiredAgreed
        btnSignup.isEnabled = enabled
        btnSignup.setBackgroundResource(if (enabled) R.drawable.bg_btn_blue else R.drawable.bg_btn_gray)
        btnSignup.setTextColor(Color.WHITE)
    }

    private fun checkEmailDuplicate(email: String) {
        // 확인 중에는 버튼 비활성만 (색은 selector가 유지)
        btnCheckEmail.isEnabled = false

        lifecycleScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    authRepository.duplicateCheck(email)
                }

                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    isEmailChecked = true
                    btnCheckEmail.text = "확인됨"
                    btnCheckEmail.setBackgroundResource(R.drawable.bg_btn_green)
                    showEmailStatus("사용 가능한 이메일이에요.", true)
                } else {
                    isEmailChecked = false
                    btnCheckEmail.text = "중복확인"
                    btnCheckEmail.setBackgroundResource(R.drawable.bg_btn_blue)

                    val msg = when (res.code()) {
                        409 -> "이미 사용 중인 이메일이에요."
                        else -> "중복확인 실패 (HTTPS ${res.code()})"
                    }
                    showEmailStatus(msg, false)
                }
            } catch (e: Exception) {
                isEmailChecked = false
                btnCheckEmail.text = "중복확인"
                btnCheckEmail.setBackgroundResource(R.drawable.bg_btn_blue)
                showEmailStatus("네트워크 오류: ${e.message}", false)
            } finally {
                // 이메일이 유효하면 다시 누를 수 있게
                btnCheckEmail.isEnabled = isEmailValid
                updateSignupButton()
            }
        }
    }

    private fun showEmailStatus(text: String, ok: Boolean) {
        tvEmailStatus.visibility = View.VISIBLE
        tvEmailStatus.setTextColor(if (ok) GREEN else RED)
        tvEmailStatus.text = text
    }

    private fun signup(email: String, password: String, phoneNumber: String) {
        btnSignup.isEnabled = false

        lifecycleScope.launch {
            try {
                val res = withContext(Dispatchers.IO) {
                    authRepository.signup(email, password, phoneNumber)
                }

                if (!res.isSuccessful) {
                    Toast.makeText(
                        this@SignupActivity,
                        "회원가입 실패 (HTTPS ${res.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateSignupButton()
                    return@launch
                }

                val body = res.body()
                if (body?.isSuccess == true) {
                    Toast.makeText(this@SignupActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        body?.message ?: "회원가입 실패",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateSignupButton()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SignupActivity,
                    "네트워크 오류: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                updateSignupButton()
            }
        }
    }

    private class SimpleTextWatcher(
        private val onChanged: (String) -> Unit
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            onChanged(s?.toString() ?: "")
        }
    }
}
