package com.games.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.games.management.base.BaseAuthActivity;
import com.games.management.leaderboards.LeaderboardSelectionActivity;
import com.games.management.hiddenPlayers.PlayersManagementActivity;

public class MenuActivity extends BaseAuthActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_layout);

        findViewById(R.id.buttonLeaderboards).setOnClickListener(this);
        findViewById(R.id.buttonHiddenPlayers).setOnClickListener(this);
        findViewById(R.id.buttonSignOut).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSignOut)
            signOut();
        else if (view.getId() == R.id.buttonLeaderboards) {
            Intent i = new Intent(this, LeaderboardSelectionActivity.class);
            startActivity(i);
        }
        else if (view.getId() == R.id.buttonHiddenPlayers) {
            Intent i = new Intent(this, PlayersManagementActivity.class);
            startActivity(i);
        }
    }
}
