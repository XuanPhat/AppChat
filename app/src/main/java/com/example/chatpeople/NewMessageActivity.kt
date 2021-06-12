package com.example.chatpeople

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class NewMessageActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
    supportActionBar?.title="Select User"
//        val recycleviewnewmessage=findViewById<RecyclerView>(R.id.recycleview_newmessage)
//        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        recycleviewnewmessage.adapter=adapter
         fetchUsers()
    }
    private  fun fetchUsers()
    {
val ref=FirebaseDatabase.getInstance().getReference("/users");
ref.addListenerForSingleValueEvent(object :ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {

        val adapter = GroupAdapter<GroupieViewHolder>()
        val recycleviewnewmessage=findViewById<RecyclerView>(R.id.recycleview_newmessage)
        snapshot.children.forEach{
            Log.d("NewMessageActivity",it.toString())
            val user=it.getValue(RegisterActivity.User::class.java)
            if(user!=null)
            {
                if(FirebaseAuth.getInstance().uid!=user.uid)
                {
                    adapter.add(UserItem(user))
                }

            }

        }
        adapter.setOnItemClickListener { item, view ->
            val UserItem=item as UserItem
            val intent= Intent(view.context,ChatLogActivity::class.java)
            intent.putExtra("Username",UserItem.user.username)
            intent.putExtra("profileImageUrl",UserItem.user.profileImageUrl)
            intent.putExtra("uid",UserItem.user.uid)
            startActivity(intent)
            finish()
        }
        recycleviewnewmessage.adapter=adapter
    }


    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }
}
)

    }
    class UserItem(val user: RegisterActivity.User): Item<GroupieViewHolder>()
    {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.username_textview_new_message).text= user.username;
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.imageview_new_message))
        }

        override fun getLayout(): Int {
            return  R.layout.user_row_new_message
        }
    }


}

