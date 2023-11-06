import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dreamteam.sharedream.databinding.FragmentCalenderDialogBinding
import com.dreamteam.sharedream.databinding.FragmentEditCalenderDialogBinding
import com.dreamteam.sharedream.view.PostEditFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditCalenderFragmentDialog(private var startDateFormatted: String): DialogFragment() {
    private var _binding:FragmentEditCalenderDialogBinding? = null
    private val binding get() = _binding!!
    private var dataListener: CalendarDataListener? = null
    private var selectedDate: Date? = null
    private var initialStartDate: Date? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCalenderDialogBinding.inflate(inflater, container, false)



        val postCalender: CalendarView = binding.postCalender
        val startDate = initialStartDate?.let {
            SimpleDateFormat("yyyy년MM월dd일", Locale.getDefault()).format(it)
        } ?: extractStartDate(startDateFormatted)

        Log.d("fasafs","$startDate")

        binding.startDate.text = startDate+"부터,"


        postCalender.setOnDateChangeListener { postCalender, year, month, dayOfMonth ->
            val day: String = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            binding.endDate.text = day+"까지"
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

    fun setCalendarDataListener(listener: PostEditFragment) {
        dataListener = listener
    }

    private fun extractStartDate(startDateFormatted: String): String {
        val parts = startDateFormatted.split("부터,")
        return if (parts.isNotEmpty()) {
            parts[0].trim()
        } else {
            startDateFormatted
        }
    }






}