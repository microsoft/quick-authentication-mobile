package com.example.signdemo1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ChooseActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.sign_in_activity)?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        findViewById<View>(R.id.token_activity)?.setOnClickListener {
            startActivity(Intent(this, IdTokenActivity::class.java))
        }
    }

}