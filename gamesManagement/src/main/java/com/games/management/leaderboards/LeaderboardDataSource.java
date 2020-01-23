package com.games.management.leaderboards;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.games.management.GamesServiceHelper;
import com.games.management.NetworkState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.games.model.Leaderboard;
import com.google.api.services.games.model.LeaderboardListResponse;

public class LeaderboardDataSource extends PageKeyedDataSource<String, Leaderboard> {
    private GamesServiceHelper mGameServiceHelper;

    private MutableLiveData<NetworkState> mNetworkStateInitialLoad;
    private MutableLiveData<NetworkState> mNetworkState;

    private LeaderboardDataSource(@NonNull GamesServiceHelper gameServiceHelper) {
        mGameServiceHelper = gameServiceHelper;

        mNetworkStateInitialLoad = new MutableLiveData<>();
        mNetworkState = new MutableLiveData<>();
    }

    LiveData<NetworkState> getNetworkStateInitialLoad() { return mNetworkStateInitialLoad; }

    LiveData<NetworkState> getNetworkState() { return mNetworkState; }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, Leaderboard> callback) {
        mNetworkStateInitialLoad.postValue(NetworkState.LOADING);
        mNetworkState.postValue(NetworkState.LOADING);

        mGameServiceHelper.getLeaderboards(params.requestedLoadSize, null)
                .addOnSuccessListener(new OnSuccessListener<LeaderboardListResponse>() {
                    @Override
                    public void onSuccess(LeaderboardListResponse leaderboardListResponse) {
                        mNetworkStateInitialLoad.postValue(NetworkState.LOADED);
                        mNetworkState.postValue(NetworkState.LOADING);

                        callback.onResult(leaderboardListResponse.getItems(), null, leaderboardListResponse.getNextPageToken());
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
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Leaderboard> callback) {
        mNetworkState.postValue(NetworkState.error(new RuntimeException("Paging back not supported ('loadBefore')")));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, Leaderboard> callback) {
        load(params.key, params.requestedLoadSize, callback);
    }

    private void load(String pageToken, int requestedLoadSize, @NonNull final LoadCallback<String, Leaderboard> callback) {
        mNetworkState.postValue(NetworkState.LOADING);

        mGameServiceHelper.getLeaderboards(requestedLoadSize, pageToken)
                .addOnSuccessListener(new OnSuccessListener<LeaderboardListResponse>() {
                    @Override
                    public void onSuccess(LeaderboardListResponse leaderboardListResponse) {
                        mNetworkState.postValue(NetworkState.LOADED);

                        callback.onResult(leaderboardListResponse.getItems(), leaderboardListResponse.getNextPageToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mNetworkState.postValue(NetworkState.error(e));
                    }
                });
    }

    public static class LeaderboardsDataSourceFactory extends PageKeyedDataSource.Factory<String, Leaderboard> {
        private GamesServiceHelper mGameServiceHelper;
        private MutableLiveData<LeaderboardDataSource> mDataSource;

        LeaderboardsDataSourceFactory(@NonNull GamesServiceHelper gameServiceHelper) {
            mGameServiceHelper = gameServiceHelper;

            mDataSource = new MutableLiveData<>();
        }

        LiveData<LeaderboardDataSource> getDataSource() {
            return mDataSource;
        }

        @NonNull
        @Override
        public DataSource<String, Leaderboard> create() {
            LeaderboardDataSource dataSource = new LeaderboardDataSource(mGameServiceHelper);
            mDataSource.postValue(dataSource);

            return dataSource;
        }
    }
}
