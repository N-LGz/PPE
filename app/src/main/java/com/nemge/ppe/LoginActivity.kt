package com.nemge.ppe

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun connect(view: View) {
        val intentHome = Intent(this, HomeActivity::class.java)
        startActivity(intentHome)
    }
}
