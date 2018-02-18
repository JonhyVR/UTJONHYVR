package jvr.utcuates


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private var mDriver: Button? = null
    private var mCustomer: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showToolbar("College Drivers", false)

        mDriver = findViewById(R.id.driver) as Button
        mCustomer = findViewById(R.id.customer) as Button

        mDriver!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, DriverLoginActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })

        mCustomer!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, CustomerLoginActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
    }

    fun showToolbar(title: String, upButton: Boolean) {
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(upButton)
    }
}
