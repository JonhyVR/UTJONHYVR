package jvr.utcuates
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * Created by Jonathan on 11/02/2018.
 */

class Coordenadas : AppCompatActivity() {

    private var mFbTextView: TextView? = null
    internal var mDatabaseReference = FirebaseDatabase.getInstance().reference
    internal var mRootChild = mDatabaseReference.child("customerRequest")//.child("l")


    //private Button mLatitude;
    //private Button mLongitude;

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_costumer_map)
        mFbTextView = findViewById(R.id.Prueba) as TextView
    }

    override fun onStart() {
        super.onStart()

        mRootChild.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val customerRequest = dataSnapshot.value!!.toString()
                //val l = dataSnapshot.value!!.toString()
                mFbTextView!!.text = customerRequest
                // mFbTextView!!.text = l

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}