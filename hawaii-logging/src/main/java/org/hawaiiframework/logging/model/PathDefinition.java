package org.hawaiiframework.logging.model;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathDefinition {

  private String pattern;

  private String method;

  private final PathMatcher matcher;

  public PathDefinition() {
    this.matcher = new AntPathMatcher();
  }

  public PathDefinition(String pattern) {
    this(null, pattern);
  }

  public PathDefinition(String method, String pattern) {
    this();
    this.pattern = pattern;
    this.method = method;
  }

  public boolean matches(String method, String path) {
    boolean matches = true;
    if (this.method != null) {
      matches = this.method.equals(method);
    }

    return matches && matcher.match(this.pattern, path);
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }
}
