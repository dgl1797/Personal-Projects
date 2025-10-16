package com.personal.njtodo.utilities;

public class ResponseCompatibleException extends Exception {
  private int status;

  public ResponseCompatibleException(int status, String message, Throwable error) {
    super(message, error);
    this.status = status;
  }

  public ResponseCompatibleException(int status, String message) {
    super(message);
    this.status = status;
  }

  public void logAndThrow() throws ResponseCompatibleException {
    System.out.println("[SERVER:ERROR]");
    System.out.println(String.format("%d:%s", this.status, this.getMessage().toUpperCase()));
    this.printStackTrace();
    throw this;
  }

  public int getStatus() {
    return this.status;
  }
}
