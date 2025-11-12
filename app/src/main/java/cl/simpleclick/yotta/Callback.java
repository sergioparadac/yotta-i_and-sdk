package cl.simpleclick.yotta;

public interface Callback<T> {
    void onSuccess(T response);
    void onError(Exception e);
}
