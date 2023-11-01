import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import android.widget.Toast
import com.dreamteam.sharedream.databinding.FragmentCalenderDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CalenderFragmentDialog : DialogFragment() {
    private var _binding: FragmentCalenderDialogBinding? = null
    private val binding get() = _binding!!
    private var dataListener: CalendarDataListener? = null
    private var selectedDate: Date? = null // 저장된 선택한 날짜

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalenderDialogBinding.inflate(inflater, container, false)

        val postCalender: CalendarView = binding.postCalender
        val currentDate = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())
        binding.startDate.text = currentDate

        postCalender.setOnDateChangeListener { postCalender, year, month, dayOfMonth ->
            val day: String = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            binding.endDate.text = day
            selectedDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
        }

        binding.checkCalender.setOnClickListener {
            if (selectedDate != null) {
                val currentCalendar = Calendar.getInstance()
                val currentDate = currentCalendar.time
                if (selectedDate!!.before(currentDate)) {
                    Toast.makeText(requireContext(), "현재 시간 이전으로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    dataListener?.onDataSelected(selectedDate!!)
                    dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "날짜를 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.discardCalender.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    interface CalendarDataListener {
        fun onDataSelected(date: Date)
    }

    fun setCalendarDataListener(listener: CalendarDataListener) {
        dataListener = listener
    }
}