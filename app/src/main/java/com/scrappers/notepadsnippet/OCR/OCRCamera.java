package com.scrappers.notepadsnippet.OCR;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.scrappers.notepadsnippet.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import jp.wasabeef.richeditor.RichEditor;

import static android.os.Environment.getExternalStorageDirectory;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.ocrCameraFragmentLayout;

/**
 * OCR camera Text/BarCode Reader
 */
public class OCRCamera extends Fragment {

    private static final int CAMERA_CAPTURE_OCR = 1;
    private static final int DOC_OCR = 2;
    private static final int SCAN_BARCODE = 3 ;

    private RichEditor editTextPanel;
    private AppCompatActivity context;

    /**
     * Create an OCR camera Fragment to choose between Camera Quick Shoot & PhotoLibrary
     * @param editTextPanel the editText where the extracted text goes
     */
    public OCRCamera(RichEditor editTextPanel, AppCompatActivity context){
        this.editTextPanel=editTextPanel;
        this.context=context;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.ocrcamera_fragment,container,false);

        /*
         *Define OCR Guide
         */
        LinearLayout ocrGuide=view.findViewById(R.id.ocrGuide);
        if(readOCRGuideState(context.getFilesDir()+"/SPRecordings/config/ocrGuideState.state")){
            ocrGuide.setVisibility(View.INVISIBLE);
        }else{
            ocrGuide.setVisibility(View.VISIBLE);
        }        /*
         * Close OCR Guide
         */
        ImageView closeOCrGuide=view.findViewById(R.id.closeOCRGuide);
        closeOCrGuide.setOnClickListener(v -> ocrGuide.setVisibility(View.INVISIBLE));
        /*
         * Define Never Show again checkBox
         */
        CheckBox dismissGuideForever=view.findViewById(R.id.dismissGuideForever);
        dismissGuideForever.setChecked(readOCRGuideState(context.getFilesDir()+"/SPRecordings/config/ocrGuideState.state"));

        dismissGuideForever.setOnClickListener(v->{
            if(((CheckBox)v).isChecked()){
                saveOCRGuideState(context.getFilesDir()+"/SPRecordings/config/ocrGuideState.state",true);
            }else if(!((CheckBox)v).isChecked()){
                saveOCRGuideState(context.getFilesDir()+"/SPRecordings/config/ocrGuideState.state",false);
            }
        });
        /*
         * Camera Intent
         */
        ImageView cameraIntentBtn=view.findViewById(R.id.cameraIntent);
        cameraIntentBtn.setOnClickListener(v -> {
            Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(cameraIntent,"Take your shot !"), CAMERA_CAPTURE_OCR);
        });

        /*
         *ACTION_OPEN_DOCUMENT Intent
         */
        ImageView openDocIntentBtn=view.findViewById(R.id.openDocIntent);
        openDocIntentBtn.setOnClickListener(v-> {
            Intent openDocIntent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            openDocIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(openDocIntent,"Choose your shot from the Photo Library !"),DOC_OCR);
        });

        /*
         * Action scan qr/bar code
         */
        ImageView scanCodeBtn=view.findViewById(R.id.barcodeReader);
        scanCodeBtn.setOnClickListener(v->{
            Intent scanCodeIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(scanCodeIntent,"Capture an image to scan !"),SCAN_BARCODE);
        });
        /*
         *Close Fragment & exit procedure
         */
        ImageView closeOcrFrag=view.findViewById(R.id.closeOcrFrag);
        closeOcrFrag.setOnClickListener(v -> ocrCameraFragmentLayout.setVisibility(View.INVISIBLE));

        return view;
    }

    private void saveOCRGuideState(String file,boolean data){
        try(BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(new File(file)))){
            bufferedWriter.write(String.valueOf(data));
        }catch (IOException | NullPointerException ex){
            System.err.println(ex.getMessage());
        }
    }

    private boolean readOCRGuideState(String file){
        try(BufferedReader bufferedReader=new BufferedReader(new FileReader(new File(file)))){
            return Boolean.parseBoolean(bufferedReader.readLine());
        }catch (IOException | NullPointerException ex){
            System.err.println(ex.getMessage());
            return Boolean.parseBoolean("false");
        }

    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK ){
            switch (requestCode) {
                case CAMERA_CAPTURE_OCR:
                    assert data != null;
                    runTextRecognition((Bitmap) Objects.requireNonNull(data.getExtras()).get("data"), editTextPanel);
                    break;
                case DOC_OCR:
                    assert data != null;
                    runTextRecognition(BitmapFactory.decodeFile(getExternalStorageDirectory() + "/" + Objects.requireNonNull(Objects.requireNonNull(data.getData()).getPath()).substring(Objects.requireNonNull(data.getData().getPath()).indexOf(":") + 1)),
                            editTextPanel);
                    break;
                case SCAN_BARCODE:
                    assert data != null;
                    runBarCodeScanner((Bitmap) Objects.requireNonNull(data.getExtras()).get("data"), editTextPanel);
                default:
                    break;
            }
        }
    }



    /**
     * Processes an image & extract the text from it
     * @param image the image to process in bitmap
     * @param editText the final editText where the processed text from the bitmap goes
     */
    private void runTextRecognition(Bitmap image, final RichEditor editText){
        InputImage inputImage=InputImage.fromBitmap(image, 0);
        TextRecognizer textRecognizer= TextRecognition.getClient();
        textRecognizer.process(inputImage).addOnSuccessListener(text ->
                editText.setHtml((editTextPanel.getHtml()==null?"":editTextPanel.getHtml())+"\n"+"  "+ text.getText()+"\n"))
                .addOnFailureListener(Throwable::printStackTrace);
    }

    /**
     * Processes a barcode/QrCode/matrixData image & extract the text then parse the extracted text to the editPanel
     * @param image the bitmap image to process
     * @param editTextPanel the final editText where the processed text from the bitmap goes
     */
    private void runBarCodeScanner(Bitmap image, final RichEditor editTextPanel) {
        InputImage inputImage = InputImage.fromBitmap(image, 0);
        BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions);

        barcodeScanner.process(inputImage).addOnSuccessListener(barCodes -> {

            StringBuilder data = new StringBuilder();
            for (Barcode barcode : barCodes) {
                switch (barcode.getValueType()) {
                    case Barcode.TYPE_PHONE:
                        data.append(Objects.requireNonNull(barcode.getPhone()).getNumber()).append(System.lineSeparator());
                        break;
                    case Barcode.TYPE_TEXT:
                        data.append(Objects.requireNonNull(barcode.getDisplayValue()).toCharArray()).append(System.lineSeparator());
                        break;
                    case Barcode.TYPE_URL:
                        data.append(Objects.requireNonNull(barcode.getUrl()).getUrl()).append(System.lineSeparator());
                        data.append(barcode.getUrl().getTitle()).append(System.lineSeparator());
                        break;
                    case Barcode.TYPE_WIFI:
                        data.append("SSID : ").append(Objects.requireNonNull(barcode.getWifi()).getSsid()).append(System.lineSeparator());
                        data.append("Password : ").append(barcode.getWifi().getPassword()).append(System.lineSeparator());
                        data.append("Encryption Type : ").append(barcode.getWifi().getEncryptionType()).append(System.lineSeparator());
                        break;
                }
            }

            editTextPanel.setHtml((editTextPanel.getHtml() == null ? "" : editTextPanel.getHtml()) + "\n" + "  " + data + "\n");

        });
    }




    /*
    /**
     * Controls the bitmap image rotation angle
     * @param source the bitmap
     * @param angle the angle of rotation usually in rads
     * @return
     *
    *private  Bitmap rotateBitmap(Bitmap source, float angle){
    *
    *    Matrix matrix = new Matrix();
    *    matrix.setRotate(angle);
    *    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    *}
    */

}
