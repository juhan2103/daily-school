package com.daily_school.daily_school.ui.page

import FirebaseManager
import android.graphics.Paint
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.daily_school.daily_school.R
import com.daily_school.daily_school.databinding.FragmentHomeBinding
import com.daily_school.daily_school.home.HomeCalendarFragment
import com.daily_school.daily_school.utils.KakaoRef
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName

    private lateinit var binding : FragmentHomeBinding

    private var currentDate = LocalDate.now()

    private val firebaseManager = FirebaseManager()

    private var midDateValue : String = ""
    private var finalDateValue : String = ""

    private var todayMidCal : Calendar = Calendar.getInstance()
    private var dDayMidCal : Calendar = Calendar.getInstance()

    private var midYear : Int = 0
    private var midMonth : Int = 0
    private var midDay : Int = 0

    private var todayFinalCal : Calendar = Calendar.getInstance()
    private var dDayFinalCal : Calendar = Calendar.getInstance()

    private var finalYear : Int = 0
    private var finalMonth : Int = 0
    private var finalDay : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val swipe = binding.homeSwipeLayout
        swipe.setOnRefreshListener {

            // 중간고사 디데이 설정 함수 호출
            changeFirebaseMidTextView()

            // 기말고사 디데이 설정 함수 호출
            changeFirebaseFinalTextView()

            swipe.isRefreshing = false
        }

        // 중간고사 디데이 설정 함수 호출
        changeFirebaseMidTextView()

        // 기말고사 디데이 설정 함수 호출
        changeFirebaseFinalTextView()

        // 유저 정보를 입력시키는 함수 호출
        getSchoolInfoData()

        // 날짜 텍스트뷰에 현재 날짜를 연결하는 함수 호출
        setCurrentDate()

        // 중간고사 캘린더 이동 함수 호출
        showMidCalendar()

        // 중간고사 캘린더 이동 함수 호출
        showFinalCalendar()

        // // 할 일 목록을 보여주는 함수 호출
        showTodoList()

        return binding.root

    }

    // 중간고사 디데이 설정 함수
    private fun changeFirebaseMidTextView(){
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e(TAG, "토큰 정보 보기 실패", error)
            } else if (tokenInfo != null) {

                lifecycleScope.launch {
                    try {
                        val dateInfo = firebaseManager.readDDayDateInfoData(tokenInfo.id.toString())
                        if (dateInfo != null) {
                            var midDateText = dateInfo["midDay"].toString()
                            if (midDateText.length == 7){
                                midDateText = midDateText.substring(0,6) + "0" + midDateText.substring(6,7)
                                midYear = midDateText.substring(0,4).toInt()
                                midMonth = midDateText.substring(4,6).toInt() - 1
                                midDay = midDateText.substring(6,8).toInt()
                            }
                            else{
                                midYear = midDateText.substring(0,4).toInt()
                                midMonth = midDateText.substring(4,6).toInt() - 1
                                midDay = midDateText.substring(6,8).toInt()
                            }
                            dDayMidCal.set(midYear, midMonth, midDay)
                            val today = todayMidCal.timeInMillis / 86400000
                            val dDay = dDayMidCal.timeInMillis / 86400000
                            val count = dDay - today
                            

                            if(count < 0){
                                binding.homeMidDDayTextView.visibility = View.VISIBLE
                                binding.homeMidDayTextView.visibility = View.INVISIBLE
                                binding.homeMidDDayTextView.text = "D+" + abs(count)
                            }
                            else{
                                binding.homeMidDDayTextView.visibility = View.VISIBLE
                                binding.homeMidDayTextView.visibility = View.INVISIBLE
                                binding.homeMidDDayTextView.text = "D-" + abs(count)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to Read", e)
                    }
                }

            }
        }
    }

    // 기말고사 디데이 설정 함수
    private fun changeFirebaseFinalTextView(){

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e(TAG, "토큰 정보 보기 실패", error)
            } else if (tokenInfo != null) {

                lifecycleScope.launch {
                    try {
                        val dateInfo = firebaseManager.readDDayDateInfoData(tokenInfo.id.toString())
                        if (dateInfo != null) {
                            var finalDateText = dateInfo["finalDay"].toString()
                            if (finalDateText.length == 7){
                                finalDateText = finalDateText.substring(0,6) + "0" + finalDateText.substring(6,7)
                                finalYear = finalDateText.substring(0,4).toInt()
                                finalMonth = finalDateText.substring(4,6).toInt() - 1
                                finalDay = finalDateText.substring(6,8).toInt()
                            }
                            else{
                                finalYear = finalDateText.substring(0,4).toInt()
                                finalMonth = finalDateText.substring(4,6).toInt() - 1
                                finalDay = finalDateText.substring(6,8).toInt()
                            }
                            Log.d(TAG, finalDateText)
                            dDayFinalCal.set(finalYear, finalMonth, finalDay)
                            val today = todayFinalCal.timeInMillis / 86400000
                            val dDay = dDayFinalCal.timeInMillis / 86400000
                            val count = dDay - today


                            if(count < 0){
                                binding.homeFinalDDayTextView.visibility = View.VISIBLE
                                binding.homeFinalDayTextView.visibility = View.INVISIBLE
                                binding.homeFinalDDayTextView.text = "D+" + abs(count)
                            }
                            else{
                                binding.homeFinalDDayTextView.visibility = View.VISIBLE
                                binding.homeFinalDayTextView.visibility = View.INVISIBLE
                                binding.homeFinalDDayTextView.text = "D-" + abs(count)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to Read", e)
                    }
                }

            }
        }
    }

    // 날짜 포맷을 세팅하는 함수
    private fun loadCurrentDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일").withLocale(
            Locale.forLanguageTag("ko")
        )
        return date.format(formatter)
    }

    // 날짜 텍스트뷰에 현재 날짜를 연결하는 함수
    private fun setCurrentDate() {
        binding.homeTodayTextView.text = loadCurrentDate(currentDate)
    }

    // 날짜 포맷을 세팅하는 함수
    private fun loadTodayDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd").withLocale(
            Locale.forLanguageTag("ko")
        )
        return date.format(formatter)
    }

    // 중간고사 캘린더 이동 함수
    private fun showMidCalendar(){
        binding.homeMidLayout.setOnClickListener {
            val bottomSheet = HomeCalendarFragment("중간고사",0)
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    // 기말고사 캘린더 이동 함수
    private fun showFinalCalendar(){
        binding.homeFinalLayout.setOnClickListener {
            val bottomSheet = HomeCalendarFragment("기말고사",1)
            bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
        }
    }

    // 유저 정보를 입력시키는 함수
    private fun getSchoolInfoData(){
        KakaoSdk.init(requireContext(), KakaoRef.APP_KEY)
        UserApiClient.instance.accessTokenInfo{ tokenInfo, error ->
            if (error != null){
                Log.e(TAG, "토큰 정보 보기 실패", error)
            }
            else if (tokenInfo != null){
                lifecycleScope.launch{
                    try {
                        val userInfo = firebaseManager.readSchoolInfoData(tokenInfo.id.toString())
                        if (userInfo != null) {
                            val userSchoolName = userInfo["schoolName"]
                            val userGrade = userInfo["grade"]
                            val userClass = userInfo["class"]
                            binding.homeMainTextView.text = "$userSchoolName $userGrade $userClass"

                            UserApiClient.instance.me { user, error ->
                                if (error != null) {
                                    Log.e(TAG, "사용자 정보 요청 실패 $error")
                                } else if (user != null) {
                                    Log.e(TAG, "사용자 정보 요청 성공 : $user")
                                    val nickname = user.kakaoAccount?.profile?.nickname
                                    binding.homeNicknameTextView.text = "$nickname"
                                }
                            }

                        } else {
                            Log.d(TAG, "TodoList is Null")
                        }
                    }
                    catch (e: Exception) {
                        Log.e(TAG, "Failed to Read", e)
                    }
                }

            }
        }

    }

    // 할 일 목록을 보여주는 함수
    private fun showTodoList() {
        KakaoSdk.init(requireContext(), KakaoRef.APP_KEY)
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e(TAG, "토큰 정보 보기 실패", error)
            } else if (tokenInfo != null) {
                val getCurrentDate = currentDate.toString()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date = dateFormat.parse(getCurrentDate)
                val getDate = SimpleDateFormat("yyyy-MM-d").format(date)

                lifecycleScope.launch {
                    try {
                        val todoList = firebaseManager.readTodoListData(tokenInfo.id.toString())
                        if (todoList != null) {
                            Log.d(TAG, "TodoList : $todoList")

                            binding.homeToDoDetailTextView.visibility = View.INVISIBLE

                            val filteredTodoList = todoList.filter { todoItem ->
                                getDate == todoItem["todoDate"]
                            }

                            val planTodoLayout = binding.planTodoLayout
                            val inflater = LayoutInflater.from(requireContext())
                            planTodoLayout.removeAllViews()

                            for (i in filteredTodoList.indices) {
                                val todoItem = filteredTodoList[i]
                                val todoName = todoItem["todoName"].toString()
                                val todoComplete = todoItem["todoComplete"]

                                val planTodoItem = inflater.inflate(R.layout.plan_todo_check_item, planTodoLayout, false)
                                val planTodoColorImageView = planTodoItem.findViewById<ImageView>(R.id.planTodoCheckImageView)
                                val planTodoTextView = planTodoItem.findViewById<TextView>(R.id.planTodoTextView)

                                if(todoComplete == true) {
                                    val textColorResourceId = R.color.gray_6
                                    val textColor = ContextCompat.getColor(requireContext(), textColorResourceId)

                                    planTodoTextView.paintFlags = planTodoTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                    planTodoTextView.setTextColor(textColor)
                                    planTodoColorImageView.setBackgroundResource(R.drawable.ic_todo_check_disable)
                                }

                                planTodoTextView.text = todoName
                                planTodoLayout.addView(planTodoItem)
                            }

                        } else {
                            Log.d(TAG, "TodoList is Null")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to Read", e)
                    }
                }
            }
        }
    }
}