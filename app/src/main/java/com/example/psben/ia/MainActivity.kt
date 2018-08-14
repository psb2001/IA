package com.example.psben.ia

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.clubticket.view.*
import kotlinx.android.synthetic.main.dialogticket.view.*


class MainActivity : AppCompatActivity() {

    // adminUid = uid
    private var mAuth: FirebaseAuth? = null
    var user:FirebaseUser? = null
    var loginManager:LoginManager? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var listClubs = ArrayList<ClubInfo>()
    var adapter:MyNotesAdapter? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var dialog: ACProgressFlower? = null
    var pin = "1234"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Progress bar for loading the whole list
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        loadClub()
        mAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()
        user = mAuth!!.currentUser
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this@MainActivity  /* OnConnectionFailedListener */) { }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        adapter = MyNotesAdapter(listClubs, this)
        listView.adapter = adapter
    }




    inner class MyNotesAdapter: BaseAdapter {
        var listClubs = ArrayList<ClubInfo>()
        var context: Context? = null
        constructor(listClubs: ArrayList<ClubInfo>, context: Context): super() {
            this.listClubs = listClubs
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myClub = listClubs[p0]
            var myView = inflater.inflate(R.layout.clubticket, null)
            myView.tvNameRequest.text = myClub.name
            myView.tvCategory.text = myClub.category
            Picasso.get().load(myClub.photo).into(myView.ivLogo, object: Callback{
                override fun onSuccess() {
                    dialog!!.dismiss()
                }

                override fun onError(e: java.lang.Exception?) {

                }

            })
            myView.linearLayout.setOnClickListener {
                // Intent to take to Club Information
                var intent = Intent(context, ClubInformation::class.java)
                intent.putExtra("name", myClub.name)
                intent.putExtra("category", myClub.category)
                intent.putExtra("photo", myClub.photo)
                intent.putExtra("uid", myClub.uid)
                intent.putExtra("adminName", myClub.adminName)
                intent.putExtra("description", myClub.description)
                intent.putExtra("clubReq", myClub.requirements)
                intent.putExtra("clubId", myClub.clubId)
                intent.putExtra("userUid", user!!.uid)
                intent.putExtra("userName", user!!.displayName)
                intent.putExtra("userMail", user!!.email)
                intent.putExtra("userPhoto", user!!.photoUrl.toString())
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
                                listClubs.add(ClubInfo(post["name"].toString(), post["category"].toString(), post["photo"].toString(), post["uid"].toString(), post["adminName"].toString(), post["description"].toString(), post["clubId"].toString(), post["requirements"].toString()))
                            }
                        } catch (e:Exception) {

                        }
                        adapter!!.notifyDataSetChanged()
                        if (listClubs.isEmpty()) {
                            Toast.makeText(applicationContext, "No clubs are made right now. Feel free to make one right now", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }

                })
    }

    fun logOut() {
        mAuth!!.signOut()
        loginManager!!.logOut()
        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null) {
            when(item.itemId) {
                R.id.logOut -> {
                    logOut()
                }
                R.id.newClub -> {
                    newClub()
                }
                R.id.manageRequest -> {
                    manageRequest()
                }
                R.id.manageRequestAdmin -> {
                    manageRequestAdmin()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun newClub() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        var inflater: LayoutInflater = layoutInflater
        var view: View = inflater.inflate(R.layout.dialogticket, null)
        builder.setView(view)
        builder.setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                dialog!!.dismiss()
            }

        })
        builder.setPositiveButton("Submit", object: DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if (view.editText.text.toString() == pin) {
                    dialog!!.dismiss()
                    Toast.makeText(applicationContext, "Correct pin!!", Toast.LENGTH_LONG).show()
                    var intent = Intent(applicationContext, NewClub::class.java)
                    intent.putExtra("uid", user!!.uid)
                    intent.putExtra("name", user!!.displayName)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Incorrect pin!!", Toast.LENGTH_LONG).show()
                }
            }

        })
        var dialog: Dialog = builder.create()
        dialog.show()
    }

    fun manageRequest() {
        var intent = Intent(this, RequestsActivity::class.java)
        intent.putExtra("uid", user!!.uid)
        intent.putExtra("name", user!!.displayName)
        startActivity(intent)
    }

    fun manageRequestAdmin() {
        var intent = Intent(this, admin_club_request1::class.java)
        intent.putExtra("uid", user!!.uid)
        intent.putExtra("name", user!!.displayName)
        startActivity(intent)
    }
}
