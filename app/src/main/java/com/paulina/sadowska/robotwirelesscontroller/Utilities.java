package com.paulina.sadowska.robotwirelesscontroller;

/**
 * Created by palka on 26.11.15.
 */
public class Utilities {

    static private boolean connectionState;
    public static boolean isDeviceConnected() { return connectionState; }
    public static void setConnectionState(boolean connected) { connectionState = connected; }

    static private int alphaOutDeg = 0; //degree to rotate robot
    static private boolean robotIsMoving = false; //determines if the robot should move
    static private double velocityMultiplier = 0.0; //not used yet, depends on analog position
    static private String velocity = "050";
    static private int[] currentmV = {0, 0};

    public static int  getAlpha() {  return alphaOutDeg; }
    public static void setAlpha(int alpha) {    alphaOutDeg = alpha;   }

    public static double getVelocityMultiplier() {  return velocityMultiplier; }
    public static void   setVelocityMultiplier(double mult) {    velocityMultiplier = mult;   }

    public static String getVelocity() {  return velocity; }
    public static void   setVelocity(String vel) {    velocity = vel;   }

    public static double getCurrentmV(int nr) {  return currentmV[nr-1]; }
    public static void   setCurrentmV(int curr, int nr) {    currentmV[nr-1] = curr;}

    public static void setRobotIsMovingFlag()    { robotIsMoving = true; }
    public static void resetRobotIsMovingFlag()  { robotIsMoving = false;}
    public static boolean getRobotIsMovingFlag() { return robotIsMoving; }

}
