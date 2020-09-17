package com.scrappers.notepadsnippet.FingerPrint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.TextView;
import android.widget.Toast;

import com.scrappers.notepadsnippet.MainScreens.MainActivity;
import com.scrappers.notepadsnippet.R;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static androidx.core.content.ContextCompat.startActivity;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.FINGERPRINT_TRANSACTION;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.Theme;

public class FingerPrint extends AppCompatActivity {


    private static final String KEY_NAME = "01#ROOT";
    private Cipher cipher;
    private KeyStore keyStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ReadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);


        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            //Get an instance of KeyguardManager and FingerprintManager//
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            TextView textView = findViewById(R.id.textview);

            //Check whether the device has a fingerprint sensor//
            if ( !fingerprintManager.isHardwareDetected() ){
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                textView.setText("Your device doesn't support fingerprint authentication");
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if ( ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED ){
                // If your app doesn't have this permission, then display the following text//
                textView.setText("Please enable the fingerprint permission");
            }

            //Check that the user has registered at least one fingerprint//
            if ( !fingerprintManager.hasEnrolledFingerprints() ){
                // If the user hasn’t configured any fingerprints, then display the following message//
                textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
            }

            //Check that the lockscreen is secured//
            if ( !keyguardManager.isKeyguardSecure() ){
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                textView.setText("Please enable lockScreen security in your device's Settings");
            } else {
                try {
                    generateKey();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if ( initCipher() ){
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);

                }
            }
        }
    }

    public void ReadTheme(){
        //applying themes according to the content
        if(Theme.contains("GreenTheme")){
            setTheme(R.style.GreenTheme);
        }else if(Theme.contains("AppTheme")){
            setTheme(R.style.AppTheme);
        }else if(Theme.contains("GrayScaleTheme")){
            setTheme(R.style.Darky);
        }else if(Theme.contains("TitanTheme")){
            setTheme(R.style.orangeLover);
        }else if(Theme.contains("CyanTheme")){
            setTheme(R.style.BlueDark);
        }
    }
    private boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (Exception e) {

            return  false;
            //Return false if cipher initialization failed//

        }


    }

//Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//

    private void generateKey() {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                keyGenerator.init(new

                        //Specify the operation(s) this key can be used for//
                        KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                        //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }

            //Generate the key//
            keyGenerator.generateKey();

        } catch (Exception exc) {
            exc.printStackTrace();

        }


    }


}



@RequiresApi(api = Build.VERSION_CODES.M)
 class FingerprintHandler extends FingerprintManager.AuthenticationCallback{

    private Context context;
    private CancellationSignal cancellationSignal;

    public FingerprintHandler(Context mContext) {
        context = mContext;
    }
    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        startActivity(context,new Intent(context, MainActivity.class),null);
        Toast.makeText(context,"Authentication Success",Toast.LENGTH_LONG).show();

        FINGERPRINT_TRANSACTION=true;

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context,"Authentication Failed",Toast.LENGTH_LONG).show();


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }}


