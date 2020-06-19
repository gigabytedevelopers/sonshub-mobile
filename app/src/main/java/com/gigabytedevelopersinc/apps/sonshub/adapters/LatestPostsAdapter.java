package com.gigabytedevelopersinc.apps.sonshub.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.models.MainListModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.ClickListener;
import com.gigabytedevelopersinc.apps.sonshub.utils.OnBottomReachedListener;

import java.util.List;

public class LatestPostsAdapter extends BaseAdapter {
    private Context context;
    private List<MainListModel> list;
    private ClickListener listener;
    OnBottomReachedListener onBottomReachedListener;

    public LatestPostsAdapter(Context context, List<MainListModel> list,  ClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {

        this.onBottomReachedListener = onBottomReachedListener;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        if (i == list.size() - 1) {
            onBottomReachedListener.onBottomReached(i);
        }

       MainListModel mainListModel = list.get(i);

        View gridView;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            gridView = inflater.inflate(R.layout.latestposts_item, viewGroup, false);


        } else {
            gridView = view;

        }

        ImageView image = gridView.findViewById(R.id.post_image);
        TextView title = gridView.findViewById(R.id.post_title);
        TextView description = gridView.findViewById(R.id.post_description);
        TextView time = gridView.findViewById(R.id.post_time);

        gridView.setOnClickListener(view1 -> listener.onItemClick(gridView,i));

        Glide.with(context)
                .load(mainListModel.getImageUrl())
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                .into(image);

        title.setText(mainListModel.getTitle());
        description.setText(mainListModel.getDescription());
        time.setText(mainListModel.getTime());

        return gridView;
    }
}
