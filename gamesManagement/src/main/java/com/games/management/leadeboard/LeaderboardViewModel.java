package com.games.management.leadeboard;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.games.management.GamesServiceHelper;
import com.games.management.NetworkState;
import com.google.api.services.games.model.LeaderboardEntry;

class LeaderboardViewModel extends ViewModel {
    private static final int MAX_LEADERBOARD_SCORES = 30;

    private LiveData<PagedList<LeaderboardEntry>> mLeaderboardEntries;
    private LiveData<NetworkState> mNetworkStateInitialLoad;
    private LiveData<NetworkState> mNetworkState;

    LeaderboardViewModel(GamesServiceHelper gameServiceHelper, String leaderboardId) {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(MAX_LEADERBOARD_SCORES)
                .setInitialLoadSizeHint(MAX_LEADERBOARD_SCORES)
                .setEnablePlaceholders(true)
                .build();

        LeaderboardEntryDataSource.LeaderboardEntryDataSourceFactory factory = new LeaderboardEntryDataSource.LeaderboardEntryDataSourceFactory(gameServiceHelper, leaderboardId);

        mLeaderboardEntries = new LivePagedListBuilder<>(factory, config).build();
        mNetworkStateInitialLoad = Transformations.switchMap(factory.getLeaderboardEntryDataSource(), new Function<LeaderboardEntryDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(LeaderboardEntryDataSource input) {
                return input.getNetworkStateInitialLoad();
            }
        });

        mNetworkState = Transformations.switchMap(factory.getLeaderboardEntryDataSource(), new Function<LeaderboardEntryDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(LeaderboardEntryDataSource input) {
                return input.getNetworkState();
            }
        });
    }

    LiveData<NetworkState> getNetworkStateInitialLoad() {
        return mNetworkStateInitialLoad;
    }

    LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    LiveData<PagedList<LeaderboardEntry>> getLeaderboardEntries() {
        return mLeaderboardEntries;
    }
}
