package com.games.management;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.games.Games;
import com.google.api.services.games.GamesScopes;
import com.google.api.services.games.model.LeaderboardListResponse;
import com.google.api.services.games.model.LeaderboardScores;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GamesServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Games mGamesService;

    // See documentation here: https://developers.google.com/games/services/web/api/scores/list
    private static final String TIME_SPAN_ALL_TIME = "ALL_TIME";
    private static final String TIME_SPAN_DAILY = "WEEKLY";
    private static final String TIME_SPAN_WEEKLY = "DAILY";

    private static final String COLLECTION_PUBLIC = "PUBLIC";
    private static final String COLLECTION_SOCIAL = "SOCIAL";
    private static final String COLLECTION_SOCIAL_IP = "SOCIAL_IP";

    public static GamesServiceHelper buildServiceHelper(Context context, GoogleSignInAccount signInAccount) {
        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        context, Collections.singleton(GamesScopes.GAMES));
        credential.setSelectedAccount(signInAccount.getAccount());

        Games gamesService = new Games.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                        .setApplicationName(context.getString(R.string.application_name))
                        .build();

        return new GamesServiceHelper(gamesService);
    }

    private GamesServiceHelper(Games gamesService) {
        mGamesService = gamesService;
    }

    public Task<LeaderboardListResponse> getLeaderboards(final int maxResults, final String pageToken) {
        return Tasks.call(mExecutor, new Callable<LeaderboardListResponse>() {
            @Override
            public LeaderboardListResponse call() throws Exception {
                Games.Leaderboards.List leaderboardList = mGamesService.leaderboards().list();
                leaderboardList.setMaxResults(maxResults);
                if (pageToken != null)
                    leaderboardList.setPageToken(pageToken);

                return leaderboardList.execute();
            }
        });
    }

    public Task<LeaderboardScores> getLeaderboardScores(final String leaderboardId, final int maxResults, final String pageToken) {
        return Tasks.call(mExecutor, new Callable<LeaderboardScores>() {
            @Override
            public LeaderboardScores call() throws Exception {
                Games.Scores.List leaderboardScores = mGamesService.scores().list(leaderboardId, COLLECTION_PUBLIC, TIME_SPAN_ALL_TIME);
                leaderboardScores.setMaxResults(maxResults);
                if (pageToken != null)
                    leaderboardScores.setPageToken(pageToken);

                return leaderboardScores.execute();
            }
        });
    }

}
