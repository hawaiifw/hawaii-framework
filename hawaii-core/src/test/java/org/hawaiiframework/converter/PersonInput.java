package org.hawaiiframework.converter;

/** Sample bean to convert. */
public class PersonInput {

  private String name;

  @SuppressWarnings("PMD.CommentRequired")
  public String getName() {
    return name;
  }

  @SuppressWarnings("PMD.CommentRequired")
  public void setName(String name) {
    this.name = name;
  }
}
