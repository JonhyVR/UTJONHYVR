package jvr.utcuates

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.util.Log
import android.view.View
=======
import android.support.v7.app.AppCompatActivity
>>>>>>> 0320d6eb7fc263ba7d23c011a9120b0d9577013c
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class DriverLoginActivity : AppCompatActivity() {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mLogin: Button? = null
    private var mRegistration: Button? = null

    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null

     //private var db:FirebaseDatabase = FirebaseDatabase.getInstance();



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

<<<<<<< HEAD
        registrar()
        login()


    }


    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthListener!!)
    }

    override fun registrar() {
        mRegistration!!.setOnClickListener {
            if (mEmail!!.text.toString().length < 8 || mPassword!!.text.toString().length < 5  ) return@setOnClickListener;

            val email = mEmail!!.text.toString() +  "@utmetropolitana.edu.mx"
            val password = mPassword!!.text.toString()

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@DriverLoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@DriverLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                } else {
                    val user_id = mAuth!!.currentUser!!.uid
                    val current_user_db = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(user_id)
                    current_user_db.setValue(true)
=======
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
>>>>>>> 0320d6eb7fc263ba7d23c011a9120b0d9577013c
                }
        }

        mLogin!!.setOnClickListener {
            val email = mEmail!!.text.toString() +  "@utmetropolitana.edu.mx"
            val password = mPassword!!.text.toString()
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@DriverLoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@DriverLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


<<<<<<< HEAD
=======
    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(firebaseAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthListener!!)
    }
>>>>>>> 0320d6eb7fc263ba7d23c011a9120b0d9577013c
}
