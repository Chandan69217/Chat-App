package com.chandan.chats.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chandan.chats.databinding.ItemContainerUserBinding;
import com.chandan.chats.listeners.UserListener;
import com.chandan.chats.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private ItemContainerUserBinding binding;
    private final List<User> users;
    private final UserListener userListener;

    public UserAdapter(List<User> users,UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }

    private Bitmap getUserProfile(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public UserViewHolder(ItemContainerUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        private void setUserData(User user){
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.profileImage.setImageBitmap(getUserProfile(user.image));
            binding.getRoot().setOnClickListener(v-> userListener.onUserClicked(user));
        }
    }
}
