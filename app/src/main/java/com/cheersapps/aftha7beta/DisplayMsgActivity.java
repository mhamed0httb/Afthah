package com.cheersapps.aftha7beta;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.cheersapps.aftha7beta.adapter.ChatAdapter;
import com.cheersapps.aftha7beta.adapter.CircleTransform;
import com.cheersapps.aftha7beta.entity.Message;
import com.cheersapps.aftha7beta.entity.MyNotif;
import com.cheersapps.aftha7beta.entity.User;
import com.cheersapps.aftha7beta.utils.Notif;
import com.cheersapps.aftha7beta.utils.SharedData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class DisplayMsgActivity extends AppCompatActivity {

    public ListView lv ;
    public String index,hisPic,myPic;
    List<String> convoHim = new ArrayList<>();
    List<String> convoMe = new ArrayList<>();
    List<String> convo = new ArrayList<>();
    List<Message> msgUs = new ArrayList<>();
    ImageView hisImg;
    TextView hisName;






    ArrayAdapter arrayAdapter;
    ArrayList<String> messages = new ArrayList<>();
    Firebase mRef;
    EditText send;
    Date now;
    ArrayList<Message> msgs = new ArrayList<>();
    User usr ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_msg);

        SharedData.context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        send = (EditText) findViewById(R.id.editTextsend);


        hisImg = (ImageView) findViewById(R.id.pic_chat);
        hisName = (TextView) findViewById(R.id.name_chat);
        convoMe = new ArrayList<String>(new LinkedHashSet<String>(convoMe));
        convoHim = new ArrayList<String>(new LinkedHashSet<String>(convoHim));
        //class





        //

        Picasso.with(this).load(getIntent().getStringExtra("TASWIRTOU")).transform(new CircleTransform()).centerCrop()
                .resize(100,100).into(hisImg);
        SpannableString spanString = new SpannableString(getIntent().getStringExtra("name"));
        //spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        // spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
        hisName.setText(spanString);
        setSupportActionBar(toolbar);

        hisPic = getIntent().getStringExtra("TASWIRTOU");
        String h = getIntent().getStringExtra("howmuch");
        //Toast.makeText(DisplayMsgActivity.this,h,Toast.LENGTH_SHORT).show();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(send.getText().toString()!=null){
                    // mp1.start();

                    mRef = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");

                    Calendar c = Calendar.getInstance();
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int year = c.get(Calendar.YEAR);
                    int hour = c.get(Calendar.HOUR);
                    int minutes = c.get(Calendar.MINUTE);
                    int seconds = c.get(Calendar.SECOND);

                    String todayTime = hour+":"+minutes+":"+seconds;
                    new SharedData();
                    Toast.makeText(DisplayMsgActivity.this,SharedData.daten,Toast.LENGTH_SHORT).show();
                    Log.e("NOW",SharedData.daten);

                    //2017-01-04T12:22:26+00:00

                   // String[] parts = SharedData.daten.split("-");
                   // String today = parts[2] + "/" + parts[3].substring(0,1) + "/" + parts[1] + " " +  parts[3].substring(3,4)
//5,7 // 8,10//0,4
                           // +":"+ parts[3].substring(6,7)+":"+ parts[3].substring(9,10);
                    String today = SharedData.daten.substring(5,7)+"/"+SharedData.daten.substring(8,10)+"/"+SharedData.daten.substring(0,4)
                            +" "+SharedData.daten.substring(11,13)+":"+SharedData.daten.substring(14,16)+":"+SharedData.daten.substring(17,19);

                            //Toast.makeText(DisplayMsgActivity.this,today,Toast.LENGTH_SHORT).show();
                    Log.e("today",today);
                   ;
                    String todayDate = month + "/" + day + "/" + year+" "+hour+":"+minutes+":"+seconds ;
                    try {
                        now = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(todayDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.e("today",todayDate);



                    arrayAdapter.notifyDataSetChanged();





                    if(send.getText().toString().equalsIgnoreCase("")){
                        Toast.makeText(DisplayMsgActivity.this,"Type something first",Toast.LENGTH_SHORT).show();
                    }else{
                        Firebase mmRef = new Firebase("https://aftha7-2a05e.firebaseio.com/notif");
                        mmRef.push().setValue(new MyNotif(index));
                        mRef.push().setValue(new Message(send.getText().toString(),index, FirebaseAuth.getInstance().getCurrentUser().getUid(),today ));



                        String date = todayDate;
                        try {
                            now = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(todayDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        msgUs.add(new Message("1",send.getText().toString(),now));

                        //msgUs = new ArrayList<Message>(new LinkedHashSet<Message>(msgUs));
                        arrayAdapter.notifyDataSetChanged();
                        send.setText(null);

                       // Toast.makeText(DisplayMsgActivity.this,"Mail successfully sent",Toast.LENGTH_SHORT).show();
                    }




                    arrayAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(DisplayMsgActivity.this,"Type something first",Toast.LENGTH_SHORT).show();
                }


            }


        });
        lv = (ListView) findViewById(R.id.contenu_box);
        index = getIntent().getStringExtra("INDEX");

        convoMe = getIntent().getStringArrayListExtra("iSaid");
        convoHim = getIntent().getStringArrayListExtra("heSaid");

        convoMe = new ArrayList<String>(new LinkedHashSet<String>(convoMe));
        convoHim = new ArrayList<String>(new LinkedHashSet<String>(convoHim));
        List<Message> msgMe = new ArrayList<>();
        List<Message> msgHim = new ArrayList<>();


        for(String star : convoHim){
            String date = star.substring(star.indexOf("@/msgTime")+9);
            try {
                now = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            msgUs.add(new Message("2",star.substring(0,star.indexOf("@/msgTime")),now));
            //Toast.makeText(DisplayMsgActivity.this,star.substring(0,star.indexOf("@/msgTime")),Toast.LENGTH_SHORT).show();


        }
        for(String star : convoMe){
            String date = star.substring(star.indexOf("@/msgTime")+9);
            try {
                now = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            msgMe.add(new Message("1",star.substring(0,star.indexOf("@/msgTime")),now));
            msgUs.add(new Message("1",star.substring(0,star.indexOf("@/msgTime")),now));
            //Toast.makeText(DisplayMsgActivity.this,star.substring(0,star.indexOf("@/msgTime")),Toast.LENGTH_SHORT).show();

        }


        arrayAdapter = new ChatAdapter(DisplayMsgActivity.this,R.layout.one_chat,msgUs,hisPic,myPic);

        lv.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();




        Firebase ref = new Firebase("https://aftha7-2a05e.firebaseio.com/messages");
        ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()){
                    Message pi = msgSnapshot.getValue(Message.class);

                    if(pi.getSender()!=null && pi.getSender().equals(index)&&pi.getRecipient().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        // Toast.makeText(DisplayMsgActivity.this,pi.getContext(),Toast.LENGTH_SHORT).show();
                        String date = pi.getTodayDate();
                        try {
                            now = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Message msg = new Message("2",pi.getContext(),now);



                        if(!msgUs.contains(msg)){
                            msgUs.add(msg);

                            //msgUs = new ArrayList<Message>(new LinkedHashSet<Message>(msgUs));

                        }


                    }

                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        new Notif(DisplayMsgActivity.this);
    }

}
