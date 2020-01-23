package com.games.management.leaderboards;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.games.management.base.BaseAuthActivity;
import com.games.management.GamesServiceHelper;
import com.games.management.NetworkState;
import com.games.management.OnListItemClickListener;
import com.games.management.R;
import com.games.management.leadeboard.LeaderboardManagementActivity;
import com.google.api.services.games.model.Leaderboard;

public class LeaderboardSelectionActivity extends BaseAuthActivity implements OnListItemClickListener<Leaderboard> {
    private static final String TAG = LeaderboardSelectionActivity.class.getSimpleName();

    private LeaderboardListAdapter mLeaderboardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.leaderboards_selection_layout);

        mLeaderboardsAdapter = new LeaderboardListAdapter(new LeaderboardListAdapter.LeaderboardDiffUtil(), this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewLeaderboards);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mLeaderboardsAdapter);
    }

    @Override
    protected void onConnectSuccess() {
        super.onConnectSuccess();

        final AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setTitle("Loading leaderboards")
                .setMessage(R.string.please_wait)
                .create();

        GamesServiceHelper gamesServiceHelper = GamesServiceHelper.buildServiceHelper(getApplicationContext(), getGoogleSignInAccount());
        LeaderboardsViewModel viewModel = new LeaderboardsViewModel(gamesServiceHelper);
        viewModel.getNetworkStateInitialLoad().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(NetworkState networkState) {
                if (networkState.getStatus().equals(NetworkState.Status.RUNNING))
                    progressDialog.show();
                if (networkState.getStatus().equals(NetworkState.Status.SUCCESS))
                    progressDialog.dismiss();

                // Error reporting will be done for all network calls observer
                if (networkState.getStatus().equals(NetworkState.Status.FAILED))
                    progressDialog.dismiss();

            }
        });

        viewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(NetworkState networkState) {
                if (networkState.getStatus().equals(NetworkState.Status.FAILED))
                    reportError("Failed to load leaderboards", networkState.getException());
            }
        });

        viewModel.getLeaderboards().observe(this, new Observer<PagedList<Leaderboard>>() {
            @Override
            public void onChanged(PagedList<Leaderboard> leaderboards) {
                mLeaderboardsAdapter.submitList(leaderboards);
            }
        });
    }

    @Override
    public void onItemClick(Leaderboard item) {
        Log.d(TAG, "Leaderboard selected: " + item.getName());
        Intent i = new Intent(getApplicationContext(), LeaderboardManagementActivity.class);
        i.putExtra(LeaderboardManagementActivity.EXTRA_LEADERBOARD_ID, item.getId());

        startActivity(i);
    }
}
