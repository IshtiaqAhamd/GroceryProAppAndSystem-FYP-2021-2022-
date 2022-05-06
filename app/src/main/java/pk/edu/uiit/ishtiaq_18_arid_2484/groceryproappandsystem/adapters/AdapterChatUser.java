package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.activities.ChatActivity;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelChatUser;

public class AdapterChatUser extends RecyclerView.Adapter<AdapterChatUser.HolderChatUser>{
    Context chatHomeActivity;
    ArrayList<ModelChatUser> modelChatUserArrayList;

    public AdapterChatUser(Context chatHomeActivity, ArrayList<ModelChatUser> modelChatUserArrayList) {
        this.chatHomeActivity = chatHomeActivity;
        this.modelChatUserArrayList = modelChatUserArrayList;
    }

    @NonNull
    @Override
    public HolderChatUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(chatHomeActivity).inflate(R.layout.row_chat_users, parent, false);
        return new HolderChatUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChatUser holder, int position) {
        ModelChatUser users = modelChatUserArrayList.get(position);
        holder.userNameTv.setText(users.getName());
        holder.userStatusTv.setText(users.getEmail());

        try {
            Picasso.get().load(users.getImageUri()).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
        }
        catch (Exception e){
            holder.profileIv.setImageResource(R.drawable.ic_person_gray);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatHomeActivity, ChatActivity.class);
                intent.putExtra("name", users.getName());
                intent.putExtra("ReceiverImage", users.getImageUri());
                intent.putExtra("uid", users.getUid());
                chatHomeActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelChatUserArrayList.size();
    }

    // Holder Class
    class HolderChatUser extends RecyclerView.ViewHolder {
        CircleImageView profileIv;
        TextView userNameTv, userStatusTv;
        public HolderChatUser(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            userNameTv = itemView.findViewById(R.id.userNameTv);
            userStatusTv = itemView.findViewById(R.id.userStatusTv);
        }
    }

}
