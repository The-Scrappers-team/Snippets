package com.scrappers.notepadsnippet.FingerPrint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.Toast;

import com.scrappers.notepadsnippet.MainScreens.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static androidx.core.content.ContextCompat.startActivity;
import static com.scrappers.notepadsnippet.MainScreens.MainActivity.FINGERPRINT_TRANSACTION;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback{

    private final AppCompatActivity context;

    FingerPrintHandler(AppCompatActivity context) {
            this.context = context;
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

    void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }
}


