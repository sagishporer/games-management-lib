package com.games.management.base;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.games.management.OnListItemClickListener;
import com.games.management.R;

public abstract class BaseSimpleTextViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnListItemClickListener<T> mOnItemClickListener;
    private T mItem;

    protected BaseSimpleTextViewHolder(@NonNull View itemView, @NonNull OnListItemClickListener<T> onItemClickListener) {
        super(itemView);

        mOnItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    public void bind(T item) {
        mItem = item;

        TextView textView = itemView.findViewById(getTextViewResourceId());
        if (item != null)
            textView.setText(getStringFromItem(item));
        else
            textView.setText(R.string.please_wait);
    }

    protected abstract int getTextViewResourceId();
    protected abstract String getStringFromItem(T item);

    @Override
    public void onClick(View view) {
        // Not loaded yet. Ignore.
        if (mItem == null)
            return;

        mOnItemClickListener.onItemClick(mItem);
    }

}
