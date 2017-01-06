package com.cheersapps.aftha7beta;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cheersapps.aftha7beta.adapter.UsersAdapter;
import com.cheersapps.aftha7beta.entity.Message;
import com.cheersapps.aftha7beta.entity.User;
import com.cheersapps.aftha7beta.utils.SharedData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NewMessageActivity extends AppCompatActivity {

    private static final String TAG = "UserList" ;
    private DatabaseReference userlistReference;
    private ValueEventListener mUserListListener;
    ArrayList<String> usernamelist = new ArrayList<>();
    ArrayList<User> userz = new ArrayList<>();
    ArrayList<User> filteredUserz = new ArrayList<>();
    User receiver ;
    ArrayList<String> usernamelistFiltered = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    FirebaseAuth mAuth;
    ListView userListView;
    private Firebase mRef;
    EditText search;
    ArrayList<Message> msgs = new ArrayList<>();
    List<Message> receivedM = new ArrayList<>();
    List<Message> sentM = new ArrayList<>();

    Context context;
    AdapterView<?> finalparent;

    Date now;
    int finalposition;
    String recipientMail,sender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        sender = mAuth.getCurrentUser().getUid().toString();
        setContentView(R.layout.activity_new_message);
        userListView = (ListView) findViewById(R.id.userlistview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Message");
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        search = (EditText) findViewById(R.id.et_search);

        context = this;
        //userListView.setBackgroundColor(LTGRAY);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Firebase Ref = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");

                Ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                        Message m = dataSnapshot.getValue(Message.class);
                        if(m.getContext().equals("456@eemzpatycheck789123"))
                            dataSnapshot.getRef().removeValue();
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



                User usr = (User) userListView.getAdapter().getItem(position);
                //chosenOne = usr.getUid();
                Intent in = new Intent(NewMessageActivity.this,DisplayMsgActivity.class);
                in.putExtra("INDEX", usr.getUid());
                in.putExtra("TASWIRTOU",usr.getImage());
                in.putExtra("mail", usr.getMail());
                in.putExtra("name",usr.getName());
                in.putExtra("pass",usr.getPass());


                //traitement
                List<String> whatISaid = new ArrayList<String>();
                List<String> whatHeSaid = new ArrayList<String>();
                for(Message msg : msgs){


                    if (msg.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        //if(!sentM.contains(msg))
                        sentM.add(msg);
                    }

                    else if(msg.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        // if(!receivedM.contains(msg))
                        receivedM.add(msg);


                    }

                }
                for(Message iSaid : sentM){
                    if(iSaid.getRecipient().equals(usr.getUid()) && !iSaid.getContext().contains("456@eemzpatycheck789123")){
                        whatISaid.add(iSaid.getContext()+"@/msgTime"+iSaid.getTodayDate());

                    }
                }
                for(Message HeSaid : receivedM){
                    if(HeSaid.getSender().equals(usr.getUid())&& !HeSaid.getContext().contains("456@eemzpatycheck789123")){
                        whatHeSaid.add(HeSaid.getContext()+"@/msgTime"+HeSaid.getTodayDate());
                    }
                }


                //endTraitement
                SharedData.currentWhatHeSaid=whatHeSaid;
                SharedData.currentWhatISaid=whatISaid;
               /* for(String str : whatHeSaid){
                   if(str.contains("456@eemzpatycheck789123")){
                       whatHeSaid.remove(str);
                   }
                }
                for(String str : whatHeSaid){
                    if(str.contains("456@eemzpatycheck789123")){
                        whatISaid.remove(str);
                }
                }*/

                in.putStringArrayListExtra("heSaid",(ArrayList<String>) whatHeSaid);
                in.putStringArrayListExtra("iSaid",(ArrayList<String>) whatISaid);
                in.putExtra("howmuch",whatISaid.size()+"*"+whatHeSaid.size());


                startActivity(in);
                arrayAdapter.notifyDataSetChanged();


            }


        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                filteredUserz.clear();
                for(User usr : userz){
                    if(usr.getName()!=null && usr.getName().toLowerCase().contains(s.toString().toLowerCase())){
                        filteredUserz.add(usr);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
     /*   recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String name : usernamelist){
                    if(name.contains(search.getText().toString())){
                        usernamelistFiltered.add(name);
                    }
                }
                arrayAdapter = new ArrayAdapter(NewMessageActivity.this,android.R.layout.simple_list_item_1,usernamelistFiltered);
                userListView.setAdapter(arrayAdapter);
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                for(String name : usernamelist){
                    if(name.contains(s.toString())){
                        usernamelistFiltered.add(name);
                    }
                }
                arrayAdapter = new ArrayAdapter(NewMessageActivity.this,android.R.layout.simple_list_item_1,usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                for(String name : usernamelist){
                    if(name.contains(s.toString())){
                        usernamelistFiltered.add(name);
                    }
                }
                arrayAdapter = new ArrayAdapter(NewMessageActivity.this,android.R.layout.simple_list_item_1,usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                for(String name : usernamelist){
                    if(name.contains(s.toString())){
                        usernamelistFiltered.add(name);
                    }
                }
                arrayAdapter = new ArrayAdapter(NewMessageActivity.this,android.R.layout.simple_list_item_1,usernamelist);
                userListView.setAdapter(arrayAdapter);
            }
        });*/

    }

    private void loadMail() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_sendmessage);

        dialog.show();
        final EditText context = (EditText) dialog.findViewById(R.id.context);
        final Button go = (Button) dialog.findViewById(R.id.send_button);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //.orderByChild("name").equalTo((String) finalparent.getItemAtPosition(finalposition))
               /* Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users" );
                ownerRef
                        .orderByChild("name")
                        .equalTo((String) finalparent.getItemAtPosition(finalposition))
                        ;

                ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        User mail = dataSnapshot.getValue(User.class);
                        recipientMail = mail.getMail();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/
               /* Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + mAuth.getCurrentUser().getUid().toString());
                ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        User currentUser = dataSnapshot.getValue(User.class);
                            sender = currentUser.getName();

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/

                mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");
                //User receiver = (User) finalparent.getItemAtPosition(finalposition);
                // User receiver = (User) userListView.getSelectedItem();
                String hisUid ="";
                for(User element : userz){
                    if(element.getName().equals(receiver.getName())){
                        hisUid = element.getUid();
                    }
                }
                Calendar c = Calendar.getInstance();
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);
                int hour = c.get(Calendar.HOUR);
                int minutes = c.get(Calendar.MINUTE);
                int seconds = c.get(Calendar.SECOND);


                String todayDate = month + "/" + day + "/" + year+" "+hour+":"+minutes+":"+seconds ;

                mRef.push().setValue(new Message(context.getText().toString(),hisUid,sender,todayDate));

                Toast.makeText(NewMessageActivity.this,hisUid,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();


        Log.i(TAG, "onDataChange: "+usernamelist.toString());
        arrayAdapter = new UsersAdapter(NewMessageActivity.this,R.layout.one_user,filteredUserz);
        userListView.setAdapter(arrayAdapter);
        mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                final User pi = dataSnapshot.getValue(User.class);
                pi.setUid(dataSnapshot.getKey());
                usernamelist.add(pi.getName());
                userz.add(pi);
                filteredUserz.add(pi);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mUserListListener != null) {
            userlistReference.removeEventListener(mUserListListener);
        }
    }
}
