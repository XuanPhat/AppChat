package com.example.chatpeople

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LatestMessagesActivity: AppCompatActivity()  {
    companion object{
      var CurrentUser: RegisterActivity.User?=null;
    }
    public val Tag:String="LatestMessagesActivity"
    val adapter =GroupAdapter<GroupieViewHolder>();
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        verifyUserLoggedIn()
        fetchCurrentUser()
        findViewById<RecyclerView>(R.id.recycleview_latest_messages).adapter=adapter
//        findViewById<RecyclerView>(R.id.recycleview_latest_messages).addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//        setupDummyRows()
        adapter.setOnItemClickListener { item, view ->
val intent=Intent(this, ChatLogActivity::class.java)
            val row =item as LatestMessagRow
            intent.putExtra("Username", row.ChatPartnerUser?.username)
            intent.putExtra("profileImageUrl", row.ChatPartnerUser?.profileImageUrl)
            intent.putExtra("uid", row.ChatPartnerUser?.uid)
            intent.putExtra("fromId", row.Chatmessage.fromId)
            intent.putExtra("toId2", row.Chatmessage.toId)
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
            val currentDate = sdf.format(Date())
            Log.d("LatestMessagesActivity","Time: "+currentDate)
            val fromId=FirebaseAuth.getInstance().uid;
            val toId=intent.getStringExtra("uid")
            if( row.Chatmessage.id!=row.Chatmessage.readmessageId && row.Chatmessage.fromId!=FirebaseAuth.getInstance().uid)
            {
                val latestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
                val TolatestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                val Messages= ChatLogActivity.ChatMessage(latestMessageref.key!!, row.Chatmessage.text, fromId!!, intent.getStringExtra("uid"), currentDate, latestMessageref.key!!)
                latestMessageref.setValue(Messages)
                TolatestMessageref.setValue(Messages)

            }

            startActivity(intent)
        }
        ListenForLatestMessages()

    }

    val latestMessageMap=HashMap<String, ChatLogActivity.ChatMessage>()

    private  fun ListenForLatestMessages()
    {
        val fromId=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val Chatmessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java)
                        ?: return
                latestMessageMap[snapshot.key!!] = Chatmessage
                adapter.clear()
                latestMessageMap.values.forEach {
                    adapter.add(LatestMessagRow(it))
                }
//                adapter.add(LatestMessagRow(Chatmessage))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val Chatmessage = snapshot.getValue(ChatLogActivity.ChatMessage::class.java)
                        ?: return
                latestMessageMap[snapshot.key!!] = Chatmessage
                adapter.clear()
                latestMessageMap.values.forEach {
                    adapter.add(LatestMessagRow(it))
                    Log.d("LatestMessagesActivity", "text " + Chatmessage.text)

                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

//    private fun  setupDummyRows()
//    {
//        val adapter =GroupAdapter<GroupieViewHolder>();
//        adapter.add(LatestMessagRow());
//        adapter.add(LatestMessagRow());
//        adapter.add(LatestMessagRow());
//        findViewById<RecyclerView>(R.id.recycleview_latest_messages).adapter=adapter
//
//
//    }

    class LatestMessagRow(val Chatmessage: ChatLogActivity.ChatMessage): Item<GroupieViewHolder>()
    {
        var ChatPartnerUser : RegisterActivity.User?=null
        @ExperimentalStdlibApi
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
val TextlatestBlack=viewHolder.itemView.findViewById<TextView>(R.id.textview_latest_messages)
            val Textlatest=viewHolder.itemView.findViewById<TextView>(R.id.textview_latest_messages)
//            Textlatest.setTextColor(Color.parseColor("#d2dae2"))
            val toId=ChatPartnerUser?.uid
            val fromId=FirebaseAuth.getInstance().uid;
            Log.d("LatestMessagesActivity", "get readmessage " + Chatmessage.readmessageId)
//            val loading=LoadingDialog(LatestMessagesActivity());
//            loading.startLoading();
//            val handler= Handler();
//            handler.postDelayed(object :Runnable{
//                override fun run() {
//                    loading.Dismiss();
//                }
//            },3000)
           if(Chatmessage.text.length < 21)
           {
               val Textmessage=Chatmessage.text+"..."
           }
            val textMessage=Chatmessage.text.length>15
            Log.d("LatestMessagesActivity","Length latest: "+Chatmessage.text)


            if(Chatmessage.id!=Chatmessage.readmessageId && Chatmessage.fromId!=FirebaseAuth.getInstance().uid )
            {

                    TextlatestBlack.typeface= Typeface.DEFAULT_BOLD
                    TextlatestBlack.setTextColor(Color.parseColor("#1e272e"))
                TextlatestBlack.text=Chatmessage.text
                    viewHolder.itemView.findViewById<TextView>(R.id.DotLatest).text="."

            }
            else
            {
                viewHolder.itemView.findViewById<TextView>(R.id.DotLatest).text=""
                TextlatestBlack.text=Chatmessage.text
            }
            viewHolder.itemView.findViewById<TextView>(R.id.timestamp).text= Chatmessage.timeStamp


            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
            val currentDate = sdf.format(Date())
            Log.d("LatestMessagesActivity","Time: "+currentDate)

            val ChatPartnerId: String
            if(Chatmessage.fromId==FirebaseAuth.getInstance().uid)
            {

                ChatPartnerId= Chatmessage.toId!!
            }
            else
            {
                ChatPartnerId= Chatmessage.fromId
            }
           val ref=FirebaseDatabase.getInstance().getReference("/users/$ChatPartnerId");
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ChatPartnerUser = snapshot.getValue(RegisterActivity.User::class.java);

                    viewHolder.itemView.findViewById<TextView>(R.id.textview_username_latest_messages).text = ChatPartnerUser?.username
                    Picasso.get().load(ChatPartnerUser?.profileImageUrl).into(viewHolder.itemView.findViewById<ImageView>(R.id.targetImageView))
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

    }

    private  fun fetchCurrentUser()
    {
        val uid=FirebaseAuth.getInstance().uid;
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid");
         ref.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 CurrentUser = snapshot.getValue(RegisterActivity.User::class.java)
                 Log.d("LatestMessagesActivity", "get user image" + CurrentUser?.profileImageUrl)
             }

             override fun onCancelled(error: DatabaseError) {

             }
         })

    }
    private fun verifyUserLoggedIn()
    {
        val uid= FirebaseAuth.getInstance().uid;
        if(uid==null)
        {
            val intent= Intent(this, RegisterActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut();
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}





