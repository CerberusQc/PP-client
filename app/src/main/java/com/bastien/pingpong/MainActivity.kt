package com.bastien.pingpong

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    val pingRoute = "/ping/"
    val pongRoute = "/pong/"
    val createRoute = "/create/"
    var route = ""
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val create = findViewById<Button>(R.id.btn_create)
        val join = findViewById<Button>(R.id.btn_join)
        val ping = findViewById<Button>(R.id.btn_ping)

        ping.visibility = View.INVISIBLE

        val host = findViewById<EditText>(R.id.text_host)
        val room = findViewById<EditText>(R.id.text_room)

        create.setOnClickListener {
            hideKeyboard()
            create(host.text, room.text)
        }

        join.setOnClickListener {
            hideKeyboard()
            join(host.text, room.text)
        }

        ping.setOnClickListener {
            hideKeyboard()
            play(host.text, room.text)
        }
    }

    fun play(host: Editable, room: Editable) {
        switch(false)

        val request = Request.Builder()
            .url("http://$host$route$room")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body()?.string()
                val code = response.code()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()

                    if (code == 200) {
                        switch(true)
                    } else {
                        menu(true)
                    }
                }

            }
        })
    }

    fun create(host: Editable, room: Editable) {
        menu(false)
        switch(false)
        Toast.makeText(
            this, "Creating $host Room : $room... Waiting for another player",
            Toast.LENGTH_SHORT
        ).show()
        val request = Request.Builder()
            .url("http://$host$createRoute$room")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val data = response.body()?.string()
                val code = response.code()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_LONG).show()
                    if (code == 200) {
                        route = pingRoute
                        switch(true)
                    } else {
                        menu(true)
                    }
                }
            }
        })
    }

    fun join(host: Editable, room: Editable) {
        menu(false)
        route = pongRoute
        Toast.makeText(this, "Joining $host Room : $room", Toast.LENGTH_SHORT).show()
        play(host, room)
    }

    fun menu(on: Boolean) {
        val btn = findViewById<Button>(R.id.btn_ping)
        val create = findViewById<Button>(R.id.btn_create)
        val join = findViewById<Button>(R.id.btn_join)

        if (on) {
            create.visibility = View.VISIBLE
            join.visibility = View.VISIBLE
            btn.visibility = View.INVISIBLE
        } else {
            create.visibility = View.INVISIBLE
            join.visibility = View.INVISIBLE
            btn.visibility = View.VISIBLE
        }
    }

    fun switch(enabled: Boolean) {
        val btn = findViewById<Button>(R.id.btn_ping)

        if (enabled) {
            btn.setBackgroundColor(Color.CYAN)
            btn.isEnabled = true
        } else {
            btn.setBackgroundColor(Color.DKGRAY)
            btn.isEnabled = false
        }
    }

    fun hideKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }
}
