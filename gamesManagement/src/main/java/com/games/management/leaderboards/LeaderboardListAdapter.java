package com.games.management.leaderboards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.games.management.base.BaseSimpleTextViewHolder;
import com.games.management.OnListItemClickListener;
import com.google.api.services.games.model.Leaderboard;

public class LeaderboardListAdapter extends PagedListAdapter<Leaderboard, LeaderboardListAdapter.LeaderboardHolder> {
    private OnListItemClickListener<Leaderboard> mOnItemClickListener;

    LeaderboardListAdapter(@NonNull DiffUtil.ItemCallback<Leaderboard> diffCallback, @NonNull OnListItemClickListener<Leaderboard> onItemClickListener) {
        super(diffCallback);

        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LeaderboardListAdapter.LeaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new LeaderboardListAdapter.LeaderboardHolder(viewItem, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardListAdapter.LeaderboardHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class LeaderboardDiffUtil extends DiffUtil.ItemCallback<Leaderboard> {
        @Override
        public boolean areItemsTheSame(@NonNull Leaderboard oldItem, @NonNull Leaderboard newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Leaderboard oldItem, @NonNull Leaderboard newItem) {
            return (oldItem.equals(newItem));
        }
    }

    static class LeaderboardHolder extends BaseSimpleTextViewHolder<Leaderboard> {
        LeaderboardHolder(@NonNull View itemView, @NonNull OnListItemClickListener<Leaderboard> onItemClickListener) {
            super(itemView, onItemClickListener);
        }

        @Override
        protected int getTextViewResourceId() {
            return android.R.id.text1;
        }

        @Override
        protected String getStringFromItem(Leaderboard item) {
            return item.getName();
        }
    }
}
