package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {


    public CalculateRootsService() {
        super("CalculateRootsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        long timeStartMs = System.currentTimeMillis();
        long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
        if (numberToCalculateRootsFor <= 0) {
            Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
            return;
        }

        long limit = (long) Math.sqrt(numberToCalculateRootsFor);
        long root1 = numberToCalculateRootsFor;

        for (long i = 2; i <= limit; i++) {
            long timeSpentMs = System.currentTimeMillis() - timeStartMs;
            if (timeSpentMs >= 20_000) {
                Intent broadcastIntent = new Intent("stopped_calculations");
                broadcastIntent.putExtra("original_number", numberToCalculateRootsFor);
                broadcastIntent.putExtra("time_until_give_up_seconds", timeSpentMs / 1000);
                sendBroadcast(broadcastIntent);
                return;
            }

            if (numberToCalculateRootsFor % i == 0) {
                root1 = i;
                break;
            }
        }

        long calcTime = (System.currentTimeMillis() - timeStartMs) / 1000;

        Intent broadcastIntent = new Intent("found_roots");
        broadcastIntent.putExtra("original_number", numberToCalculateRootsFor);
        broadcastIntent.putExtra("root1", root1);
        broadcastIntent.putExtra("root2", numberToCalculateRootsFor / root1);
        broadcastIntent.putExtra("calc_time", calcTime);
        sendBroadcast(broadcastIntent);
    }
}