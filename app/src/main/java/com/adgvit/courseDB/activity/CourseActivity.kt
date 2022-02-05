package com.adgvit.courseDB.activity

//import com.adgvit.courseApp.databinding.ActivityCourseBinding
//import com.adgvit.courseApp.databinding.ActivityHomeBinding
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.adgvit.courseDB.Models.Course
import com.adgvit.courseDB.R
import com.adgvit.courseDB.fragments.FragmentModule
import com.adgvit.courseDB.fragments.ReferenceFragment
import com.adgvit.courseDB.fragments.TextbookFragment
import com.adgvit.courseDB.viewModel.CourseViewModel
import com.google.android.material.tabs.TabLayout

class CourseActivity : AppCompatActivity() {

    lateinit var courseViewModel: CourseViewModel

    lateinit var courseabbrev: TextView
    lateinit var coursename: TextView
    lateinit var courseCode : TextView
    lateinit var textviewL: TextView
    lateinit var textviewT: TextView
    lateinit var textviewP: TextView
    lateinit var textviewJ: TextView
    lateinit var textviewCredits: TextView
    lateinit var textviewCourseDesc: TextView
    lateinit var back:ImageView
    lateinit var imageStar: ImageView
    lateinit var progressBar: ProgressBar

    lateinit var viewpager: ViewPager
    lateinit var tabLayout: TabLayout
    private lateinit var loadConstraint: ConstraintLayout
    companion object
    {
        var DataList: MutableLiveData<Course> = MutableLiveData<Course>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        viewpager = findViewById(R.id.view_pager_course)
        viewpager.adapter = PageAdapter(supportFragmentManager)

        tabLayout = findViewById(R.id.tab_layout_course)
        tabLayout.setupWithViewPager(viewpager)
        textviewJ = findViewById(R.id.J_text_view)
        textviewT = findViewById(R.id.T_text_view)
        textviewL = findViewById(R.id.L_text_view)
        textviewP = findViewById(R.id.P_text_view)
        textviewCredits = findViewById(R.id.credits_text_view)
        courseabbrev = findViewById(R.id.course_name_coursepage)
        coursename = findViewById(R.id.course_name_full)
        courseCode = findViewById(R.id.course_code_coursepage)
        textviewCourseDesc = findViewById(R.id.course_desc_coursepage)
        back = findViewById<ImageView>(R.id.settings_back)
        imageStar = findViewById(R.id.image_star)
        progressBar = findViewById(R.id.progress_bar)
        loadConstraint=findViewById(R.id.loadLayout)


        back.setOnClickListener(View.OnClickListener { finish() })
        imageStar.setOnClickListener {
            Log.i("STAR", "star clicked")
            courseViewModel.onStarClicked()
            Home.star=true
        }
        courseViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(CourseViewModel::class.java)

        courseViewModel.course.observe(this, Observer {
//            Toast.makeText(applicationContext, "" + it.code, Toast.LENGTH_LONG).show()
            Log.i("Hello", "" + it.code)
//            Toast.makeText(applicationContext, "" + it.code, Toast.LENGTH_LONG).show()
            Log.i("Error", "" + it.code)
            textviewJ.text = it.credits?.J
            textviewT.text = it.credits?.T
            textviewL.text = it.credits?.L
            textviewP.text = it.credits?.P
            textviewCredits.text = it.credits?.C
            coursename.text = it.name
            courseCode.text = it.code
            DataList.postValue(it)
            textviewCourseDesc.text = courseViewModel.getTextCourseDesc(textviewJ, textviewT, textviewL, textviewP)
            courseabbrev.text = courseViewModel.getCourseAbbr(coursename.text.toString())
            if(it.favourite){
                imageStar.setImageResource(R.drawable.ic_star_selected)
            }
            else{
                imageStar.setImageResource(R.drawable.ic_star_notselected)
            }
        })
        courseViewModel.load.observe(this, Observer {
            if(it==true){

                progressBar.visibility = ProgressBar.VISIBLE
                loadConstraint.visibility=View.VISIBLE
            }
            else{

                progressBar.visibility = ProgressBar.GONE
                loadConstraint.visibility=View.GONE
            }
        })

//        courseViewModel.getCourseFromCode("CSE2001")
        val ccode = intent.getStringExtra("code")
        ccode?.let {
            courseViewModel.getCourseFromCode(ccode)
        }

    }

    inner class PageAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm){
        override fun getCount(): Int {
            return 3;
        }

        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> {
                    val fragmod:FragmentModule = FragmentModule()
                    return fragmod

                }
                1 -> {
                    val fragText:TextbookFragment = TextbookFragment()
                    return fragText
                }
                else -> {
                    val fragRef:ReferenceFragment = ReferenceFragment()
                    return fragRef
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> {return "Modules"}
                1 -> {return "Textbooks"}
                else -> {return "References"}
            }
            return super.getPageTitle(position)
        }

    }



}