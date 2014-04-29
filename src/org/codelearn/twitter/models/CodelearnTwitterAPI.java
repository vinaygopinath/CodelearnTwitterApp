package org.codelearn.twitter.models;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface CodelearnTwitterAPI {

  @POST("/login")
  void login(@Body User user, Callback<UserLoginResponse> loginCallback);

  @GET("/tweets")
  void getTweets(Callback<List<Tweet>> tweetListCallback);

  @POST("/tweets")
  void submitTweet(@Body Tweet tweet, Callback<Void> submitTweetCallback);

}
