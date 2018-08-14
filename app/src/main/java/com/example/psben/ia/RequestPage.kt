package com.example.psben.ia

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_request_page.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RequestPage : AppCompatActivity() {
    // adminUid = uid
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var mStorageRef: StorageReference? = null
    var ref:StorageReference? = null
    var filePath: Uri? = null
    var contentType: String? = null
    val AUDIO = 1
    val VIDEO = 1
    var IMAGE = 1
    var clubName: String? = null
    var clubCategory: String? = null
    var clubId: String? = null
    var clubReq: String? = null
    var userName: String? = null
    var userUid: String? = null
    var clubPhoto: String? = null
    var adminUid: String? = null
    var adminName: String? = null
    var userPhoto:String? = null
    var dialog: ACProgressFlower? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_request_page)

        //Progress bar for uploading data
        mStorageRef = FirebaseStorage.getInstance().reference
        var tvClubCategory = findViewById<TextView>(R.id.tvClubCategory)
        var tvClubReq = findViewById<TextView>(R.id.textView3)
        var ivClubLogo = findViewById<ImageView>(R.id.ivClubLogo)
        clubName = intent.getStringExtra("clubName")
        clubCategory = intent.getStringExtra("category")
        clubId = intent.getStringExtra("clubId")
        clubReq = intent.getStringExtra("clubReq")
        userName = intent.getStringExtra("userName")
        userUid = intent.getStringExtra("userUid")
        clubPhoto = intent.getStringExtra("photo")
        adminUid = intent.getStringExtra("adminUid")
        adminName = intent.getStringExtra("adminName")
        userPhoto = intent.getStringExtra("userPhoto")
        tvClubName5.text = clubName
        tvClubCategory.text = clubCategory
        tvClubReq.text = clubReq
        Picasso.get().load(clubPhoto).into(ivClubLogo)
    }

    fun uploadVideo(view: View) {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO)
        contentType = "video"
    }

    fun uploadAudio(view: View) {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO)
        contentType = "audio"
    }

    fun uploadImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE)
        contentType = "image"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && data != null && data.data != null) {
            if (requestCode == IMAGE) {
                filePath = data.data
            } else if (requestCode == VIDEO) {
                filePath = data.data
            } else {
                filePath = data.data
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun buRequest(view: View) {
        if (filePath != null) {
            uploadData(contentType.toString(), filePath!!)
            dialog = ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Uploading...")
                    .fadeColor(Color.DKGRAY).build()
            dialog!!.show()
        } else {
            Toast.makeText(this, "Please upload a file according to the requirements!!", Toast.LENGTH_LONG).show()
        }
    }

    var downloadUrl:Uri? = null
    fun uploadData(contentType:String, filePath:Uri) {
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Uploading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataObject = Date()
        val imagePath = userName + clubName + "." + df.format(dataObject)
        if (contentType == "image") {
            ref = mStorageRef!!.child("request images/$imagePath")
        }
        else if (contentType == "video") {
            ref = mStorageRef!!.child("request videos/$imagePath")
        }
        else {
            ref = mStorageRef!!.child("request audios/$imagePath")
        }
        var uploadTask = ref!!.putFile(filePath)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    ref!!.downloadUrl
                }

                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Applied Successfully!!!", Toast.LENGTH_SHORT).show()
                        downloadUrl = task.result
                        // TODO: CONTINUE
                        var status:String = "Pending..."
                        myRef.child("requests").push().setValue(newRequest(adminUid.toString(), userUid.toString(), userName.toString(), userPhoto.toString(), clubId.toString(), clubName.toString(), clubPhoto.toString(), contentType, downloadUrl.toString(), status))
                        dialog!!.dismiss()
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Apply Unsuccessful!!", Toast.LENGTH_LONG).show()
                    }
                }
    }
}
