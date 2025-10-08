package com.example.and101_project_5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // By not calling fetchNewPokemon in main, the "Who's That Pokemon" screen can be seen
        setupButton(findViewById<Button>(R.id.getNewPokemon))
    }

    private fun fetchNewPokemon () {
        // This gets a random number from among the total amount of Pokemon
        val num = (1..1025).random()

        val client = AsyncHttpClient()
        val imageView = findViewById<ImageView>(R.id.whosThatPokemon)

        client["https://pokeapi.co/api/v2/pokemon/$num", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("Pokemon", "Response successful")
                // The list of sprites is its own JSOn object, so first we need to fetch that
                val sprites = json.jsonObject.getJSONObject("sprites")
                val pokemonImageURL = sprites.getString("front_default")
                // Pull the name and number from the API
                val pokeName = json.jsonObject.getString("name")
                val pokeNum = json.jsonObject.getInt("id")
                // Create a string from the number pulled from the API that we can pass into PokemonNumber
                val pokeNumber = pokeNum.toString()
                Log.d("petImageURL", "pet image URL set: $pokemonImageURL")
                // Insert the sprite into the ImageView
                Glide.with(this@MainActivity)
                    .load(pokemonImageURL)
                    .fitCenter()
                    .into(imageView)
                // Reformat the name to actually look like a name instead of just data
                val formattedName = pokeName
                    .split("-", "_")
                    .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
                // Set the text of each EditText to their respective information
                findViewById<EditText>(R.id.pokemonName).setText(formattedName)
                findViewById<EditText>(R.id.pokemonNum).setText(pokeNumber)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Pokemon Error", errorResponse)
            }
        }]
    }
    private fun setupButton(button: Button) {
        button.setOnClickListener {
            fetchNewPokemon()
        }
    }
}