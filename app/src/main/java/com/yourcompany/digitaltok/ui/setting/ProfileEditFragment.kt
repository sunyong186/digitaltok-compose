package com.yourcompany.digitaltok.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.MainActivity
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.network.RetrofitClient
import com.yourcompany.digitaltok.data.repository.AccountRepository
import com.yourcompany.digitaltok.data.repository.AuthLocalStore
import com.yourcompany.digitaltok.data.repository.PrefsAuthLocalStore
import com.yourcompany.digitaltok.data.repository.UserRepository
import com.yourcompany.digitaltok.databinding.FragmentProfileEditBinding
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment() {

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var accountRepository: AccountRepository
    private lateinit var userRepository: UserRepository

    // 로그아웃 실패(500 등) 시에도 로컬 토큰 삭제를 위해 Fragment에서도 보관
    private lateinit var localStore: AuthLocalStore

    // prefs key (HelpFragment와 동일)
    private val PREFS_NAME = "auth_prefs"
    private val KEY_EMAIL = "email"
    private val KEY_NICKNAME = "nickname"
    private val KEY_USER_ID = "userId"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.connectTopAppBar.titleTextView.text = "프로필 편집"
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }


        // Repository 초기화
        val accountApi = RetrofitClient.create(AccountApiService::class.java)
        localStore = PrefsAuthLocalStore(requireContext().applicationContext)
        accountRepository = AccountRepository(accountApi, localStore)

        // /users/me 조회용
        userRepository = UserRepository(requireContext().applicationContext)

        binding.tvLogout.setOnClickListener { showLogoutDialog() }
        binding.tvWithdraw.setOnClickListener { showWithdrawDialog() }
        binding.ivEditEmail.setOnClickListener { showChangeEmailDialog() }

        // 1) 공백 방지: args/prefs 즉시 바인딩
        bindFromArgsOrPrefs()

        // 2) 서버 최신값으로 동기화
        loadMyProfile()
    }

    private fun bindFromArgsOrPrefs() {
        val argNickname = arguments?.getString(KEY_NICKNAME).orEmpty()
        val argEmail = arguments?.getString(KEY_EMAIL).orEmpty()

        if (argNickname.isNotBlank()) binding.tvNameValue.text = argNickname
        if (argEmail.isNotBlank()) binding.tvEmailValue.text = argEmail

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedNickname = prefs.getString(KEY_NICKNAME, "").orEmpty()
        val cachedEmail = prefs.getString(KEY_EMAIL, "").orEmpty()

        if (binding.tvNameValue.text.isNullOrBlank() && cachedNickname.isNotBlank()) {
            binding.tvNameValue.text = cachedNickname
        }
        if (binding.tvEmailValue.text.isNullOrBlank() && cachedEmail.isNotBlank()) {
            binding.tvEmailValue.text = cachedEmail
        }

        Log.d("ProfileEdit", "BIND args: ($argNickname, $argEmail) / prefs: ($cachedNickname, $cachedEmail)")
    }

    private fun loadMyProfile() {
        lifecycleScope.launch {
            Log.d("ProfileEdit", "calling getMyProfile() ...")

            userRepository.getMyProfile()
                .onSuccess { me ->
                    binding.tvNameValue.text = me.nickname
                    binding.tvEmailValue.text = me.email

                    val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit()
                        .putLong(KEY_USER_ID, me.userId)
                        .putString(KEY_NICKNAME, me.nickname)
                        .putString(KEY_EMAIL, me.email)
                        .apply()

                    Log.d("ProfileEdit", "SERVER/UI BIND OK: ${me.nickname}, ${me.email}")
                }
                .onFailure { e ->
                    Log.e("ProfileEdit", "getMyProfile failed", e)
                    showError("프로필 정보를 불러오지 못했습니다.")
                }
        }
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnLogout).setOnClickListener {
            dialog.dismiss()

            lifecycleScope.launch {
                Log.d("Logout", "UI CLICK -> logout confirmed")

                val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val access = prefs.getString("accessToken", null)
                val refresh = prefs.getString("refreshToken", null)
                Log.d("Logout", "BEFORE logout prefs: accessToken=${access?.take(10)}..., refreshToken=${refresh?.take(10)}...")

                accountRepository.logout()
                    .onSuccess {
                        Log.d("Logout", "RESPONSE -> logout SUCCESS")
                        Log.d("Logout", "LOCAL AUTH CLEARED (by repository)")
                        moveToSplash()
                    }
                    .onFailure { e ->
                        // 서버가 500이어도 "앱 로그아웃"은 진행
                        Log.e("Logout", "RESPONSE -> logout FAILED, but do local logout anyway", e)

                        runCatching { localStore.clearAuth() }
                            .onSuccess { Log.d("Logout", "LOCAL AUTH CLEARED (forced)") }
                            .onFailure { Log.e("Logout", "LOCAL AUTH CLEAR FAILED", it) }

                        Toast.makeText(
                            requireContext(),
                            "서버 로그아웃에 실패. 앱 로그아웃 처리.",
                            Toast.LENGTH_SHORT
                        ).show()

                        moveToSplash()
                    }
            }
        }

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showWithdrawDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_withdraw, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<View>(R.id.btnWithdraw).setOnClickListener {
            dialog.dismiss()

            lifecycleScope.launch {
                Log.d("Withdraw", "UI CLICK -> withdraw confirmed")

                accountRepository.withdraw()
                    .onSuccess {
                        Log.d("Withdraw", "UI RESULT -> withdraw SUCCESS, moving to start")
                        moveToSplash()
                    }
                    .onFailure { e ->
                        Log.e("Withdraw", "UI RESULT -> withdraw FAIL", e)
                        showError(e.message ?: "회원탈퇴 실패")
                    }
            }
        }

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 스플래시로 이동
    private fun moveToSplash() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun showError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showChangeEmailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_email, null)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etPassword = dialogView.findViewById<android.widget.EditText>(R.id.etPassword)
        val etNewEmail = dialogView.findViewById<android.widget.EditText>(R.id.etNewEmail)

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnChange).setOnClickListener {
            val password = etPassword.text?.toString().orEmpty()
            val newEmail = etNewEmail.text?.toString().orEmpty()

            if (password.isBlank()) {
                showError("기존 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }
            if (newEmail.isBlank()) {
                showError("새 이메일을 입력해주세요.")
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                showError("이메일 형식이 올바르지 않습니다.")
                return@setOnClickListener
            }

            Log.d("ChangeEmail", "passwordLen=${password.length}, newEmail=$newEmail")

            lifecycleScope.launch {
                accountRepository.changeEmail(password, newEmail)
                    .onSuccess {
                        Log.d("ChangeEmail", "changeEmail SUCCESS")
                        dialog.dismiss()
                        binding.tvEmailValue.text = newEmail
                        Log.d("ChangeEmail", "UI set tvEmailValue=$newEmail")
                        loadMyProfile()
                    }
                    .onFailure { e ->
                        Log.e("ChangeEmail", "changeEmail FAILED", e)
                        showError(e.message ?: "이메일 변경 실패")
                    }
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
