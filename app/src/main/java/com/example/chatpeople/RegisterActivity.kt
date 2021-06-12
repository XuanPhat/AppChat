package com.example.chatpeople

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         val Register = findViewById<Button>(R.id.login_button_register)
        Register.setOnClickListener {
            performRegister()
        }

      val Alreadyaccount=findViewById<TextView>(R.id.already_have_account)
        Alreadyaccount.setOnClickListener{
//            Log.d("MainActivity","Show already account")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val Selectphoto=findViewById<ImageView>(R.id.selectphoto_button_register)
        Selectphoto.setOnClickListener {
            Log.d("RegisterActivity", "Select photo")
            val intent=Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            intent.type="image/*"
          startActivityForResult(intent, 0)
        }
    }
 var selectedPhotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       val Selectphoto=findViewById<ImageView>(R.id.selectphoto_button_register)
        if(requestCode ==0 && resultCode == Activity.RESULT_OK && data != null)
        {
            Log.d("RegisterActivity", "image was seleted")
            selectedPhotoUri=data.data
//            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,uri);
//            val bitmapDrawable =BitmapDrawable(bitmap)
//            findViewById<Button>(R.id.selectphoto_button_register).setBackgroundDrawable(bitmapDrawable)
            Selectphoto.setImageURI(selectedPhotoUri)



        }
    }



    private fun performRegister()
    {
        val email= findViewById<EditText>(R.id.email_edittext_register).text.toString()
        val password=findViewById<EditText>(R.id.password_edittext_register).text.toString()
//            Log.d("MainActivity","Email is: "+email)
//            Log.d("MainActivity","Password: $password")

        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("RegisterActivity", "createUserWithEmail:success: " + auth.uid)

                        UploadImageToFirebaseStorage()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)

                        Toast.makeText(baseContext, "Enter email wrong",
                                Toast.LENGTH_SHORT).show()

                    }
                }

}

    private fun UploadImageToFirebaseStorage()
    {
        if(selectedPhotoUri==null) return;
 val filename=UUID.randomUUID().toString();
     val ref=  FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("RegisterActivity","Successly upload "+it)
            ref.downloadUrl.addOnSuccessListener {
                Log.d("RegisterActivity","get Image "+it)
                saveUserToFirebaseDatabase(it.toString())
            }.addOnFailureListener {
                Log.d("RegisterActivity","get Image Failed")
                }
        }
    }
    private lateinit var database: DatabaseReference
    private  fun saveUserToFirebaseDatabase(profileImageUrl: String)
    {
        val username=findViewById<EditText>(R.id.username_edittext_register)
        val uid=FirebaseAuth.getInstance().uid?:""
//       val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        database = Firebase.database.reference
         val user=User(uid,username.text.toString(), profileImageUrl)
        database.child("users").child(uid).setValue(user).addOnSuccessListener {
            Log.d("RegisterActivity","Register user successfully")
            val intent=Intent(this,LatestMessagesActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)


        }.addOnFailureListener {
                Log.d("RegisterActivity","save user Failed")
            }

    }

 class User(val uid:String?, val username: String? = null, val profileImageUrl:String?)
    {
        constructor() :this("","","")
    }


}