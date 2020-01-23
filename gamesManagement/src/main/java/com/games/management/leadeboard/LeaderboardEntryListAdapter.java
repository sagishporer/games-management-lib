package com.games.management.leadeboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.games.management.base.BaseSimpleTextViewHolder;
import com.games.management.OnListItemClickListener;
import com.google.api.services.games.model.LeaderboardEntry;

import java.util.Locale;

class LeaderboardEntryListAdapter extends PagedListAdapter<LeaderboardEntry, LeaderboardEntryListAdapter.LeaderboardEntryHolder> {
    private OnListItemClickListener<LeaderboardEntry> mOnItemClickListener;

    LeaderboardEntryListAdapter(@NonNull DiffUtil.ItemCallback<LeaderboardEntry> diffCallback, @NonNull OnListItemClickListener<LeaderboardEntry> onItemClickListener) {
        super(diffCallback);

        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LeaderboardEntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new LeaderboardEntryHolder(viewItem, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardEntryHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class LeaderboardEntryDiffUtil extends DiffUtil.ItemCallback<LeaderboardEntry> {
        @Override
        public boolean areItemsTheSame(@NonNull LeaderboardEntry oldItem, @NonNull LeaderboardEntry newItem) {
            return oldItem.getPlayer().getPlayerId().equals(newItem.getPlayer().getPlayerId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull LeaderboardEntry oldItem, @NonNull LeaderboardEntry newItem) {
            return (oldItem.equals(newItem));
        }
    }

    static class LeaderboardEntryHolder extends BaseSimpleTextViewHolder<LeaderboardEntry> {
        LeaderboardEntryHolder(@NonNull View itemView, @NonNull OnListItemClickListener<LeaderboardEntry> onItemClickListener) {
            super(itemView, onItemClickListener);
        }

        @Override
        protected int getTextViewResourceId() {
            return android.R.id.text1;
        }

        @Override
        protected String getStringFromItem(LeaderboardEntry item) {
            return getLeaderboardEntryString(item);
        }
    }

    static String getLeaderboardEntryString(LeaderboardEntry entry) {
        return String.format(Locale.US, "User: %s, Score: %d", entry.getPlayer().getDisplayName(), entry.getScoreValue());
    }
}
