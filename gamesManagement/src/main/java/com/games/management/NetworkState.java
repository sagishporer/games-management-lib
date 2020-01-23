package com.games.management;

public class NetworkState {
    public enum Status { RUNNING, SUCCESS, FAILED };

    private Status mLastStatus;
    private Exception mLastException;

    public static final NetworkState LOADED = new NetworkState(Status.SUCCESS);
    public static final NetworkState LOADING = new NetworkState(Status.RUNNING);
    public static NetworkState error(Exception e) { return new NetworkState(Status.FAILED, e); }

    private NetworkState(Status status) {
        this(status, null);
    }

    private NetworkState(Status status, Exception e) {
        mLastStatus = status;
        mLastException = e;
    }

    public Status getStatus() {
        return mLastStatus;
    }

    public Exception getException() {
        return mLastException;
    }
}
