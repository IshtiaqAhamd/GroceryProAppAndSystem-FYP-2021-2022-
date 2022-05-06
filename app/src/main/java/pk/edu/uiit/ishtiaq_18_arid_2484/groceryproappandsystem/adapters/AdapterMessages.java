package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelMessages;

public class AdapterMessages extends RecyclerView.Adapter {
    Context context;
    ArrayList<ModelMessages> messagesArrayList;
    int ITEM_SAND = 1;
    int ITEM_RECEIVE = 2;

    public AdapterMessages(Context context, ArrayList<ModelMessages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SAND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item, parent, false);
            return  new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout_item, parent, false);
            return  new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelMessages messages = messagesArrayList.get(position);

        if (holder.getClass() == SenderViewHolder.class)
        {
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            viewHolder.senderMessageTv.setText(messages.getMessage());
//            Picasso.get().load(sImage).into(viewHolder.senderProfileIv);
        }else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.receiverMessageTv.setText(messages.getMessage());
//            Picasso.get().load(rImag).into(viewHolder.receiverProfileIv);
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ModelMessages modelMessages = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(modelMessages.getSenderId()))
        {
            return ITEM_SAND;
        }else {
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        ImageView senderProfileIv;
        TextView senderMessageTv;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderProfileIv = itemView.findViewById(R.id.senderProfileIv);
            senderMessageTv = itemView.findViewById(R.id.senderMessageTv);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        ImageView receiverProfileIv;
        TextView receiverMessageTv;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverProfileIv = itemView.findViewById(R.id.receiverProfileIv);
            receiverMessageTv = itemView.findViewById(R.id.receiverMessageTv);
        }
    }
}
