package com.cookandroid.voicenote;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class searchadapter extends RecyclerView.Adapter<searchadapter.ViewHolder>{

    ArrayList<Memo> MemoArrayList;
    Activity activity;


    public searchadapter(ArrayList<Memo> memoItemArrayList, Activity activity) {
        this.MemoArrayList = memoItemArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_food,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Memo memo = MemoArrayList.get(position);
        holder.text.setText(memo.getMaintext());
    }



    @Override
    public int getItemCount() {
        return MemoArrayList.size();
    }

    public void  filterList(ArrayList<Memo> filteredList) {
        MemoArrayList = filteredList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.text);

        }
    }
}