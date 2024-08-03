package com.chandan.chats.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chandan.chats.R;
import com.chandan.chats.adapters.ChatAdapter;
import com.chandan.chats.databinding.ActivityChatBinding;
import com.chandan.chats.model.ChatMessage;
import com.chandan.chats.model.User;
import com.chandan.chats.utilities.Constants;
import com.chandan.chats.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User userReceived;
    private List<ChatMessage> chatMessage;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private ChatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceivedDetails();
        init();
        listenMessages();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessage = new ArrayList<>();
        chatAdapter = new ChatAdapter(getUserProfile(userReceived.image),chatMessage,preferenceManager.getString(Constants.KEY_USER_ID));
        database = FirebaseFirestore.getInstance();
        binding.chatRecyclerView.setAdapter(chatAdapter);
    }

    private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,userReceived.id)
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,userReceived.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_SENDER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
            if(error != null){
                return;
            }
            if(value != null){
                int count = value.size();
                for(DocumentChange documentChange : value.getDocumentChanges()){
                   if(documentChange.getType() == DocumentChange.Type.ADDED){
                       ChatMessage chatMessage1 = new ChatMessage();
                       chatMessage1.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                       chatMessage1.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                       chatMessage1.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                       chatMessage1.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                       chatMessage1.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                       chatMessage.add(chatMessage1);
                   }
                }

                Collections.sort(chatMessage,(obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
                if(count == 0){
                    chatAdapter.notifyDataSetChanged();
                }else{
                    chatAdapter.notifyItemRangeInserted(chatMessage.size(),chatMessage.size());
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessage.size() - 1);
                }
                binding.chatRecyclerView.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
    });

    private void sendMessage(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,userReceived.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
    }

    private void loadReceivedDetails(){
        userReceived = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(userReceived.name);
        binding.profileImage.setImageBitmap(getUserProfile(userReceived.image));
    }

    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.sendLayout.setOnClickListener(v->{
            if(!binding.inputMessage.getText().toString().trim().equals(null)){
                sendMessage();
            }
        });
    }

    private Bitmap getUserProfile(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}