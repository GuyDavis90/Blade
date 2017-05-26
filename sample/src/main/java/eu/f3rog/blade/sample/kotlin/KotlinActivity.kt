package eu.f3rog.blade.sample.kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import blade.Blade

import eu.f3rog.blade.sample.R


@Blade
class KotlinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
    }
}
