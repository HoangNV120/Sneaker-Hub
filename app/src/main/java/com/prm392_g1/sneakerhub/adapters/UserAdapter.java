package com.prm392_g1.sneakerhub.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.prm392_g1.sneakerhub.R;
import com.prm392_g1.sneakerhub.entities.User;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users = new ArrayList<>();
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
        void onUserBan(User user);
        void onUserUnban(User user);
        void onUserDelete(User user);
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView textUserName, textUserEmail, textUserPhone, textUserStatus;
        private ImageView imageUserAvatar, btnBanUser, btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.text_user_name);
            textUserEmail = itemView.findViewById(R.id.text_user_email);
            textUserPhone = itemView.findViewById(R.id.text_user_phone);
            textUserStatus = itemView.findViewById(R.id.text_user_status);
            imageUserAvatar = itemView.findViewById(R.id.image_user_avatar);
            btnBanUser = itemView.findViewById(R.id.btn_ban_user);
            btnDeleteUser = itemView.findViewById(R.id.btn_delete_user);
        }

        public void bind(User user) {
            textUserName.setText(user.name != null ? user.name : "Unknown User");
            textUserEmail.setText(user.email != null ? user.email : "No email");
            textUserPhone.setText(user.phone_number != null ? user.phone_number : "No phone");

            // Set user status - user.is_banned is primitive boolean, so no need for null check
            if (user.is_banned) {
                textUserStatus.setText("Banned");
                textUserStatus.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
                btnBanUser.setImageResource(android.R.drawable.ic_menu_revert);
            } else {
                textUserStatus.setText("Active");
                textUserStatus.setBackgroundColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
                btnBanUser.setImageResource(android.R.drawable.ic_delete);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });

            btnBanUser.setOnClickListener(v -> {
                if (listener != null) {
                    if (user.is_banned) {
                        listener.onUserUnban(user);
                    } else {
                        listener.onUserBan(user);
                    }
                }
            });

            btnDeleteUser.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserDelete(user);
                }
            });
        }
    }
}
