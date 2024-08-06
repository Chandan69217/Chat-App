package com.chandan.chats.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chandan.chats.databinding.ItemContainerReceivedMessageBinding;
import com.chandan.chats.databinding.ItemContainerSendMessageBinding;
import com.chandan.chats.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
     private  Bitmap userProfile;
     private final List<ChatMessage> chats;
     private final String senderID;
     public static final int VIEW_TYPE_SEND = 1;
     public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(Bitmap userProfile, List<ChatMessage> chats, String senderID) {
        this.userProfile = userProfile;
        this.chats = chats;
        this.senderID = senderID;
    }

    public void setUserProfile(Bitmap bitmap){
        if(bitmap != null)
        this.userProfile = bitmap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND){
            return new SendMessageViewHolder(ItemContainerSendMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }else{
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(getItemViewType(position) == VIEW_TYPE_SEND){
                ((SendMessageViewHolder) holder).setData(chats.get(position));
            }else{
                ((ReceivedMessageViewHolder) holder).setData(chats.get(position),userProfile);
            }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chats.get(position).senderId.equals(senderID)){
            return VIEW_TYPE_SEND;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSendMessageBinding binding;
        public SendMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding) {
            super(itemContainerSendMessageBinding.getRoot());
            this.binding = itemContainerSendMessageBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;
        public ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            this.binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage,Bitmap userProfile){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if(userProfile !=null)
            binding.profileImage.setImageBitmap(userProfile);
        }

    }
}
