package com.yourcompany.digitaltok.ui.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.digitaltok.ui.theme.FaqItem
import com.yourcompany.digitaltok.databinding.FragmentFaqBinding
import com.yourcompany.digitaltok.ui.setting.SupportFragment


class FaqFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단바
        binding.connectTopAppBar.titleTextView.text = "자주 묻는 질문"
        binding.connectTopAppBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }


        // FAQ 데이터
        val faqList = listOf(
            FaqItem(
                "디링은 어떻게 사용하나요?",
                "앱에서 이미지나 템플릿을 선택한 후 NFC로 디링에 전송하면 자동으로 표시됩니다."
            ),
            FaqItem(
                "디링과 연결이 안 돼요",
                "휴대폰 NFC가 켜져 있는지 확인하고, 디링을 휴대폰 뒷면에 가까이 대주세요."
            ),
            FaqItem(
                "이미지가 표시되지 않아요",
                "이미지 크기가 너무 크거나 지원하지 않는 형식일 수 있습니다. JPG, PNG 형식을 사용해주세요."
            ),
            FaqItem(
                "여러 개의 디링을 연결할 수 있나요?",
                "현재는 하나의 디링에 연결할 수 있습니다."
            )
        )

        // RecyclerView
        binding.rvFaq.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFaq.adapter = FaqAdapter(faqList)

        // CTA 클릭 처리: SupportFragment로 교체 (네비 유지)
        binding.ctaSupport.btnContactSupport.setOnClickListener {
            val containerId = id  // 지금 FaqFragment가 붙어있는 FragmentContainerView id

            if (containerId != View.NO_ID) {
                parentFragmentManager.beginTransaction()
                    .replace(containerId, SupportFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
