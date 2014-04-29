package org.codelearn.twitter.models;

import java.io.Serializable;

/**
 * A Plain Old Java Object that represents a single Tweet.
 */
public class Tweet implements Serializable {

  private static final long serialVersionUID = -2555720755871566222L;
  private String id;
  private String title;
  private String body;

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
