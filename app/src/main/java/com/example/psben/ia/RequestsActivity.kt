package com.example.psben.ia

import android.content.Context
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
import kotlinx.android.synthetic.main.activity_requests.*
import kotlinx.android.synthetic.main.admin_club_request_ticket.view.*
import kotlinx.android.synthetic.main.user_request_ticket.view.*

class RequestsActivity : AppCompatActivity() {

    var listRequests = ArrayList<newRequest>()
    var adapter: MyNotesAdapter? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var b: Bundle? = null
    var userUid:String? = null
    var dialog: ACProgressFlower? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)
        // Progress bar for loading requests
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        loadClub()
        b = intent.extras
        userUid = b!!.getString("uid")
        adapter = MyNotesAdapter(listRequests, this)
        listViewRequests.adapter = adapter
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listRequests = ArrayList<newRequest>()
        var context: Context? = null

        constructor(listRequests: ArrayList<newRequest>, context: Context) : super() {
            this.listRequests = listRequests
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myRequest = listRequests[p0]
            var myView = inflater.inflate(R.layout.user_request_ticket, null)
            myView.tvNameRequest.text = myRequest.clubName
            Picasso.get().load(myRequest.clubPhoto).into(myView.ivLogoRequest)
            if (myRequest.status == "accepted") {
                myView.tvStatus.text = "ACCEPTED"
                myView.tvStatus.setBackgroundResource(R.color.green)
            } else if (myRequest.status == "rejected") {
                myView.tvStatus.text = "REJECTED"
                myView.tvStatus.setBackgroundResource(R.color.red)
            } else {
                myView.tvStatus.text = "PENDING..."
            }
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listRequests[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listRequests.size
        }
    }



    fun loadClub() {
        myRef.child("requests")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        listRequests.clear()
                        try {
                            var td = p0!!.value as HashMap<String, Any>
                            for (key in td.keys) {
                                var post = td[key] as HashMap<String, Any>

                                if (post["userUid"] == userUid) {
                                    listRequests.add(newRequest(post["adminUid"].toString(), post["userUid"].toString(), post["userName"].toString(), post["userPhoto"].toString(), post["clubId"].toString(), post["clubName"].toString(), post["clubPhoto"].toString(), post["contentType"].toString(), post["content"].toString(), post["status"].toString()))
                                }
                            }
                        } catch (e:Exception) {

                        }
                        adapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        if (listRequests.isEmpty()) {
                            finish()
                            Toast.makeText(applicationContext, "No requests", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }
}
