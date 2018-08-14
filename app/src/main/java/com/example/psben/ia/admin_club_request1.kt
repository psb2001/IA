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
import kotlinx.android.synthetic.main.activity_admin_club_request1.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.clubticket.view.*

class admin_club_request1 : AppCompatActivity() {

    // adminUid = uid
    var listClubs = ArrayList<ClubInfo>()
    var adapter: MyNotesAdapter? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var dialog: ACProgressFlower? = null
    var userUid:String? = null
    var b: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_club_request1)
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        loadClub()
        adapter = MyNotesAdapter(listClubs, this)
        adminClubListView1.adapter = adapter
        b = intent.extras
        userUid = b!!.getString("uid")
    }

    inner class MyNotesAdapter : BaseAdapter {
        var listClubs = ArrayList<ClubInfo>()
        var context: Context? = null

        constructor(listClubs: ArrayList<ClubInfo>, context: Context) : super() {
            this.listClubs = listClubs
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myClub = listClubs[p0]
            var myView = inflater.inflate(R.layout.clubticket, null)
            myView.tvNameRequest.text = myClub.name
            myView.tvCategory.text = myClub.category
            Picasso.get().load(myClub.photo).into(myView.ivLogo)
            myView.linearLayout.setOnClickListener {
                var intent = Intent(applicationContext, AdminClubRequestActivity::class.java)
                intent.putExtra("clubId", myClub.clubId)
                startActivity(intent)
            }
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listClubs[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listClubs.size
        }
    }

    fun loadClub() {
        myRef.child("clubs")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        listClubs.clear()
                        try {
                            var td = p0!!.value as HashMap<String, Any>
                            for (key in td.keys) {
                                var post = td[key] as HashMap<String, Any>
                                if (post["uid"] == userUid)
                                    listClubs.add(ClubInfo(post["name"].toString(), post["category"].toString(), post["photo"].toString(), post["uid"].toString(), post["adminName"].toString(), post["description"].toString(), post["clubId"].toString(), post["requirements"].toString()))
                            }
                        } catch (e:Exception) {

                        }
                        adapter!!.notifyDataSetChanged()
                        dialog!!.dismiss()
                        if (listClubs.isEmpty()) {
                            finish()
                            Toast.makeText(applicationContext, "No clubs created", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }
}
