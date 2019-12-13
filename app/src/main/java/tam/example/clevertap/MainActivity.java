package tam.example.clevertap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.example.clevertap_tam.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button pushUserProfileButton, pushProductViewedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        String CHANNEL_ID = "1";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        pushUserProfileButton = findViewById(R.id.pushUserProfileButton);
        pushProductViewedEvent = findViewById(R.id.pushProductViewedEvent);

        pushUserProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();

                //Update pre-defined profile properties
                profileUpdate.put("Name", "Joshua Clever Tap 1");
                profileUpdate.put("Email", "joshua_clevertap1@gmail.com");
                //Update custom profile properties
                profileUpdate.put("Plan Type", "Silver");
                profileUpdate.put("Favorite Food", "Pizza");

                clevertapDefaultInstance.pushProfile(profileUpdate);

                Toast.makeText(getApplicationContext(), "User Profile Pushed!", Toast.LENGTH_LONG).show();
            }
        });

        pushProductViewedEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // event with properties
                HashMap<String, Object> prodViewedAction = new HashMap<String, Object>();
                prodViewedAction.put("Product ID", 1);
                prodViewedAction.put("Product Image", "https://d35fo82fjcw0y8.cloudfront.net/2018/07/26020307/customer-success-clevertap.jpg");
                prodViewedAction.put("Product Name", "CleverTap");

                clevertapDefaultInstance.pushEvent("Product viewed", prodViewedAction);

                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();

                profileUpdate.put("Email", "dk+joshuaprayogi1@clevertap.com");

                clevertapDefaultInstance.pushProfile(profileUpdate);

                Toast.makeText(getApplicationContext(), "Product Viewed Event & Add Email Pushed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
