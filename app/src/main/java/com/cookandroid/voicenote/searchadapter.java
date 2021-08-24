/*package com.cookandroid.voicenote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
        holder.maintext.setText(memo.getMaintext());
        holder.subtext.setText(memo.getSubtext());
        
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


        private TextView maintext;
        private TextView subtext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            maintext = itemView.findViewById(R.id.item_maintext);
            subtext = itemView.findViewById(R.id.item_subtext);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(getApplicationContext(), Detail.class);
                        intent.putExtra("maintext", maintext.getText());
                        intent.putExtra("subtext", subtext.getText());
                        intent.putExtra("no", (int)maintext.getTag());
                        //intent.putExtra("pos",getAdapterPosition());

                        activity.startActivity(intent);
                    }
                }
            });

        }

    }

}*/