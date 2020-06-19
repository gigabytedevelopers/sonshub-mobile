package com.gigabytedevelopersinc.apps.sonshub.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.models.MainListModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.ClickListener;
import com.gigabytedevelopersinc.apps.sonshub.utils.OnBottomReachedListener;

import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MyViewHolder> {
    private Context context;
    private List<MainListModel> list;
    private ClickListener listener;
    OnBottomReachedListener onBottomReachedListener;


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView title, description, time;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.music_image);
            title = view.findViewById(R.id.music_title);
            description = view.findViewById(R.id.music_title_description);
            time = view.findViewById(R.id.music_post_time);
        }
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener;
    }

    public MainListAdapter(Context context, List<MainListModel> list,ClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MainListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        MyViewHolder myViewHolder;
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.music_list_item,viewGroup,false);

                myViewHolder = new MyViewHolder(view);
                view.setOnClickListener(view1 -> listener.onItemClick(view1,myViewHolder.getAdapterPosition()));
                return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainListAdapter.MyViewHolder holder, int i) {

        int lastPosition = -1;
        Animation animation = AnimationUtils.loadAnimation(context,
                (i > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);


        holder.itemView.startAnimation(animation);
        lastPosition = i;
        if (i == list.size() - 1) {
            onBottomReachedListener.onBottomReached(i);
        }

                MainListModel mainListModel = list.get(i);
                Glide.with(context)
                        .load(mainListModel.getImageUrl())
                        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                        .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                        .into(holder.image);
                holder.title.setText(Html.fromHtml(mainListModel.getTitle()), TextView.BufferType.SPANNABLE);
                holder.description.setText(Html.fromHtml(mainListModel.getDescription()), TextView.BufferType.SPANNABLE );
                holder.time.setText(mainListModel.getTime());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
