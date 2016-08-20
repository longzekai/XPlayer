package com.xapp.jjh.xplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xapp.jjh.xplayer.R;
import com.xapp.jjh.xplayer.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taurus on 16/8/20.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoHolder>{

    private List<VideoInfo> mList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public VideoListAdapter(Context context, List<VideoInfo> list){
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(View.inflate(mContext,R.layout.item_video,null));
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, final int position) {
        VideoInfo videoInfo = mList.get(position);
        holder.tv_name.setText(videoInfo.getDisplayName());
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class VideoHolder extends RecyclerView.ViewHolder{

        TextView tv_name;

        public VideoHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView.ViewHolder holder,int position);
    }

}
