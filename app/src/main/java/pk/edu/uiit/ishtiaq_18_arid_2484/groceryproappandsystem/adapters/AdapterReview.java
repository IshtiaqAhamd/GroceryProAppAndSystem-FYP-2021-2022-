package pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.R;
import pk.edu.uiit.ishtiaq_18_arid_2484.groceryproappandsystem.models.ModelReview;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.HolderReview> {
    Context context;
    ArrayList<ModelReview> reviewArrayList;

    public AdapterReview(Context context, ArrayList<ModelReview> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public HolderReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout row_review.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_review, parent, false);
        return new HolderReview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReview holder, int position) {
        // Get Dta At Position
        ModelReview modelReview = reviewArrayList.get(position);
        String uid = modelReview.getUid();
        String ratings = modelReview.getRatings();
        String timestamp = modelReview.getTimestamp();
        String review = modelReview.getReview();

        // We Also Need Info (Profile, Name) Of User Who Wrote The Review: We Can Do It Using uid Of User
        loadUserDetail(modelReview, holder);

        // Convert Timestamp To Proper Format dd/MM/yyyy
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();
        // Set Data
        holder.ratingBar.setRating(Float.parseFloat(ratings));
        holder.reviewTv.setText(review);
        holder.dateTv.setText(dateFormat);
    }

    private void loadUserDetail(ModelReview modelReview, HolderReview holder) {
        // uid Of User Who Wrote Review
        String uid = modelReview.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Get User Info, Use Same Key As In Firebase
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        // Set Data
                        holder.nameTV.setText(name);
                        try{
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                        }
                        catch (Exception e){
                            // If Anything Goes Wrong Setting Image (Exception Occur), Set Default Image
                            holder.profileIv.setImageResource(R.drawable.ic_store_gray);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    // View Holder Class, Hold/Initialize Views Of RecyclerView
    class HolderReview extends RecyclerView.ViewHolder{
        // UI Views Of Layout row_review.xml
        ImageView profileIv;
        TextView nameTV, dateTv, reviewTv;
        RatingBar ratingBar;
        public HolderReview(@NonNull View itemView) {
            super(itemView);
            // Initialization Views Of row_review.xml
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTV = itemView.findViewById(R.id.nameTV);
            dateTv = itemView.findViewById(R.id.dateTv);
            reviewTv = itemView.findViewById(R.id.reviewTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
