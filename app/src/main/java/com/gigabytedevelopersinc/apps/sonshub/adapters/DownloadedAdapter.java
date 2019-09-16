package com.gigabytedevelopersinc.apps.sonshub.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gigabytedevelopersinc.apps.sonshub.R;
import com.gigabytedevelopersinc.apps.sonshub.models.DownloadedModel;
import com.gigabytedevelopersinc.apps.sonshub.utils.DownloadListener;

import java.util.List;

public class DownloadedAdapter extends RecyclerView.Adapter<DownloadedAdapter.MyViewHolder> {

    private Context context;
    private List<DownloadedModel> list;
    private DownloadListener listener;

    public DownloadedAdapter(Context context, List<DownloadedModel> list, DownloadListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        public MyViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.title);
        }
    }
    @NonNull
    @Override
    public DownloadedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.downloaded_custom_layout,viewGroup,false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAudioOrVideoClick(view, myViewHolder.getAdapterPosition(),myViewHolder.title.getText().toString());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadedAdapter.MyViewHolder holder, int i) {
            DownloadedModel downloadedModel = list.get(i);
            holder.title.setText(downloadedModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
