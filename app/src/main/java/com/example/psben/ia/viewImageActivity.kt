package com.example.psben.ia

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.firebase.database.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_image.*

class viewImageActivity : AppCompatActivity() {

    var b: Bundle? = null
    var Url: String? = null
    var userName: String? = null
    var eMail:String? = null
    var email1:String? = null
    var userPhoto: String? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var keyValue: String? = null
    var dialog: ACProgressFlower? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_view_image)
        // Progress bar for loading data
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        b = intent.extras
        Url = b!!.getString("Url")
        userPhoto = b!!.getString("userPhoto")
        userName = b!!.getString("userName")
        tvViewImage.text = userName
        loadMail()
        loadImage()
        var query: Query = myRef.child("requests").orderByChild("content").equalTo(Url)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (requests:DataSnapshot in p0.children) {
                        loadKey(requests.key.toString())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    fun loadImage() {
        Picasso.get().load(Url).into(ivViewImage, object: Callback{
            override fun onSuccess() {
                dialog!!.dismiss()
            }

            override fun onError(e: java.lang.Exception?) {

            }

        })
    }

    fun loadKey(valueKey:String) {
        keyValue = valueKey
    }
    fun loadMail() {
        myRef.child("Users").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                try {
                    var td = p0!!.value as HashMap<String, Any>
                    for (key in td.keys) {
                        var post = td[key] as HashMap<String, Any>
                        if (post["photo"] == userPhoto) {
                            loadEmail(post["email"].toString())
                        }
                    }
                } catch (e:Exception) {

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    fun loadEmail(email:String) {
        email1 = email
    }

    fun buAccept(view: View) {
        myRef.child("requests").child(keyValue.toString()).child("status").setValue("accepted")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email1.toString()))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Club Application Status")
        intent.putExtra(Intent.EXTRA_TEXT, "We are really exited to tell that you are accepted in the club")
        startActivity(intent)
        finish()
    }

    fun buReject(view: View) {
        myRef.child("requests").child(keyValue.toString()).child("status").setValue("rejected")
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email1.toString()))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Club Application Status")
        intent.putExtra(Intent.EXTRA_TEXT, "We are really sorry to tell you but you were not accepted in the club")
        startActivity(intent)
        finish()
    }
}
