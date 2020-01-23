package com.games.management.leadeboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.games.management.GamesServiceHelper;
import com.games.management.NetworkState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.games.model.LeaderboardEntry;
import com.google.api.services.games.model.LeaderboardScores;

public class LeaderboardEntryDataSource extends PageKeyedDataSource<String, LeaderboardEntry> {
    private GamesServiceHelper mGameServiceHelper;
    private String mLeaderboardId;
    private MutableLiveData<NetworkState> mNetworkStateInitialLoad;
    private MutableLiveData<NetworkState> mNetworkState;


    private LeaderboardEntryDataSource(@NonNull GamesServiceHelper gameServiceHelper, String leaderboardId) {
        mGameServiceHelper = gameServiceHelper;
        mLeaderboardId = leaderboardId;
        mNetworkStateInitialLoad = new MutableLiveData<>();
        mNetworkState = new MutableLiveData<>();
    }

    LiveData<NetworkState> getNetworkStateInitialLoad() { return mNetworkStateInitialLoad; }

    LiveData<NetworkState> getNetworkState() { return mNetworkState; }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, LeaderboardEntry> callback) {
        mNetworkStateInitialLoad.postValue(NetworkState.LOADING);
        mNetworkState.postValue(NetworkState.LOADING);

        mGameServiceHelper.getLeaderboardScores(mLeaderboardId, params.requestedLoadSize, null)
                .addOnSuccessListener(new OnSuccessListener<LeaderboardScores>() {
                    @Override
                    public void onSuccess(LeaderboardScores leaderboardScores) {
                        mNetworkStateInitialLoad.postValue(NetworkState.LOADED);
                        mNetworkState.postValue(NetworkState.LOADING);

                        callback.onResult(leaderboardScores.getItems(), 0, leaderboardScores.getNumScores().intValue(), leaderboardScores.getPrevPageToken(), leaderboardScores.getNextPageToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mNetworkStateInitialLoad.postValue(NetworkState.error(e));
                        mNetworkState.postValue(NetworkState.error(e));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, LeaderboardEntry> callback) {
        load(params.key, params.requestedLoadSize, callback);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, LeaderboardEntry> callback) {
        load(params.key, params.requestedLoadSize, callback);
    }

    private void load(String pageToken, int requestedLoadSize, @NonNull final LoadCallback<String, LeaderboardEntry> callback) {
        mNetworkState.postValue(NetworkState.LOADING);

        mGameServiceHelper.getLeaderboardScores(mLeaderboardId, requestedLoadSize, pageToken)
                .addOnSuccessListener(new OnSuccessListener<LeaderboardScores>() {
                    @Override
                    public void onSuccess(LeaderboardScores leaderboardScores) {
                        mNetworkState.postValue(NetworkState.LOADED);

                        callback.onResult(leaderboardScores.getItems(), leaderboardScores.getNextPageToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mNetworkState.postValue(NetworkState.error(e));
                    }
                });
    }

    public static class LeaderboardEntryDataSourceFactory extends PageKeyedDataSource.Factory<String, LeaderboardEntry> {
        private GamesServiceHelper mGameServiceHelper;
        private String mLeaderboardId;
        private MutableLiveData<LeaderboardEntryDataSource> mLeaderboardEntryDataSource;

        LeaderboardEntryDataSourceFactory(@NonNull GamesServiceHelper gameServiceHelper, String leaderboardId) {
            mGameServiceHelper = gameServiceHelper;
            mLeaderboardId = leaderboardId;
            mLeaderboardEntryDataSource = new MutableLiveData<>();
        }

        LiveData<LeaderboardEntryDataSource> getLeaderboardEntryDataSource() {
            return mLeaderboardEntryDataSource;
        }

        @NonNull
        @Override
        public DataSource<String, LeaderboardEntry> create() {
            LeaderboardEntryDataSource dataSource = new LeaderboardEntryDataSource(mGameServiceHelper, mLeaderboardId);
            mLeaderboardEntryDataSource.postValue(dataSource);

            return dataSource;
        }
    }
}
