package org.codelearn.twitter.models;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;


public interface CodelearnTwitterAPI {

  @POST("/login")
  void login(@Body User user, Callback<UserLoginResponse> loginCallback);

}
