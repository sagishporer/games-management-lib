package com.games.management.leadeboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.games.management.GamesManagementServiceHelper;
import com.games.management.GamesServiceHelper;
import com.games.management.base.BaseAuthActivity;
import com.games.management.NetworkState;
import com.games.management.OnListItemClickListener;
import com.games.management.PlayGamesHelper;
import com.games.management.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.games.model.LeaderboardEntry;

public class LeaderboardManagementActivity extends BaseAuthActivity implements OnListItemClickListener<LeaderboardEntry> {
    private static final String TAG = LeaderboardManagementActivity.class.getSimpleName();

    public static final String EXTRA_LEADERBOARD_ID = "EXTRA_LEADERBOARD_ID";

    private LeaderboardEntryListAdapter mLeaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.leaderboard_management_layout);

        mLeaderboardAdapter = new LeaderboardEntryListAdapter(new LeaderboardEntryListAdapter.LeaderboardEntryDiffUtil(), this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewLeaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mLeaderboardAdapter);
    }

    @Override
    protected void onConnectSuccess() {
        super.onConnectSuccess();

        String leaderboardId = this.getIntent().getStringExtra(EXTRA_LEADERBOARD_ID);
        if (leaderboardId == null)
            throw new NullPointerException(EXTRA_LEADERBOARD_ID);

        GamesServiceHelper gamesServiceHelper = GamesServiceHelper.buildServiceHelper(getApplicationContext(), getGoogleSignInAccount());

        final AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setTitle("Loading leaderboard")
                .setMessage(R.string.please_wait)
                .create();

        LeaderboardViewModel leaderboardViewModel = new LeaderboardViewModel(gamesServiceHelper, leaderboardId);
        leaderboardViewModel.getNetworkStateInitialLoad().observe(this, new Observer<NetworkState>() {
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

        leaderboardViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(NetworkState networkState) {
                if (networkState.getStatus().equals(NetworkState.Status.FAILED))
                    reportError("Failed to load scores", networkState.getException());
            }
        });

        leaderboardViewModel.getLeaderboardEntries().observe(this, new Observer<PagedList<LeaderboardEntry>>() {
            @Override
            public void onChanged(PagedList<LeaderboardEntry> leaderboardEntries) {
                mLeaderboardAdapter.submitList(leaderboardEntries);
            }
        });
    }

    private void hidePlayer(String playerId) {
        if (playerId == null)
            throw new NullPointerException("playerId");

        Log.d(TAG, "Hiding player: " + playerId);

        GamesManagementServiceHelper gamesManagementServiceHelper =
                GamesManagementServiceHelper.buildServiceHelper(this, getGoogleSignInAccount());

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Hiding player", "Please wait");

        gamesManagementServiceHelper.playerHide(PlayGamesHelper.getApplicationId(this), playerId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        progressDialog.dismiss();

                        Log.d(TAG, "Hide player: success");
                        Toast.makeText(getApplicationContext(), "Hide player: success", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        reportError("Failed to hide user", e);
                    }
                });
    }

    @Override
    public void onItemClick(LeaderboardEntry item) {
        processUserSelectEntry(item);
    }

    private void processUserSelectEntry(final LeaderboardEntry leaderboardEntry) {
        new AlertDialog.Builder(LeaderboardManagementActivity.this)
                .setTitle("Hide User - Are you sure?")
                .setMessage(LeaderboardEntryListAdapter.getLeaderboardEntryString(leaderboardEntry))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hidePlayer(leaderboardEntry.getPlayer().getPlayerId());

                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }
}
