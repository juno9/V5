package dji.sampleV5.aircraft


import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import dji.v5.ux.sample.showcase.defaultlayout.DefaultLayoutActivity
import dji.v5.ux.sample.showcase.widgetlist.WidgetsActivity
import kotlinx.android.synthetic.main.activity_main_new_one.btn_start
import kotlinx.android.synthetic.main.activity_main_new_one.btn_widgets

class DJIMainAvtivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new_one)

        btn_start.setOnClickListener {
            val nextIntent = Intent(this, DefaultLayoutActivity::class.java)
            startActivity(nextIntent)
        }

        btn_widgets.setOnClickListener {
            val nextIntent = Intent(this, WidgetsActivity::class.java)
            startActivity(nextIntent)
        }

    }
}