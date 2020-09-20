package com.scrappers.notepadsnippet.FingerPrint;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.TextView;

import com.scrappers.notepadsnippet.R;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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


        //Get an instance of KeyguardManager and FingerprintManager//
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        TextView textView = findViewById(R.id.textview);

        //Check whether the device has a fingerprint sensor//
        if ( !fingerprintManager.isHardwareDetected() ){
            // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
            textView.setText(R.string.deviceNotSupported);
        }
        //Check whether the user has granted your app the USE_FINGERPRINT permission//
        if ( ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED ){
            // If your app doesn't have this permission, then display the following text//
            textView.setText(R.string.enableWarning);
        }

        //Check that the user has registered at least one fingerprint//
        if ( !fingerprintManager.hasEnrolledFingerprints() ){
            // If the user has n’t configured any fingerprints, then display the following message//
            textView.setText(R.string.noF);
        }

        //Check that the lockScreen is secured//
        if ( !keyguardManager.isKeyguardSecure() ){
            // If the user has n’t secured their lockScreen with a PIN password or pattern, then display the following text//
            textView.setText(R.string.enableFingerPrintInSettings);
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
                FingerPrintHandler helper = new FingerPrintHandler(this);
                helper.startAuth(fingerprintManager, cryptoObject);

            }
        }
    }

    private void ReadTheme(){
        //applying themes according to the content
        if(Theme.contains("GreenTheme")){
            setTheme(R.style.GreenTheme);
        }else if(Theme.contains("AppTheme")){
            setTheme(R.style.AppTheme);
        }else if(Theme.contains("Darky")){
            setTheme(R.style.Darky);
        }else if(Theme.contains("orangeLover")){
            setTheme(R.style.orangeLover);
        }else if(Theme.contains("BlueDark")){
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

            //Generate the key//
            keyGenerator.generateKey();

        } catch (Exception exc) {
            exc.printStackTrace();

        }


    }
}





