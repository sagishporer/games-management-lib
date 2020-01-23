package com.games.management.hiddenPlayers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.games.management.base.BaseSimpleTextViewHolder;
import com.games.management.OnListItemClickListener;
import com.google.api.services.gamesManagement.model.HiddenPlayer;

public class HiddenPlayerListAdapter extends PagedListAdapter<HiddenPlayer, HiddenPlayerListAdapter.HiddenPlayerHolder> {
    private OnListItemClickListener<HiddenPlayer> mOnItemClickListener;

    HiddenPlayerListAdapter(@NonNull DiffUtil.ItemCallback<HiddenPlayer> diffCallback, @NonNull OnListItemClickListener<HiddenPlayer> onItemClickListener) {
        super(diffCallback);

        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public HiddenPlayerListAdapter.HiddenPlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new HiddenPlayerListAdapter.HiddenPlayerHolder(viewItem, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HiddenPlayerListAdapter.HiddenPlayerHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class HiddenPlayerDiffUtil extends DiffUtil.ItemCallback<HiddenPlayer> {
        @Override
        public boolean areItemsTheSame(@NonNull HiddenPlayer oldItem, @NonNull HiddenPlayer newItem) {
            return oldItem.getPlayer().getPlayerId().equals(newItem.getPlayer().getPlayerId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HiddenPlayer oldItem, @NonNull HiddenPlayer newItem) {
            return (oldItem.equals(newItem));
        }
    }

    static class HiddenPlayerHolder extends BaseSimpleTextViewHolder<HiddenPlayer> {
        HiddenPlayerHolder(@NonNull View itemView, @NonNull OnListItemClickListener<HiddenPlayer> onItemClickListener) {
            super(itemView, onItemClickListener);
        }

        @Override
        protected int getTextViewResourceId() {
            return android.R.id.text1;
        }

        @Override
        protected String getStringFromItem(HiddenPlayer item) {
            return item.getPlayer().getDisplayName();
        }
    }
}
