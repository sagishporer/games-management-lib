package com.games.management.hiddenPlayers;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.games.management.GamesManagementServiceHelper;
import com.games.management.NetworkState;
import com.google.api.services.gamesManagement.model.HiddenPlayer;

public class HiddenPlayersViewModel extends ViewModel {
    private static final int MAX_HIDDEN_PLAYERS = 50;

    private LiveData<PagedList<HiddenPlayer>> mHiddenPlayers;
    private LiveData<NetworkState> mNetworkStateInitialLoad;
    private LiveData<NetworkState> mNetworkState;

    HiddenPlayersViewModel(GamesManagementServiceHelper serviceHelper, String applicationId) {
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(MAX_HIDDEN_PLAYERS)
                .setInitialLoadSizeHint(MAX_HIDDEN_PLAYERS)
                .setEnablePlaceholders(false)
                .build();

        HiddenPlayersDataSource.HiddenPlayersDataSourceFactory factory
                = new HiddenPlayersDataSource.HiddenPlayersDataSourceFactory(serviceHelper, applicationId);

        mHiddenPlayers = new LivePagedListBuilder<>(factory, config).build();
        mNetworkStateInitialLoad = Transformations.switchMap(factory.getDataSource(), new Function<HiddenPlayersDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(HiddenPlayersDataSource input) {
                return input.getNetworkStateInitialLoad();
            }
        });

        mNetworkState = Transformations.switchMap(factory.getDataSource(), new Function<HiddenPlayersDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(HiddenPlayersDataSource input) {
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

    LiveData<PagedList<HiddenPlayer>> getHiddenPlayers() {
        return mHiddenPlayers;
    }
}
