package com.example.psben.ia

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_club_request.*
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.admin_club_request_ticket.view.*
import kotlinx.android.synthetic.main.user_request_ticket.view.*

class AdminClubRequestActivity : AppCompatActivity() {

    var listAdminRequests = ArrayList<newRequest>()
    var adapter: MyNotesAdapter? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var b: Bundle? = null
    var clubId:String? = null
    var dialog: ACProgressFlower? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_club_request)
        // Progress bar for loading requests
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        loadClub()
        b = intent.extras
        clubId = b!!.getString("clubId")
        adapter = MyNotesAdapter(listAdminRequests, this)
        listViewAdminClubRequest.adapter = adapter
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listAdminRequests = ArrayList<newRequest>()
        var context: Context? = null

        constructor(listAdminRequests: ArrayList<newRequest>, context: Context) : super() {
            this.listAdminRequests = listAdminRequests
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myAdminRequest = listAdminRequests[p0]
            var myView = inflater.inflate(R.layout.admin_club_request_ticket, null)
            // set Parameters in ticket
            myView.tvAdminUserName.text = myAdminRequest.userName
            myView.TvAdminClubName.text = myAdminRequest.clubName
            Picasso.get().load(myAdminRequest.userPhoto).into(myView.ivUserPhoto)
            if (myAdminRequest.status == "accepted") {
                myView.tvAdminStatus.text = "ACCEPTED"
                myView.tvAdminStatus.setBackgroundResource(R.color.green)
            } else if (myAdminRequest.status == "rejected") {
                myView.tvAdminStatus.text = "REJECTED"
                myView.tvAdminStatus.setBackgroundResource(R.color.red)
            } else {
                myView.tvAdminStatus.text = "PENDING..."
                myView.linearLayoutAdmin.setOnClickListener {
                    if (myAdminRequest.contentType == "image") {
                        var intent = Intent(applicationContext, viewImageActivity::class.java)
                        intent.putExtra("Url", myAdminRequest.content)
                        intent.putExtra("userName", myAdminRequest.userName)
                        intent.putExtra("userPhoto", myAdminRequest.userPhoto)
                        startActivity(intent)
                    } else if (myAdminRequest.contentType == "video") {
                        var intent = Intent(applicationContext, viewVideoActivity::class.java)
                        intent.putExtra("Url", myAdminRequest.content)
                        intent.putExtra("userName", myAdminRequest.userName)
                        intent.putExtra("userPhoto", myAdminRequest.userPhoto)
                        startActivity(intent)
                    } else {
                        var intent = Intent(applicationContext, viewAudioActivity::class.java)
                        intent.putExtra("Url", myAdminRequest.content)
                        intent.putExtra("userName", myAdminRequest.userName)
                        intent.putExtra("userPhoto", myAdminRequest.userPhoto)
                        startActivity(intent)
                    }
                }
            }
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listAdminRequests[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listAdminRequests.size
        }
    }

    fun loadClub() {
        myRef.child("requests")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        listAdminRequests.clear()
                        try {
                            var td = p0!!.value as HashMap<String, Any>
                            for (key in td.keys) {
                                var post = td[key] as HashMap<String, Any>
                                if (post["clubId"] == clubId) {
                                    listAdminRequests.add(newRequest(post["adminUid"].toString(), post["userUid"].toString(), post["userName"].toString(), post["userPhoto"].toString(), post["clubId"].toString(), post["clubName"].toString(), post["clubPhoto"].toString(), post["contentType"].toString(), post["content"].toString(), post["status"].toString()))
                                }
                            }
                        } catch (e:Exception) {

                        }
                        adapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        if (listAdminRequests.isEmpty()) {
                            finish()
                            Toast.makeText(applicationContext, "No requests", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }
}
