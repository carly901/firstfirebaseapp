package com.example.firstfirebaseapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<chatMessage> messages = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public MessageAdapter() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messages = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                chatMessage m = new chatMessage(
                                        Objects.requireNonNull(document.get("userPhoto")).toString(),
                                        Objects.requireNonNull(document.get("userName")).toString(),
                                        Objects.requireNonNull(document.get("userID")).toString(),
                                        Objects.requireNonNull(document.get("message")).toString()
                                );
                                messages.add(m);
                            } catch (Exception ignored) {
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
        db.collection("chat").addSnapshotListener((value, error) -> {
            messages = new ArrayList<>();
            assert value != null;
            for (DocumentSnapshot document : value.getDocuments()) {
                try {
                    chatMessage m = new chatMessage(
                            Objects.requireNonNull(document.get("userPhoto")).toString(),
                            Objects.requireNonNull(document.get("userName")).toString(),
                            Objects.requireNonNull(document.get("userID")).toString(),
                            Objects.requireNonNull(document.get("message")).toString()
                    );
                    messages.add(m);
                } catch (Exception ignored) {
                }
            }
            notifyDataSetChanged();
        });
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        chatMessage message = messages.get(position);
        holder.message.setText(message.message);
        holder.userName.setText(message.userName);
        Glide.with(holder.userImage.getContext()).load(message.userPhoto).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(chatMessage m) {
        Map<String, Object> message = new HashMap<>();
        message.put("userPhoto", m.userPhoto);
        message.put("userName", m.userName);
        message.put("userID", m.userID);
        message.put("message",m.message);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat").document(String.valueOf(System.currentTimeMillis()))
                .set(message);
    }
}