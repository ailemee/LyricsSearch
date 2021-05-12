package com.example.lyricssearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class MainActivity : AppCompatActivity() {
    lateinit var artistTextView : TextView
    lateinit var songTextView : TextView
    lateinit var searchLyricsButton : Button
    lateinit var lyricsView : TextView

    // base api url
    companion object {
        const val URL_API = "https://api.lyrics.ovh/v1/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.artistTextView = findViewById(R.id.artistTextView)
        this.songTextView = findViewById(R.id.songTextView)
        this.searchLyricsButton = findViewById(R.id.searchLyricsButton)
        this.lyricsView = findViewById(R.id.lyricsView)

        // generates an implementation of the retrofit service interface
        val retro = Retrofit.Builder()
                .baseUrl(URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        // initialize the client
        val service = retro.create(LyricsService::class.java)

        // when the search button is pressed, GET request is made using editText contents as parameters
        // if we get a response from api onResponse() function stores the response and with the help of getLyrics()
        // only stores the string containing lyrics only not the whole json
        // if the response was null, textView for the lyrics will display "no lyrics found"
        // otherwise textView will display the song lyrics
        searchLyricsButton.setOnClickListener {
            val myLyricsRequest = service.search(artistTextView.text.toString(), songTextView.text.toString())
            myLyricsRequest.enqueue(object : Callback<Lyrics> {
                override fun onResponse(call: Call<Lyrics>, response: Response<Lyrics>) {
                    val result = response.body()?.getLyrics()
                    if(result == null) {
                        lyricsView.text = "no lyrics found"
                    } else {
                        lyricsView.text = result.toString()
                    }
                }
                override fun onFailure(call: Call<Lyrics>, t: Throwable) {
                    Log.i(MainActivity::class.simpleName, "error")
                }
            })
        }



//            if(artist.isEmpty() || song.isEmpty()) {
//                Toast.makeText(applicationContext, "Please input artist name and/or song name!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
    }
}

// data class that defines how our lyrics data will look like.
data class Lyrics(
        val lyrics: String? = null,
) {
    // getLyrics() returns only the lyrics, not the whole json from api.
    @JvmName("getLyrics1")
    fun getLyrics(): String? {
        return this.lyrics
    }
}

// interface to run the api.
interface LyricsService {
    // @GET tells retrofit that the calltype is GET. Value modifies the api base url
    @GET("{artist}/{song}")
    // function that makes the call using artist and song as parameters
    fun search(
            @Path("artist") artist: String,
            @Path("song") song: String
    ): Call<Lyrics>
}
