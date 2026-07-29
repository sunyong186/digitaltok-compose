package com.yourcompany.digitaltok.ui.faq

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yourcompany.digitaltok.databinding.FragmentHelpBinding
import com.yourcompany.digitaltok.data.repository.UserRepository
import com.yourcompany.digitaltok.ui.profile.ProfileEditFragment
import com.yourcompany.digitaltok.ui.setting.SupportFragment
import kotlinx.coroutines.launch

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository

    // prefs key (프로필 편집 화면에서 바로 꺼내 쓸 수 있게 저장)
    private val PREFS_NAME = "auth_prefs"
    private val KEY_EMAIL = "email"
    private val KEY_NICKNAME = "nickname"
    private val KEY_USER_ID = "userId"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRepository = UserRepository(requireContext().applicationContext)

        // HelpFragment가 올라가 있는 FragmentContainerView id
        val containerId = (view.parent as View).id

        // 상단바
        binding.connectTopAppBar.titleTextView.text = "설정"
        binding.connectTopAppBar.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 화면 진입 시: 먼저 prefs 값으로 즉시 표시(공백 방지)
        bindUserInfoFromPrefs()

        // 동시에 서버에서 최신값 받아서 갱신
        loadUserInfoAndCache()

        // 프로필 편집 → ProfileEditFragment
        binding.btnEditProfile.setOnClickListener {
            if (containerId != View.NO_ID) {
                // 프로필 편집 화면으로 “현재 값”을 Bundle로 넘김 (편집 화면 즉시 표시용)
                val args = Bundle().apply {
                    putString(KEY_NICKNAME, binding.tvUserName.text?.toString().orEmpty())
                    putString(KEY_EMAIL, binding.tvUserEmail.text?.toString().orEmpty())
                }

                parentFragmentManager.beginTransaction()
                    .replace(containerId, ProfileEditFragment().apply { arguments = args })
                    .addToBackStack(null)
                    .commit()
            }
        }

        // FAQ로 이동 → FaqFragment
        binding.rowFaq.setOnClickListener {
            if (containerId != View.NO_ID) {
                parentFragmentManager.beginTransaction()
                    .replace(containerId, FaqFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Support로 이동 → SupportFragment
        binding.rowSupport.setOnClickListener {
            if (containerId != View.NO_ID) {
                parentFragmentManager.beginTransaction()
                    .replace(containerId, SupportFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    /**
     * 서버 호출 전이라도 SharedPreferences에 저장된 값으로 먼저 UI 채움 (공백 방지)
     */
    private fun bindUserInfoFromPrefs() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cachedNickname = prefs.getString(KEY_NICKNAME, "") ?: ""
        val cachedEmail = prefs.getString(KEY_EMAIL, "") ?: ""

        if (cachedNickname.isNotBlank()) binding.tvUserName.text = cachedNickname
        if (cachedEmail.isNotBlank()) binding.tvUserEmail.text = cachedEmail

        Log.d("HelpFragment", "PREFS/UI BIND: $cachedNickname, $cachedEmail")
    }

    /**
     * /users/me 호출해서 UI 갱신 + prefs에 캐싱
     */
    private fun loadUserInfoAndCache() {
        lifecycleScope.launch {
            Log.d("HelpFragment", "calling getMyProfile() ...")

            userRepository.getMyProfile()
                .onSuccess { me ->
                    // UI 갱신
                    binding.tvUserName.text = me.nickname
                    binding.tvUserEmail.text = me.email

                    // prefs 캐싱 (프로필 편집 화면에서 재사용 가능)
                    val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit()
                        .putLong(KEY_USER_ID, me.userId)
                        .putString(KEY_NICKNAME, me.nickname)
                        .putString(KEY_EMAIL, me.email)
                        .apply()

                    Log.d("HelpFragment", "SERVER/UI BIND OK: ${me.nickname}, ${me.email}")
                }
                .onFailure { e ->
                    Log.e("HelpFragment", "getMyProfile failed", e)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
