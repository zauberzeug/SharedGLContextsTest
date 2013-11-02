package de.perpetualmobile.sharedglcontexts;

import java.util.ArrayList;

import android.util.Log;

public class PerformanceTimer {

    private long lastTimestamp = System.nanoTime();
    private long lastMeasurement = -1;
    private String lastDescription = null;
    private ArrayList<String> mutedDescriptions = new ArrayList<String>();

    public void start(String description) {
        if (mutedDescriptions.contains(description)) {
            lastDescription = null;
            return;
        }

        if (lastDescription != description)
            lastMeasurement = -1;

        lastDescription = description;
        lastTimestamp = System.nanoTime();
    }

    public void stop() {
        if (lastDescription == null)
            return;

        long measurement = (System.nanoTime() - lastTimestamp) / 1000000;
        if (lastMeasurement == measurement)
            return;

        Log.v("PerformanceTimer", "'" + lastDescription + "' took " + measurement
                + " milliseconds on thread " + Thread.currentThread().getId());
        lastMeasurement = measurement;
        lastTimestamp = System.nanoTime();
    }

    public void mute(String description) {
        mutedDescriptions.add(description);
    }
}
