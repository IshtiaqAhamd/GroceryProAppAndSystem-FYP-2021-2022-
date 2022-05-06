package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters.AdapterMessages;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelMessages;

public class ChatActivity extends AppCompatActivity {
    String ReceiverImage, ReceiverName, ReceiverUid, SenderUid;
    ImageView profileIv;
    TextView nameTv;
    CardView sendBtn;
    EditText messageEt;

    public static String sImage;
    public static String rImage;
    String senderRoom, receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<ModelMessages> messageArrayList;
    AdapterMessages adapter;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ReceiverName = getIntent().getStringExtra("name");
        ReceiverImage = getIntent().getStringExtra("ReceiverImage");
        ReceiverUid = getIntent().getStringExtra("uid");
        messageArrayList = new ArrayList<>();
        SenderUid = firebaseAuth.getUid();
        senderRoom = SenderUid + ReceiverUid;
        receiverRoom = ReceiverUid + SenderUid;

        profileIv = findViewById(R.id.profileIv);
        Picasso.get().load(ReceiverImage).into(profileIv);
        try {
            Picasso.get().load(ReceiverImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
        } catch (Exception e) {
            profileIv.setImageResource(R.drawable.ic_person_gray);
        }
        nameTv = findViewById(R.id.nameTv);
        messageEt = findViewById(R.id.messageEt);
        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new AdapterMessages(ChatActivity.this, messageArrayList);
        messageAdapter.setAdapter(adapter);
        sendBtn = findViewById(R.id.sendBtn);
        nameTv.setText("" + ReceiverName);

        DatabaseReference reference = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());
        DatabaseReference chatReference = firebaseDatabase.getReference().child("Chats").child(senderRoom).child("messages");



        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ModelMessages modelMessage = dataSnapshot.getValue(ModelMessages.class);
                    messageArrayList.add(modelMessage);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("profileImage").getValue().toString();
                rImage = ReceiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEt.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please Enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                messageEt.setText("");
                Date date = new Date();
                ModelMessages modelMessage = new ModelMessages(message, SenderUid, date.getTime());
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("Chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(modelMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseDatabase.getReference().child("Chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push().setValue(modelMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        });
    }
}