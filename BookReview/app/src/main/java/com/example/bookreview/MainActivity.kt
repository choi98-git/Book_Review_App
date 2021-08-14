package com.example.bookreview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookreview.adapter.BookAdapter
import com.example.bookreview.adapter.HistoryAdapter
import com.example.bookreview.api.BookService
import com.example.bookreview.databinding.ActivityMainBinding
import com.example.bookreview.model.BestSellerDto
import com.example.bookreview.model.History
import com.example.bookreview.model.SearchBookDto
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBootRecyclerView()
        initHistoryRecyclerView()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB",
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto>{

                // 요청이 성공했을 때
                override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
                    if (response.isSuccessful.not()){
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    response.body()?.let {
                        Log.d(TAG, it.toString())
                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }
                        // book의 정보 List를 보여줌
                        adapter.submitList(it.books)
                    }
                }
                // 요청이 실패했을 때
                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    Log.e(TAG, t.toString())
                }
            })

    }



    private fun search(keyword: String){
        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBookDto>{

                // 요청이 성공했을 때
                override fun onResponse(call: Call<SearchBookDto>, response: Response<SearchBookDto>) {

                    hideHistoryView()
                    saveSearchKeyword(keyword)

                    if (response.isSuccessful.not()){
                        Log.e(TAG, "NOT SUCCESS")
                        return
                    }
                    //list가 keyword List로 변경
                    adapter.submitList(response.body()?.books.orEmpty())
                }
                // 요청이 실패했을 때
                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    hideHistoryView()
                    Log.e(TAG, t.toString())
                }
            })
    }

    private fun initBootRecyclerView(){
        adapter = BookAdapter(itemClickedListener = {
            intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel",it)
            startActivity(intent)
        })

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter

    }

    private fun initHistoryRecyclerView() {
        // x버튼 눌렀을때 실행
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        initSearchEditText()
    }


    private fun initSearchEditText(){
        // 엔터 키를 눌렀을 때
        binding.searchEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN){
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN){
                showHistoryView()
            }
            return@setOnTouchListener false
        }
    }
    // historyRecyclerView 보여줌
    private fun showHistoryView(){
        Thread{ // db에서 모든 기록들을 최신순으로 keywords 리스트에 저장
           val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()
        binding.historyRecyclerView.isVisible = true
    }

    // historyRecyclerView 숨김
    private fun hideHistoryView(){
        binding.historyRecyclerView.isVisible = false
    }

    // 검색된 내용 생서된 DB에 저장
    private fun saveSearchKeyword(keyword: String){
        Thread{
            db.historyDao().insertHistory(History(null,keyword))
        }.start()
    }

    // 검색어 history 삭제 후 뷰 갱신
    private fun deleteSearchKeyword(keyword: String){
        Thread{
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object{
        private const val TAG = "MainActivity"
    }
}