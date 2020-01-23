package com.games.management.leaderboards;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.games.management.GamesServiceHelper;
import com.games.management.NetworkState;
import com.google.api.services.games.model.Leaderboard;

public class LeaderboardsViewModel extends ViewModel {
    private static final int MAX_LEADERBOARDS = 200;

    private LiveData<PagedList<Leaderboard>> mLeaderboards;
    private LiveData<NetworkState> mNetworkStateInitialLoad;
    private LiveData<NetworkState> mNetworkState;

    LeaderboardsViewModel(GamesServiceHelper gameServiceHelper) {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(MAX_LEADERBOARDS)
                .setInitialLoadSizeHint(MAX_LEADERBOARDS)
                .setEnablePlaceholders(false)
                .build();

        LeaderboardDataSource.LeaderboardsDataSourceFactory factory
                = new LeaderboardDataSource.LeaderboardsDataSourceFactory(gameServiceHelper);

        mLeaderboards = new LivePagedListBuilder<>(factory, config).build();
        mNetworkStateInitialLoad = Transformations.switchMap(factory.getDataSource(), new Function<LeaderboardDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(LeaderboardDataSource input) {
                return input.getNetworkStateInitialLoad();
            }
        });

        mNetworkState = Transformations.switchMap(factory.getDataSource(), new Function<LeaderboardDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(LeaderboardDataSource input) {
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

    LiveData<PagedList<Leaderboard>> getLeaderboards() {
        return mLeaderboards;
    }
}
