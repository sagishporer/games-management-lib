package com.games.management.hiddenPlayers;

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

import com.games.management.base.BaseAuthActivity;
import com.games.management.GamesManagementServiceHelper;
import com.games.management.NetworkState;
import com.games.management.OnListItemClickListener;
import com.games.management.PlayGamesHelper;
import com.games.management.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.gamesManagement.model.HiddenPlayer;

public class PlayersManagementActivity extends BaseAuthActivity implements OnListItemClickListener<HiddenPlayer> {
    private static final String TAG = PlayersManagementActivity.class.getSimpleName();

    private HiddenPlayerListAdapter mHiddenPlayersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.players_management_layout);

        mHiddenPlayersAdapter = new HiddenPlayerListAdapter(new HiddenPlayerListAdapter.HiddenPlayerDiffUtil(), this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPlayers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mHiddenPlayersAdapter);

    }

    @Override
    protected void onConnectSuccess() {
        super.onConnectSuccess();

        final AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setTitle("Loading players")
                .setMessage(R.string.please_wait)
                .create();

        GamesManagementServiceHelper serviceHelper = GamesManagementServiceHelper.buildServiceHelper(getApplicationContext(), getGoogleSignInAccount());

        HiddenPlayersViewModel viewModel = new HiddenPlayersViewModel(serviceHelper, PlayGamesHelper.getApplicationId(getApplicationContext()));
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
                    reportError("Failed to load players", networkState.getException());
            }
        });

        viewModel.getHiddenPlayers().observe(this, new Observer<PagedList<HiddenPlayer>>() {
            @Override
            public void onChanged(PagedList<HiddenPlayer> hiddenPlayers) {
                mHiddenPlayersAdapter.submitList(hiddenPlayers);
            }
        });
    }

    private void unhidePlayer(String playerId) {
        if (playerId == null)
            throw new NullPointerException("playerId");

        Log.d(TAG, "Unhiding player: " + playerId);

        GamesManagementServiceHelper gamesManagementServiceHelper =
                GamesManagementServiceHelper.buildServiceHelper(this, getGoogleSignInAccount());

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Unhiding player", "Please wait");

        gamesManagementServiceHelper.playerUnhide(PlayGamesHelper.getApplicationId(this), playerId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        progressDialog.dismiss();

                        Log.d(TAG, "Unhide player: success");
                        Toast.makeText(getApplicationContext(), "Unhide player: success", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        reportError("Failed to Unhide user", e);
                    }
                });
    }

    private void processUserSelectEntry(final HiddenPlayer hiddenPlayer) {
        new AlertDialog.Builder(this)
                .setTitle("Unhide User - Are you sure?")
                .setMessage(hiddenPlayer.getPlayer().getDisplayName())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unhidePlayer(hiddenPlayer.getPlayer().getPlayerId());

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

    @Override
    public void onItemClick(HiddenPlayer item) {
        processUserSelectEntry(item);
    }
}
