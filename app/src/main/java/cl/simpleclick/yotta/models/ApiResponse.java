package cl.simpleclick.yotta.models;


import androidx.annotation.NonNull;

public class ApiResponse {
    public String status;
    public String message;

    @NonNull
    @Override
    public String toString() {
        return "status=" + status + ", message=" + message;
    }
}