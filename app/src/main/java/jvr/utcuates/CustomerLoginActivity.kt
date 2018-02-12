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
import jvr.utcuates.Base.BaseLogin

class CustomerLoginActivity : AppCompatActivity(),BaseLogin {

    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mLogin: Button? = null
    private var mRegistration: Button? = null

    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_login)

        mAuth = FirebaseAuth.getInstance()

        firebaseAuthListener = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@CustomerLoginActivity, CustomerMapActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }

        mEmail = findViewById(R.id.email) as EditText
        mPassword = findViewById(R.id.password) as EditText

        mLogin = findViewById(R.id.login) as Button
        mRegistration = findViewById(R.id.registration) as Button

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


    override fun registrar(){
        mRegistration!!.setOnClickListener {

            if (mEmail!!.text.toString().length < 8 || mPassword!!.text.toString().length < 5  ) return@setOnClickListener;

            val email = mEmail!!.text.toString() + "@utmetropolitana.edu.mx"
            val password = mPassword!!.text.toString()



            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@CustomerLoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@CustomerLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                } else {
                    val user_id = mAuth!!.currentUser!!.uid
                    val current_user_db = FirebaseDatabase.getInstance().reference.child("Users").child("Customers").child(user_id)
                    current_user_db.setValue(true)
                }
            }
        }

    }

    override fun login (){
        mLogin!!.setOnClickListener {
            val email = mEmail!!.text.toString() +  "@utmetropolitana.edu.mx"
            val password = mPassword!!.text.toString()
            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@CustomerLoginActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@CustomerLoginActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}
