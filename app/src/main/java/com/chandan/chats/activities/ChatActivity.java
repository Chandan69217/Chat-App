package com.chandan.chats.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chandan.chats.R;
import com.chandan.chats.databinding.ActivityChatBinding;
import com.chandan.chats.model.User;
import com.chandan.chats.utilities.Constants;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User userReceived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceivedDetails();
    }

    private void loadReceivedDetails(){
        userReceived = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(userReceived.name);
        binding.profileImage.setImageBitmap(getUserProfile(userReceived.image));
    }

    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    private Bitmap getUserProfile(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}