package com.actia.utilities.utilities_media_player;
/**
 * Utils for Media Player
 */
public class UtilsMediaPlayer {
	
	/**
	 * Get secodns
	 * @param milliseconds milliseconds time
	 */
	public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        @SuppressWarnings("UnusedAssignment") String secondsString = "";
        String minutesString = "";
 
        // Convert total duration into time
           int hours = (int)( milliseconds / (1000*60*60));
           int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
           int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
           // Add hours if there
           if(hours > 0){
               finalTimerString = hours + ":";
           }

        if (minutes<10){
            minutesString = "0" +minutes;
        }else {
            minutesString= "" + minutes;
        }
           // Prepending 0 to seconds if it is one digit
           if(seconds < 10){
               secondsString = "0" + seconds;
           }else{
               secondsString = "" + seconds;}
 
           finalTimerString = finalTimerString + minutesString + ":" + secondsString;
 
        // return timer string
        return finalTimerString;
    }
 
    /**
     * Function to get Progress percentage
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage;
 
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
 
        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;
 
        // return percentage
        return percentage.intValue();
    }
 
    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
 
        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
