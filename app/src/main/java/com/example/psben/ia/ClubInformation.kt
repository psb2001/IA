package com.example.psben.ia

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ClubInformation : AppCompatActivity() {
    // adminUid = uid
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var didApply: Boolean = false
    var isAdmin: Boolean = false
    var name:String? = null
    var category:String? = null
    var photo:String? = null
    var des:String? = null
    var adminName:String? = null
    var adminUid:String? = null
    var clubReq:String? = null
    var clubId:String? = null
    var userUid:String? = null
    var userName:String? = null
    var userMail:String? = null
    var userPhoto:String? = null
    var b: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_information)
        b = intent.extras
        var tvClubName = findViewById<TextView>(R.id.tvClubName5)
        var tvClubCategory = findViewById<TextView>(R.id.tvClubCategory)
        var tvClubDes = findViewById<TextView>(R.id.tvClubDes)
        var tvClubAdmin = findViewById<TextView>(R.id.textView5)
        var tvClubReq = findViewById<TextView>(R.id.textView3)
        var ivClubLogo = findViewById<ImageView>(R.id.ivClubLogo)
        name = b!!.getString("name")
        category = b!!.getString("category")
        photo = b!!.getString("photo")
        des = b!!.getString("description")
        adminName = b!!.getString("adminName")
        adminUid = b!!.getString("uid")
        clubReq = b!!.getString("clubReq")
        clubId = b!!.getString("clubId")
        userUid = b!!.getString("userUid")
        userName = b!!.getString("userName")
        userMail = b!!.getString("userMail")
        userPhoto = b!!.getString("userPhoto")
        tvClubName.text = name
        tvClubCategory.text = category
        tvClubDes.text = des
        tvClubAdmin.text = adminName
        tvClubReq.text = clubReq
        Picasso.get().load(photo).into(ivClubLogo)
        loadClub()
    }

    fun loadClub() {
        myRef.child("requests")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            var td = p0!!.value as HashMap<String, Any>
                            for (key in td.keys) {
                                var post = td[key] as HashMap<String, Any>
                                if (post["userUid"] == userUid && post["clubId"] == clubId) {
                                    isTrueApplied(true)
                                }
                            }
                        } catch (e:Exception) {

                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }

    fun isTrueApplied(boolean: Boolean) {
        this.didApply = boolean
    }

    fun onJoin(view: View) {
        if (adminUid == userUid) {
            Toast.makeText(this, "You are the admin of the club!!", Toast.LENGTH_LONG).show()
        } else if (didApply) {
            Toast.makeText(this, "You have already applied to this club!!", Toast.LENGTH_LONG).show()
        }else {
            var intent = Intent(this, RequestPage::class.java)
            intent.putExtra("clubName", name)
            intent.putExtra("category", category)
            intent.putExtra("clubId", clubId)
            intent.putExtra("clubReq", clubReq)
            intent.putExtra("userName", userName)
            intent.putExtra("userUid", userUid)
            intent.putExtra("userPhoto", userPhoto)
            intent.putExtra("photo", photo)
            intent.putExtra("adminUid", adminUid)
            intent.putExtra("adminName", adminName)
            startActivity(intent)
        }
    }
}
