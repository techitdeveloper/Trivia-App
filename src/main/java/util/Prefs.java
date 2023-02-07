package util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void saveHighestScore (int score)
    {
        int currentScore = score;
        int lastScore = preferences.getInt("Highest Score", 0);
        if (currentScore > lastScore)
        {
            preferences.edit().putInt("Highest Score", currentScore).apply();
        }

    }

    public int getHighestScore ()
    {
        return preferences.getInt("Highest Score", 0);
    }

    public void setState(int index)
    {
        preferences.edit().putInt("Index State", index).apply();
    }

    public int getState()
    {
        return preferences.getInt("Index State", 0);
    }
}
