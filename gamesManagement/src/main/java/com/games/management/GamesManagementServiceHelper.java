package com.games.management;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.games.GamesScopes;
import com.google.api.services.gamesManagement.GamesManagement;
import com.google.api.services.gamesManagement.model.HiddenPlayer;
import com.google.api.services.gamesManagement.model.HiddenPlayerList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GamesManagementServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final GamesManagement mGamesManagementService;

    public static GamesManagementServiceHelper buildServiceHelper(Context context, GoogleSignInAccount signInAccount) {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(GamesScopes.GAMES));
        credential.setSelectedAccount(signInAccount.getAccount());

        GamesManagement gamesManagementService = new GamesManagement.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .setApplicationName(context.getString(R.string.application_name))
                .build();

        return new GamesManagementServiceHelper(gamesManagementService);
    }

    private GamesManagementServiceHelper(GamesManagement gamesManagementService) {
        mGamesManagementService = gamesManagementService;
    }

    public Task<HiddenPlayerList> getHiddenPlayers(@NonNull final String applicationId, final String pageToken, final int maxResults) {
        return Tasks.call(mExecutor, new Callable<HiddenPlayerList>() {
            @Override
            public HiddenPlayerList call() throws Exception {
                GamesManagement.Applications.ListHidden listHidden = mGamesManagementService.applications().listHidden(applicationId);
                if (pageToken != null)
                    listHidden.setPageToken(pageToken);
                listHidden.setMaxResults(maxResults);

                return listHidden.execute();
            }
        });
    }

    public Task<Void> playerHide(final String applicationId, final String playerId) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                GamesManagement.Players.Hide hide = mGamesManagementService.players().hide(applicationId, playerId);
                hide.execute();

                return null;
            }
        });
    }

    public Task<Void> playerUnhide(final String applicationId, final String playerId) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                GamesManagement.Players.Unhide unhide = mGamesManagementService.players().unhide(applicationId, playerId);
                unhide.execute();

                return null;
            }
        });
    }
}
