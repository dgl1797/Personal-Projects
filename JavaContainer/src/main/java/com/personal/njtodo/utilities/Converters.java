package com.personal.njtodo.utilities;

import com.personal.njtodo.EJBs.entities.support_objects.StateEnum;

public class Converters {
  public static final String binaryToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString().toLowerCase();
  }

  public static String stateEnumToString(StateEnum state) {
    return state == StateEnum.todo ? "todo" : state == StateEnum.ongoing ? "ongoing" : "done";
  }

  public static StateEnum stringToStateEnum(String state) {

    return state == null ? null
        : state.equals("todo") ? StateEnum.todo : state.equals("ongoing") ? StateEnum.ongoing : StateEnum.done;
  }

  public static String booleanTypeToString(boolean isPremium) {
    return isPremium ? "premium" : "standard";
  }
}
