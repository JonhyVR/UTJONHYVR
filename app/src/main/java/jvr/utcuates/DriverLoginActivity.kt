package jvr.utcuates

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DriverLoginActivity : AppCompatActivity() {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mLogin: Button? = null
    private var mRegistration: Button? = null

    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login)

        mAuth = FirebaseAuth.getInstance()

        firebaseAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@DriverLoginActivity, DriverMapActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        mEmail = findViewById(R.id.email) as EditText
        mPassword = findViewById(R.id.password) as EditText

        mLogin = findViewById(R.id.login) as Button
        mRegistration = findViewById(R.id.registration) as Button

        mRegistration!!.setOnClickListener {
            val email = mEmail!!.text.toString()
            val password = mPassword!!.text.toString()
            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@DriverLoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@DriverLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                } else {
                    val user_id = mAuth!!.currentUser!!.uid
                    val current_user_db = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(user_id)
                    current_user_db.setValue(true)
                }
            }
        }

        mLogin!!.setOnClickListener {
            val email = mEmail!!.text.toString()
            val password = mPassword!!.text.toString()
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@DriverLoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@DriverLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthListener!!)
    }
}