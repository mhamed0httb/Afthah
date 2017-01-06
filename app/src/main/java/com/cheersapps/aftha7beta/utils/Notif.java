package com.cheersapps.aftha7beta.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.cheersapps.aftha7beta.ChatActivity;
import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.entity.MyNotif;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Created by Mohamed on 1/2/2017.
 */

public class Notif {
    Date now;
    Context context;
    String Sendername="";
    public Notif(final Context context){
        this.context = context;
        final Firebase ref = new Firebase("https://aftha7-2a05e.firebaseio.com/notif");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()){
                        MyNotif pi = msgSnapshot.getValue(MyNotif.class);


                        if(pi.getRecipient()!=null && pi.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                           // dataSnapshot.getRef().child("recipient").removeValue();

                            FirebaseDatabase.getInstance().getReference("notif").orderByChild("recipient").equalTo(FirebaseAuth
                            .getInstance()
                            .getCurrentUser()
                            .getUid()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                @Override
                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                    Firebase firebase = new Firebase("https://aftha7-2a05e.firebaseio.com/notif");
                                    firebase.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            MyNotif n = dataSnapshot.getValue(MyNotif.class);
                                            if(n.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                dataSnapshot.getRef().removeValue();
                                            }
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
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    PendingIntent pIntent = PendingIntent.getActivity(context,0,intent,0);
                                    Notification notif = new Notification.Builder(context).setTicker("Afthah")
                                            .setContentTitle("Someone texted you")
                                            .setContentText("Tap to go inbox")
                                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                            .setContentIntent(pIntent).getNotification();
                                    notif.flags = Notification.FLAG_AUTO_CANCEL;
                                    NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                                        nm.notify(0,notif);




                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //Toast.makeText(context,msg.getContext(),Toast.LENGTH_SHORT).show();




                        }


                    }
                }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
