package com.cheersapps.aftha7beta.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.cheersapps.aftha7beta.R;
import com.cheersapps.aftha7beta.entity.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Mohamed on 12/12/2016.
 */

public class ChatAdapter extends ArrayAdapter<Message> {

    private final static String TAG = "UserAdapter";
    private int resourceId = 0;
    private LayoutInflater inflater;
    Context context;
    String myPic="";
    String hisPic="";
    LinearLayout lnImine;
    LinearLayout lnIsar;
    List<Message> chats = null;
    List<Message> chaaats = null;
    MessageHolder holder;
    String ID;
    String NAME;

    Date convert;

    FirebaseAuth mAuth;
    public ChatAdapter(Context context, int resourceId, List<Message> chats, String hisPic, final String myPic) {

        super(context, resourceId, chats);
       // this.resourceId = resourceId;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        this.myPic = myPic;
        this.hisPic = hisPic;
        this.chats = chats;
        this.ID = ID;
        this.NAME = NAME;
        Collections.sort(chats);
       /* Firebase mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                if(m.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                    if(m.getRecipient().equals(ID)){
                        String t = m.getTodayDate();

                        try {
                            convert = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(t);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        chaaats.add(new Message("1",m.getContext().toString(),convert));
                        Collections.sort(chaaats);
                    }
                }else if(m.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    if(m.getSender().equals(NAME)){
                        String t = m.getTodayDate();

                        try {
                            convert = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(t);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        chaaats.add(new Message("0",m.getContext().toString(),convert));
                        Collections.sort(chaaats);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Message m = dataSnapshot.getValue(Message.class);
                if(m.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
                    if(m.getRecipient().equals(ID)){
                        String t = m.getTodayDate();

                        try {
                            convert = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(t);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        chaaats.add(new Message("1",m.getContext().toString(),convert));
                        Collections.sort(chaaats);
                    }
                }else if(m.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    if(m.getSender().equals(NAME)){
                        String t = m.getTodayDate();

                        try {
                            convert = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(t);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        chaaats.add(new Message("0",m.getContext().toString(),convert));
                        Collections.sort(chaaats);
                    }
                }
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
        });*/

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        LinearLayout a;
        View send;
        View recive;
        a = new LinearLayout(context);
        a.setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater li = LayoutInflater.from(context);

        //Collections.sort(chats);

            if(chats.get(position).getId().equals("1"))
            {
                send = li.inflate(R.layout.cell_conversation_flirtmessage_otheruser, null, false);
                holder = new MessageHolder();
                holder.msgName = (TextView) send.findViewById(R.id.text);
                //  holder.from = (TextView) send.findViewById(R.id.tagtext);
                send.setTag(holder);

                row=send;
            }


            else
            {
                recive = li.inflate(R.layout.cell_conversation_flirtmessage_currentuser, null, false);
                holder = new MessageHolder();
                holder.msgName = (TextView) recive.findViewById(R.id.text);
                //holder.from = (TextView) recive.findViewById(R.id.tagtext);
                recive.setTag(holder);
                row=recive;
            }


        //Collections.sort(chats);
        String messsage = chats.get(position).getContext();
        if(messsage.length()>15){

        }
        holder.msgName.setText(messsage);


        return row;
    }
    
    
    
    
    
    
    




    private class MessageHolder
    {

        TextView msgName;
        ImageView msgPics;


    }
}
