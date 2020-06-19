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
import com.gigabytedevelopersinc.apps.sonshub.models.NewsModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.ClickListener;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context context;
    private List<NewsModel> list;
    private ClickListener listener;

    public NewsAdapter(Context context, List<NewsModel> list, ClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView newsTitle, newsDescription, newsTime;
        private ImageView newsImage;
        public MyViewHolder(View view) {
            super(view);
            newsTitle = view.findViewById(R.id.gist_title_text);
            newsDescription = view.findViewById(R.id.gist_title_description);
            newsTime = view.findViewById(R.id.gist_post_time);
            newsImage = view.findViewById(R.id.news_image);
        }
    }
    @NonNull
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.news_list_item,viewGroup,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        view.setOnClickListener(view1 -> listener.onItemClick(view1, myViewHolder.getAdapterPosition()));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.MyViewHolder holder, int i) {
        NewsModel newsModel = list.get(i);
        Glide.with(context)
                .load(newsModel.getNewsImage())
                .apply (RequestOptions.placeholderOf(R.drawable.placeholder))
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(new RequestOptions().override(512,512))
                .into(holder.newsImage);

        holder.newsTitle.setText(Html.fromHtml(newsModel.getNewsTitle()), TextView.BufferType.SPANNABLE);
        holder.newsDescription.setText(Html.fromHtml(newsModel.getNewsDescription()), TextView.BufferType.SPANNABLE);
        holder.newsTime.setText(newsModel.getNewsTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
