package com.games.management.hiddenPlayers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.games.management.GamesManagementServiceHelper;
import com.games.management.NetworkState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.gamesManagement.model.HiddenPlayer;
import com.google.api.services.gamesManagement.model.HiddenPlayerList;

public class HiddenPlayersDataSource extends PageKeyedDataSource<String, HiddenPlayer> {
    private GamesManagementServiceHelper mServiceHelper;

    private String mApplicationId;
    private MutableLiveData<NetworkState> mNetworkStateInitialLoad;
    private MutableLiveData<NetworkState> mNetworkState;

    private HiddenPlayersDataSource(@NonNull GamesManagementServiceHelper serviceHelper, @NonNull String applicationId) {
        mServiceHelper = serviceHelper;

        mApplicationId = applicationId;

        mNetworkStateInitialLoad = new MutableLiveData<>();
        mNetworkState = new MutableLiveData<>();
    }

    LiveData<NetworkState> getNetworkStateInitialLoad() {
        return mNetworkStateInitialLoad;
    }

    LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, HiddenPlayer> callback) {
        mNetworkStateInitialLoad.postValue(NetworkState.LOADING);
        mNetworkState.postValue(NetworkState.LOADING);

        mServiceHelper.getHiddenPlayers(mApplicationId, null, params.requestedLoadSize)
                .addOnSuccessListener(new OnSuccessListener<HiddenPlayerList>() {
                    @Override
                    public void onSuccess(HiddenPlayerList listHidden) {
                        mNetworkStateInitialLoad.postValue(NetworkState.LOADED);
                        mNetworkState.postValue(NetworkState.LOADING);

                        callback.onResult(listHidden.getItems(), null, listHidden.getNextPageToken());
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
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, HiddenPlayer> callback) {
        mNetworkState.postValue(NetworkState.error(new RuntimeException("Paging back not supported ('loadBefore')")));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, HiddenPlayer> callback) {
        load(params.key, params.requestedLoadSize, callback);
    }

    private void load(String pageToken, int requestedLoadSize, @NonNull final LoadCallback<String, HiddenPlayer> callback) {
        mNetworkState.postValue(NetworkState.LOADING);

        mServiceHelper.getHiddenPlayers(mApplicationId, pageToken, requestedLoadSize)
                .addOnSuccessListener(new OnSuccessListener<HiddenPlayerList>() {
                    @Override
                    public void onSuccess(HiddenPlayerList hiddenList) {
                        mNetworkState.postValue(NetworkState.LOADED);

                        callback.onResult(hiddenList.getItems(), hiddenList.getNextPageToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mNetworkState.postValue(NetworkState.error(e));
                    }
                });
    }

    public static class HiddenPlayersDataSourceFactory extends PageKeyedDataSource.Factory<String, HiddenPlayer> {
        private GamesManagementServiceHelper mServiceHelper;
        private MutableLiveData<HiddenPlayersDataSource> mDataSource;

        private String mApplicationId;

        HiddenPlayersDataSourceFactory(@NonNull GamesManagementServiceHelper serviceHelper, @NonNull String applicationId) {
            mServiceHelper = serviceHelper;

            mApplicationId = applicationId;

            mDataSource = new MutableLiveData<>();
        }

        LiveData<HiddenPlayersDataSource> getDataSource() {
            return mDataSource;
        }

        @NonNull
        @Override
        public DataSource<String, HiddenPlayer> create() {
            HiddenPlayersDataSource dataSource = new HiddenPlayersDataSource(mServiceHelper, mApplicationId);
            mDataSource.postValue(dataSource);

            return dataSource;
        }
    }
}