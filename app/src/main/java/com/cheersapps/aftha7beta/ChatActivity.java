package com.cheersapps.aftha7beta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.cheersapps.aftha7beta.adapter.UsersAdapter;
import com.cheersapps.aftha7beta.entity.Message;
import com.cheersapps.aftha7beta.entity.User;
import com.cheersapps.aftha7beta.utils.Notif;
import com.cheersapps.aftha7beta.utils.SharedData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ListView msgListView;
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> senders = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    Firebase mRef;
    String currentName;
    ListView contenu;
    ArrayList<String> others = new ArrayList<>();
    ArrayList<Message> msgs = new ArrayList<>();
    ArrayList<User> userz = new ArrayList<>();
    Context context = this;
    int finalpos=0;
    Map convo = new HashMap<String,List<String>>();
    List<HashMap<String,List<String>>> convos ;
    List<Message> receivedM ;
    List<Message> sentM ;
    Map<HashMap<String,List<String>>,HashMap<String,List<String>>> both;

    List<String> talkHim ;
    List<String> talkMe;
    String chosenOne ="";
    List<String> WhatISaid ;
    List<String> WhatHeSaid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Inbox");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                // .setAction("Action", null).show();
                senders.clear();
                msgListView.setAdapter(null);
                startActivity(new Intent(ChatActivity.this,NewMessageActivity.class));
            }
        });
        msgListView = (ListView) findViewById(R.id.msglistview);
        // msgListView.setBackgroundColor(LTGRAY);
        contenu = (ListView) findViewById(R.id.contenu);
        receivedM = new ArrayList<>();
        sentM = new ArrayList<>();
        talkHim = new ArrayList<>();



        talkMe = new ArrayList<>();
        both = new HashMap<>();
        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Firebase Ref = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");

                Ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message m = dataSnapshot.getValue(Message.class);
                        if(m.getContext().equals("456@eemzpatycheck789123"))
                            dataSnapshot.getRef().removeValue();
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



                User usr = (User) msgListView.getAdapter().getItem(position);
                //chosenOne = usr.getUid();
                Intent in = new Intent(ChatActivity.this,DisplayMsgActivity.class);
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

        new Notif(ChatActivity.this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        senders.clear();

        contenu = (ListView) findViewById(R.id.contenu);



        arrayAdapter = new UsersAdapter(ChatActivity.this,R.layout.one_user,userz);
        msgListView.setAdapter(arrayAdapter);

        mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");
        mRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                final Message pi = dataSnapshot.getValue(Message.class);

                msgs.add(pi);

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


        userz = new ArrayList<User>(new LinkedHashSet<User>(userz));
        arrayAdapter = new UsersAdapter(ChatActivity.this,R.layout.one_user,userz);
        msgListView.setAdapter(arrayAdapter);

        mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                final Message pi = dataSnapshot.getValue(Message.class);
                String uid ="";
                if(pi.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                    senders.add(pi.getContext());

                    if(!others.contains(pi.getSender())){


                        others.add(pi.getSender());




                        arrayAdapter.notifyDataSetChanged();

                        Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + pi.getSender());
                        ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                            @Override
                            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                currentUser.setUid(dataSnapshot.getKey());
                                //EVERYTHING ELSE HERE

                                if(!userz.contains(currentUser))

                                    userz.add(currentUser);



                                arrayAdapter.notifyDataSetChanged();
                            }


                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    }

                    arrayAdapter.notifyDataSetChanged();
                }
                if(pi.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                    senders.add(pi.getContext());

                    if(!others.contains(pi.getRecipient())){




                        others.add(pi.getRecipient());




                        arrayAdapter.notifyDataSetChanged();

                        Firebase ownerRef = new Firebase("https://aftha7-2a05e.firebaseio.com/users/" + pi.getRecipient());
                        ownerRef.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                            @Override
                            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                currentUser.setUid(dataSnapshot.getKey());
                                //EVERYTHING ELSE HERE
                                if(!userz.contains(currentUser))
                                    userz.add(currentUser);
                                ////
                                mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");

                                Calendar c = Calendar.getInstance();
                                int month = c.get(Calendar.MONTH);
                                int day = c.get(Calendar.DAY_OF_MONTH);
                                int year = c.get(Calendar.YEAR);
                                int hour = c.get(Calendar.HOUR);
                                int minutes = c.get(Calendar.MINUTE);
                                int seconds = c.get(Calendar.SECOND);


                                String todayDate = month + "/" + day + "/" + year+" "+hour+":"+minutes+":"+seconds ;

                                mRef.push().setValue(new Message("456@eemzpatycheck789123",FirebaseAuth.getInstance().getCurrentUser().getUid(),currentUser.getUid(),todayDate));
                                ////

                                arrayAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }

                    arrayAdapter.notifyDataSetChanged();
                }



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
    protected void onPause() {
        super.onPause();
        new Notif(ChatActivity.this);
    }

}
