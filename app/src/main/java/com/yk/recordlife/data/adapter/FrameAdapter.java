package com.yk.recordlife.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yk.recordlife.R;
import com.yk.recordlife.data.bean.Frame;

import java.util.List;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.ViewHolder> {
    private Context context;
    private List<Frame> frameList;

    public FrameAdapter(List<Frame> frameList) {
        this.frameList = frameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_frame, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Frame frame = frameList.get(position);
        Glide.with(context).load(frame.getBitmap()).into(holder.ivFrame);
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        AppCompatImageView ivFrame;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ivFrame = itemView.findViewById(R.id.iv_frame);
        }
    }

}
