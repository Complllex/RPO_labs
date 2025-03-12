package ru.bmstu.complllex.fclient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import java.util.Arrays;

import ru.bmstu.complllex.fclient.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncher;
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] randomBytes(int no);
    public static native byte[] decrypt(byte[] key, byte[] data);

    public static native int initRng();
    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int res = initRng();
        byte[] v = randomBytes(16);
        Log.println(Log.INFO, "Random numbers", Arrays.toString(v));
        byte[] a = encrypt(v, v);
        Log.println(Log.INFO, "Encrypt", Arrays.toString(a));
        byte[] b = decrypt(v, a);
        Log.println(Log.INFO, "Decrypt", Arrays.toString(b));
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Обработка результата
                        String pin = result.getData().getStringExtra("pin");
                        Toast.makeText(MainActivity.this, "Введен PIN: " + pin, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }
    public void onButtonClick(View v)
    {
        Intent it = new Intent(this, PinpadActivity.class);
        //startActivity(it);
        activityResultLauncher.launch(it);
    }
    public native String stringFromJNI();
}