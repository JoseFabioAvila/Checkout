package checkout.qupos.ncq.com.checkoutsncq.Utilidades;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import checkout.qupos.ncq.com.checkoutsncq.ActivityCheckout.CheckoutsActivity;
import checkout.qupos.ncq.com.checkoutsncq.R;

public class SplashScreen extends Activity
{
    private static int splashInterval = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(SplashScreen.this, CheckoutsActivity.class);

                startActivity(i);

                this.finish();
            }

            private void finish()
            {

            }
        }, splashInterval);
    }
}