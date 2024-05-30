package com.example.amplify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerMusicAdapter extends RecyclerView.Adapter<RecyclerMusicAdapter.ViewHolder>{

    Context context;
    ArrayList<MusicModel> arrMusic;
    SelectListener listener;

    RecyclerMusicAdapter(Context context , ArrayList<MusicModel> arrMusic, SelectListener listener){
        this.context = context;
        this.arrMusic = arrMusic;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.music_row, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicModel musicModel = arrMusic.get(holder.getAdapterPosition());

        // Load song image using Glide
        Glide.with(context)
                .load(musicModel.getImageUrl())
                .placeholder(R.drawable.song_img_placeholder)
                .into(holder.imgSong);

        holder.txtSongName.setText(musicModel.getSongName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(musicModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrMusic.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtSongName , txtSongSinger;
        ImageView imgSong;
        CardView cardView;
        public ViewHolder(View itemView){
            super(itemView);

            txtSongName = itemView.findViewById(R.id.songName);
            imgSong = itemView.findViewById(R.id.songImg);
            cardView = itemView.findViewById(R.id.cardContainer);
        }
    }
}
