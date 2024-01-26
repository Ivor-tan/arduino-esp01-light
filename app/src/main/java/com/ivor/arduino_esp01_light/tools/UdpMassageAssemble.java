package com.ivor.arduino_esp01_light.tools;

public class UdpMassageAssemble {

    public static final String LIGHT_COLOR_WHITE = "White";
    public static final String LIGHT_COLOR_Yellow = "Yellow";
    public static final String LIGHT_COLOR_RED = "Red";
    public static final String LIGHT_COLOR_GREEN = "Green";
    public static final String LIGHT_COLOR_BLUE = "Blue";

    public static final String LIGHT_STATE_ON = "ON";
    public static final String LIGHT_STATE_OFF = "OFF";

    public static final String LIGHT_DEFAULT_STRENGTH = "200";

    public static final String CLEAR_CONFIG = "ClearConfig";


    public static String getCommend(String color) {
        return getCommend(color, LIGHT_STATE_OFF, "200");
    }

    public static String getCommend(String color, String state) {
        return getCommend(color, state, "200");
    }

    /**
     * @param color    灯的颜色  White  Yellow
     * @param state    开/关  On/Off
     * @param strength 亮度 0~255
     **/
    public static String getCommend(String color, String state, String strength) {
        StringBuilder commend = new StringBuilder();
        commend.append("<");
        commend.append(color);
        commend.append("-");
        commend.append(state);
        commend.append("-");
        commend.append(strength);
        commend.append(">&"); //& 结尾   < - > 截取指令
        return commend.toString();
    }

    public static String getClearConfig() {
        return CLEAR_CONFIG;
    }
}
