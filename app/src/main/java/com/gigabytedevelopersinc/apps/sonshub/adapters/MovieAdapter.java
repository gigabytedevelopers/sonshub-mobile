package com.gigabytedevelopersinc.apps.sonshub.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.models.MovieModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.ClickListener;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private Context context;
    private List<MovieModel> list;
    private ClickListener listener;

    public MovieAdapter(Context context, List<MovieModel> list, ClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView musicImage;
        private TextView  musicTextName;
        public MyViewHolder(View view) {
            super(view);
            musicImage = view.findViewById(R.id.music_image);
            musicTextName = view.findViewById(R.id.music_text_name);
        }
    }
    @NonNull
    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.music_list_item_2, viewGroup,false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        view.setOnClickListener(view1 -> listener.onItemClick(view1,myViewHolder.getAdapterPosition()));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MyViewHolder holder, int i) {
        MovieModel movieModel = list.get(i);

        Glide.with(context)
                .load(movieModel.getMovieImage())
                .apply (RequestOptions.placeholderOf(R.drawable.placeholder))
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(new RequestOptions().override(512,512))
                .into(holder.musicImage);
        holder.musicTextName.setText(Html.fromHtml(movieModel.getMovieTitle()), TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
