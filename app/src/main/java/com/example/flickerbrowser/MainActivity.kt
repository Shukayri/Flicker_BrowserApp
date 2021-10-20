package com.example.flickerbrowser


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickerbrowser.image.Image
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {
    private lateinit var images: ArrayList<Image>
    private lateinit var ivMain: ImageView
    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: MyAdapter
    private lateinit var bottomLay: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var btSearch: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomLay = findViewById(R.id.bottomLay)
        etSearch = findViewById(R.id.searchED)
        btSearch = findViewById(R.id.searchBtn)

        rvMain = findViewById(R.id.mainRV)
        images = arrayListOf()
        rvAdapter = MyAdapter(this, images)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)



        btSearch.setOnClickListener {
            if(etSearch.text.isNotEmpty()){
                requestAPI()
            }else{
                Toast.makeText(this, "Search field is empty", Toast.LENGTH_LONG).show()
            }
        }

        ivMain = findViewById(R.id.ivMain)
        ivMain.setOnClickListener { closeImg() }
    }

    private fun requestAPI(){
        CoroutineScope(IO).launch {
            val data =
                withContext(Dispatchers.Default) { getPhotos() }
            if(data.isNotEmpty()){
                println(data)
                showPhotos(data)
            }else{
                Toast.makeText(this@MainActivity, "No Images Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getPhotos(): String{
        var response = ""
        try{
            response = URL("https://api.flickr.com/services/rest/?method=flickr.photos.search&per_page=10&api_key=cb0cbca5c50568f7e3189b08d8e6a89b&tags=${etSearch.text}&format=json&nojsoncallback=1")
                .readText(Charsets.UTF_8)
        }catch(e: Exception){
            println("ISSUE: $e")
        }
        return response
    }

    private suspend fun showPhotos(data: String){
        withContext(Main){
            val jsonObj = JSONObject(data)
            val photos = jsonObj.getJSONObject("photos").getJSONArray("photo")
            println("photos")
            println(photos.getJSONObject(0))
            println(photos.getJSONObject(0).getString("farm"))
            for(i in 0 until photos.length()){
                val title = photos.getJSONObject(i).getString("title")
                val farmID = photos.getJSONObject(i).getString("farm")
                val serverID = photos.getJSONObject(i).getString("server")
                val id = photos.getJSONObject(i).getString("id")
                val secret = photos.getJSONObject(i).getString("secret")
                val photoLink = "https://farm$farmID.staticflickr.com/$serverID/${id}_$secret.jpg"
                images.add(Image(title, photoLink))
            }
            rvAdapter.notifyDataSetChanged()
        }
    }

    fun openImg(link: String){
        Glide.with(this).load(link).into(ivMain)
        ivMain.isVisible = true
        rvMain.isVisible = false
        bottomLay.isVisible = false
    }

    private fun closeImg(){
        ivMain.isVisible = false
        rvMain.isVisible = true
        bottomLay.isVisible = true
    }
}