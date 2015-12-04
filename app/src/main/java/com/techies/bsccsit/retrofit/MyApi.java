package com.techies.bsccsit.retrofit;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface MyApi {

    @FormUrlEncoded
    @POST("/bsccsit/get_json.php")
    void uploadUserData(@Field("fbid") String fbId, @Field("name") String name, @Field("semester") String semester, @Field("phone_number") String phone_number,
                                   @Field("college") String college, @Field("email") String email,Callback<String> calback);
}
