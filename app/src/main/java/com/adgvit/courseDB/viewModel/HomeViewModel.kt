package com.adgvit.courseDB.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adgvit.courseDB.Models.Docs
import com.adgvit.courseDB.NetworkUtils.NetworkUtils
import com.adgvit.courseDB.repo.Repo
import com.adgvit.courseDB.tinyDB.TinyDB
//import com.adgvit.courseApp.tinyDB.TinyDB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class HomeViewModel(application: Application) : AndroidViewModel(application) {


    private val repo: Repo = Repo(NetworkUtils.getNetworkAPIInstance())
    val allCourse = MutableLiveData<List<Docs>>()
    val context = application
    val myCourse = MutableLiveData<List<Docs>>()
    val errorMessage = MutableLiveData<String>()
    val allCourseList= ArrayList<Docs>()
    val myCourseList= ArrayList<Docs>()
    var tinyDB : TinyDB = TinyDB(application.applicationContext)
    var load: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getAllCourse(){
        load.postValue(true)
        val call: Call<List<Docs>> = repo.getAllCourse()
        call.enqueue(object: Callback<List<Docs>>{
            override fun onResponse(call: Call<List<Docs>>, response: Response<List<Docs>>) {

                val favourites: ArrayList<String>
                allCourseList.clear()
                myCourseList.clear()
                if(!response.isSuccessful){
                    errorMessage.postValue(response.message())
                    load.postValue(false)
                    return
                }
                else{
                    favourites  = tinyDB.getListString("favourites")

                    for(doc in response.body()!!){
                        if(!favourites.contains(doc.code))
                          allCourseList.add(doc)
                        else{
                            doc.favourite = true
                            myCourseList.add(doc)
                        }
                    }
                    allCourse.postValue(allCourseList)
                    myCourse.postValue(myCourseList)
                    load.postValue(false)
                }

            }

            override fun onFailure(call: Call<List<Docs>>, t: Throwable) {
                errorMessage.postValue(t.message)
                load.postValue(false)
            }

        })
    }
    fun toggleFav(doc: Docs){

        doc.apply {
            favourite = !favourite
        }
        val favourites: ArrayList<String> = tinyDB.getListString("favourites")
        if(doc.favourite){
            myCourseList.add(doc)
            allCourseList.remove(doc)
            favourites.add(doc.code)
            tinyDB.putListString("favourites",favourites)
//            Toast.makeText(getApplication(),"Course added to My Course",Toast.LENGTH_SHORT).show()
        }
        else{
            myCourseList.remove(doc)
            allCourseList.add(doc)
            favourites.remove(doc.code)
            tinyDB.putListString("favourites",favourites)
//            Toast.makeText(getApplication(),"Course removed to My Course",Toast.LENGTH_SHORT).show()
        }
        myCourse.value = myCourseList
        allCourse.value = allCourseList
    }

    fun getSearchedCourse(str: String){
        repo.getSearchedCourse(str).enqueue(object: Callback<List<Docs>>{
            override fun onResponse(call: Call<List<Docs>>, response: Response<List<Docs>>) {


                val favourites: ArrayList<String>

                if(!response.isSuccessful){
                    errorMessage.postValue(response.message())
                    return
                }
                else{
                    favourites  = tinyDB.getListString("favourites")
                    allCourseList.clear()
                    myCourseList.clear()
                    for(doc in response.body()!!){
                        if(!favourites.contains(doc.code))
                            allCourseList.add(doc)
                        else{
                            doc.favourite = true
                            myCourseList.add(doc)
                        }
                    }
                    allCourse.postValue(allCourseList)
                    myCourse.postValue(myCourseList)
                }

            }

            override fun onFailure(call: Call<List<Docs>>, t: Throwable) {
//                errorMessage.postValue(t.message)
            }

        })
    }




}