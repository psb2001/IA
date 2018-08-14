package com.example.psben.ia

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_new_club.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.dialogticket.view.*
import java.text.SimpleDateFormat
import java.util.*


class NewClub : AppCompatActivity() {
    // adminUid = uid
    private var mAuth: FirebaseAuth? = null
    var database = FirebaseDatabase.getInstance()
    var myRef = database.reference
    var filePath: Uri? = null
    var adminName:String? = null
    var adminUid:String? = null
    var mStorageRef: StorageReference? = null
    var imageSelected:Boolean? = null
    var dialog: ACProgressFlower? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_new_club)
        // Progress bar for uploading data
        mStorageRef = FirebaseStorage.getInstance().reference
        var b: Bundle = intent.extras
        adminName = b.getString("name")
        adminUid = b.getString("uid")
        ivClubLogo.setOnClickListener {
            choose()
        }
        imageSelected = false
        var tvClubName = findViewById<TextView>(R.id.tvClubName5)
        var tvClubCategory = findViewById<TextView>(R.id.tvClubCategory)
        var tvClubDes = findViewById<TextView>(R.id.tvClubDes)
        var tvClubReq = findViewById<TextView>(R.id.tvClubReq)
    }

    var PICK_IMAGE_REQUEST = 1
    fun choose() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
        checkPermission()
    }

    var readImage = 1
    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), readImage)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            readImage-> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Cannot access your images", Toast.LENGTH_LONG).show()
                }
            }
            else-> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            imageSelected = true
            Log.i("File Path Uri", filePath.toString())
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ivClubLogo.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var downloadUrl:Uri? = null
    fun uploadImage() {
        dialog = ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Loading...")
                .fadeColor(Color.DKGRAY).build()
        dialog!!.show()
        val df = SimpleDateFormat("ddMMyyHHmmss")
        val dataObject = Date()
        val imagePath = tvClubName5.text.toString() + "." + df.format(dataObject)
        var ref = mStorageRef!!.child("club logo images/$imagePath")
        var uploadTask = ref.putFile(filePath!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    ref.downloadUrl
                }

                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Club Created Successfully!!", Toast.LENGTH_LONG).show()
                        var clubId:String = UUID.randomUUID().toString()
                        downloadUrl = task.result
                        myRef.child("clubs").push().setValue(ClubInfo(tvClubName5.text.toString(), tvClubCategory.text.toString(), downloadUrl.toString(), adminUid!!, adminName!!, tvClubDes.text.toString(), clubId!!, tvClubReq.text.toString()))
                        dialog!!.dismiss()
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Club not Created!!", Toast.LENGTH_LONG).show()
                    }
                }

    }

    fun onSubmit(view:View) {
        if (tvClubName5.text.equals("") || tvClubCategory.text.equals("") || tvClubDes.text.equals("") || tvClubReq.text.equals("") || !imageSelected!!) {
            Toast.makeText(this, "Please fill in all the fields. Upload an image too!!", Toast.LENGTH_LONG).show()
        } else {
            uploadImage()
        }
    }

    fun onOutsideClick(view: View) {
        var im: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
