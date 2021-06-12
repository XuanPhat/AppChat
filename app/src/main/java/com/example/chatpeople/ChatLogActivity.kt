package com.example.chatpeople

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatpeople.LatestMessagesActivity.Companion.CurrentUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
      val gerUsername= intent.getStringExtra("Username")
 supportActionBar?.title= gerUsername
//    SettupDummyData()
        ListenForMessages()
        findViewById<Button>(R.id.send_button_chat_log).setOnClickListener {
            performSendMessage()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val toId=intent.getStringExtra("uid")
        return when (item.itemId) {
            android.R.id.home -> {
                val intent= Intent(this, LatestMessagesActivity::class.java)
                val fromId=FirebaseAuth.getInstance().uid;

//                val Toreference =FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push();

//               val toId2= intent.putExtra("uid", toId)
                val text=findViewById<EditText>(R.id.edit_text_chat_log).text.toString();
//                val latestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
                Log.d("ChatLogActivity","fromId: "+fromId)

                Log.d("ChatLogActivity","toId: "+toId)
//                val reference =FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push();
//                val latestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
//                val TolatestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
//                latestMessageref.setValue(ChatMessage(reference.key!!, text, fromId!!, intent.getStringExtra("uid"), System.currentTimeMillis() / 1000, reference.key!!))
//                TolatestMessageref.setValue( ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), System.currentTimeMillis() / 1000, reference.key!!))
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun ListenForMessages()
    {
        val recycleviewchatlog= findViewById<RecyclerView>(R.id.recycleview_chat_log)
        val adapter = GroupAdapter<GroupieViewHolder>()
        val fromId=FirebaseAuth.getInstance().uid;
        val toId=intent.getStringExtra("uid")
        val ref =FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        val Toref =FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
        ref.addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                        val Chatmessage = snapshot.getValue(ChatMessage::class.java)
                        Log.d("ChatLogActivity", "Added Message " + Chatmessage)
                        if (Chatmessage != null) {
                            if (Chatmessage.fromId == FirebaseAuth.getInstance().uid) {
                                val ToUser = intent.getStringExtra("profileImageUrl")
                                adapter.add(ChatToItem(Chatmessage.text, RegisterActivity.User(intent.getStringExtra("uid"),
                                        intent.getStringExtra("Username"), CurrentUser?.profileImageUrl)))
                            } else {

                                adapter.add(ChatFromItem(Chatmessage.text, RegisterActivity.User(intent.getStringExtra("uid"),
                                        intent.getStringExtra("Username"), intent.getStringExtra("profileImageUrl"))))
                            }


                        }
                        recycleviewchatlog.adapter = adapter


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {

                    }
                }
        )


    }
    class ChatMessage(
            val id: String,
            val text: String,
            val fromId: String,
            val toId: String?,
            val timeStamp: String,
            val readmessageId: String

    )
    {
        constructor():this("", "", "", "", "", "")
    }

    private fun   performSendMessage()
    {
        val recycleviewchatlog= findViewById<RecyclerView>(R.id.recycleview_chat_log)
        val text=findViewById<EditText>(R.id.edit_text_chat_log).text.toString();
        val fromId=FirebaseAuth.getInstance().uid;
        val toId=intent.getStringExtra("uid")
       val reference =FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push();
        val Toreference =FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push();
    if (fromId == null) return;
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        val currentDate = sdf.format(Date())
        Log.d("LatestMessagesActivity","Time: "+currentDate)
        val adapter = GroupAdapter<GroupieViewHolder>()
    reference.setValue(ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), currentDate, reference.key!!))
        .addOnSuccessListener {
            Log.d("ChatLogActivity", "Added message " + reference.key)
            findViewById<EditText>(R.id.edit_text_chat_log).text.clear()

        }
        Toreference.setValue(ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), currentDate, reference.key!!))
                .addOnSuccessListener {
                    Log.d("ChatLogActivity", "Added message " + reference.key)
                    findViewById<EditText>(R.id.edit_text_chat_log).text.clear()
                }


            val Messages=ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), currentDate, "")
        val Messages2=ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), currentDate, "")

//        if(Messages.fromId==FirebaseAuth.getInstance().uid)
//        {
//
//        }


    val latestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
    val TolatestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

    latestMessageref.setValue(ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), currentDate,""))
    TolatestMessageref.setValue( ChatMessage(reference.key!!, text, fromId, intent.getStringExtra("uid"), currentDate, ""))
    }
//    private  fun SettupDummyData()
//    {
//        val recycleviewchatlog= findViewById<RecyclerView>(R.id.recycleview_chat_log)
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(ChatFromItem("Hello Thắng đỉ"))
//        adapter.add(ChatToItem("J mày"))
//        adapter.add(ChatFromItem("Hello Thắng đỉ"))
//        adapter.add(ChatToItem("J mày"))
//        recycleviewchatlog.adapter=adapter
//
//
//    }
    class ChatFromItem(val text: String, val user: RegisterActivity.User): Item<GroupieViewHolder>()
    {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
          viewHolder.itemView.findViewById<TextView>(R.id.textview_from_row).text=text
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageview_chat_from_row))
        }

        override fun getLayout(): Int {
            return R.layout.chat_from_row;
        }
    }
    class ChatToItem(val text: String, val user: RegisterActivity.User): Item<GroupieViewHolder>()
    {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.textview_to_row).text=text
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageview_chat_to_row))
        }

        override fun getItemCount(): Int {
            return super.getItemCount()
        }

        override fun getLayout(): Int {
            return R.layout.chat_to_row;
        }
    }

}
