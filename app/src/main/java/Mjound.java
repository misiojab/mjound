import android.app.Application;
import android.content.Intent;

import com.misiojab.mj.mjound.BackgroundService;

public class Mjound extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, BackgroundService.class));
    }
}
