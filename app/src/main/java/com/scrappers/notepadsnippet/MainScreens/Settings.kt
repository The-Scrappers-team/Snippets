@file:Suppress("DEPRECATION")

package com.scrappers.notepadsnippet.MainScreens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.view.View
import android.widget.RadioButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.high_quality
import com.scrappers.notepadsnippet.MainScreens.MainActivity.*
import com.scrappers.notepadsnippet.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess
import android.hardware.fingerprint.FingerprintManager as FingerprintManager2


class Settings : AppCompatActivity() {

    lateinit var  btnfingerprint:Switch

    override fun onCreate(savedInstanceState: Bundle?) {
//        Defining the existence of new theme
        checkForTheme()
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_settings)
        checkForFingerPrint()
        //to open the activity for the 1st time only
        //changing the SharedPreferences configuration

        val prefs = getSharedPreferences("recordstate", Context.MODE_PRIVATE)
        high_quality = prefs.getBoolean("high_quality", true)

        val btnhigh:RadioButton=findViewById(R.id.high)
        btnhigh.setOnClickListener{
            //turn it to high quality
            //changing the SharedPreferences configuration
            turnOnHighAudioQuality(true)
        }

        val lowbtn:RadioButton=findViewById(R.id.low)
        lowbtn.setOnClickListener{
            //turn it to low quality
            //changing the SharedPreferences configuration

            turnOnHighAudioQuality(false)


        }

        if(high_quality){
            btnhigh.isChecked=true
            lowbtn.isChecked=false
        }else{
            btnhigh.isChecked=false
            lowbtn.isChecked=true
        }
    }

    private fun turnOnHighAudioQuality(condition:Boolean){
        val prefs = getSharedPreferences("recordstate", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("high_quality", condition)
        //then apply the preferences changes
        editor.apply()
    }
    //reading fingerprint data function used in reading data
    private fun readFingerprint():String{
        try{
                //read that file (fingerprint data)
                val br = BufferedReader(FileReader(fileForFingerPrint))
                if (br.ready()) {
                    //reading first line of that db file
                    fingerprint = br.readLine()
                    }
                br.close()
                //catching an IllegalStateException if user haven't given the access permission to Storage Yet Read/Write especially write
            } catch (e: Exception) {
                e.printStackTrace()
        }
        return fingerprint
    }

    fun defaultTheme(view: View){
        applyTheme("R.style.AppTheme")
    }
    fun greenTheme(view: View){
        applyTheme("R.style.GreenTheme")
    }
    fun grayScaleTheme(view: View){
        applyTheme("R.style.GrayScaleTheme")
    }
    fun titanTheme(view: View) {
        applyTheme("R.style.TitanTheme")
    }
    fun cyanTheme(view: View) {
        applyTheme("R.style.CyanTheme")
    }

    fun fingerprintButton(view: View) {
        when (readFingerprint()) {
            "lock" -> {
                changeFingerprintState("unlock")
                Toast.makeText(applicationContext, "Unlock with Fingerprint DeActivated",Toast.LENGTH_LONG).show()
                val btnfingerprint:Switch=findViewById(R.id.fingerprintbtn)
                btnfingerprint.isChecked=false
                btnfingerprint.isEnabled=false
            }
            "unlock" -> {
                changeFingerprintState("lock")
                Toast.makeText(applicationContext,"Unlock with Fingerprint Activated",Toast.LENGTH_LONG).show()
                val btnfingerprint:Switch=findViewById(R.id.fingerprintbtn)
                btnfingerprint.isChecked=true
                btnfingerprint.isEnabled=false
            }
        }
    }
    private fun changeFingerprintState(state:String){
        val bw = BufferedWriter(FileWriter(fileForFingerPrint))
        bw.write(state)
        bw.close()
    }


    @Suppress("UNREACHABLE_CODE")
    fun applyTheme(theme:String) = try {
        val bw = BufferedWriter(FileWriter(fileForTheme))
        bw.write(theme)
        bw.close()
        exitProcess(0)
        startActivity(Intent(this, MainActivity::class.java))
    }
    catch (e:Exception){
        e.printStackTrace()
    }

    private fun checkForTheme(){
        when {
            Theme.contains("GreenTheme") -> setTheme(R.style.GreenTheme)
            Theme.contains("AppTheme") -> setTheme(R.style.AppTheme)
            Theme.contains("GrayScaleTheme") ->setTheme(R.style.Darky)
            Theme.contains("TitanTheme") ->setTheme(R.style.orangeLover)
            Theme.contains("CyanTheme") ->setTheme(R.style.BlueDark)
        }
    }


    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    fun checkForFingerPrint(){
        try{
        val fingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager2

        //Check whether the device has a fingerprint sensor//
        if (!fingerprintManager.isHardwareDetected) {
            // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
            Toast.makeText(applicationContext,"Device doesn't support fingerprint ",Toast.LENGTH_LONG).show()
            btnfingerprint=findViewById(R.id.fingerprintbtn)
            btnfingerprint.isEnabled=false
        }else{
            Toast.makeText(applicationContext,"Device supports fingerprint ",Toast.LENGTH_LONG).show()

            val btnfingerprint:Switch=findViewById(R.id.fingerprintbtn)
            btnfingerprint.isEnabled=true

            when (readFingerprint()) {
                "lock" -> {
                    btnfingerprint.isChecked=true
                }
                "unlock" -> {
                    btnfingerprint.isChecked=false

                }
            }
        }
    }catch (e:TypeCastException){
        e.printStackTrace()
            btnfingerprint=findViewById(R.id.fingerprintbtn)
            btnfingerprint.isEnabled=false
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        //transition
        overridePendingTransition(R.anim.slide_down_activity, R.anim.slide_down_activity)
        finish()
        Runtime.getRuntime().freeMemory()
        Runtime.getRuntime().gc()
    }


    fun backupData(view: View){
        zipdir("${filesDir}/SPRecordings/Notes/","${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}")
        Snackbar.make(view,"Backing up is done${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}",Snackbar.LENGTH_LONG).show()
    }


    fun restoreData(view: View){
        unzipBackup("${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}","${filesDir}/SPRecordings/Notes/",view)
    }


    private fun zipdir(filesdir:String, outputBackupZip:String){

    try {
        val fos = FileOutputStream(outputBackupZip)
        //we will deal w/ this
        val zipOut = ZipOutputStream(fos)
        //this is our directory for listing files
        val sourcedir = File(filesdir)

        val files = sourcedir.listFiles()

        for (file in files) {

            zipOut.putNextEntry(ZipEntry(file.name))

            //InputStream to read what's inside each file & save it to the new file zip
            val fis = FileInputStream("${sourcedir}${"/"}${file.name}")

            //a byte array of size 1024 for reading data inside the sourcedir files and outputting it to the new files in the new zip
            val bytes = ByteArray(1024)
            //for identifying the position of the byte array characters
            var length: Int
            //for iterating throughout the byte Array
            while (fis.read(bytes).also { length = it } >= 0) {
                //outputting data read & stored in that byte array to the new zipped file
                zipOut.write(bytes, 0, length)
            }

        }
        //close the streams
        zipOut.close()
        fos.close()
    }catch (e:Exception){
        //make the parent dir if it doesn't exist
        File("${getExternalStorageDirectory()}/SPRecordings/").mkdir()
        //make the Child dir if it doesn't exist
        File("${getExternalStorageDirectory()}/SPRecordings/Backup/").mkdir()
        //re-Zip and back the files
        zipdir("${filesDir}/SPRecordings/Notes/","${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}")

    }
    }



    private fun unzipBackup(BackupZipdir:String, extractedFilesDir:String, view: View){
        try{
                    val zis=ZipInputStream(FileInputStream(File(BackupZipdir)))
                    var zipEntry:ZipEntry=zis.nextEntry
                    //a byte array of size 1024 for reading data inside the sourcedir files and outputing it to the new files in the new zip
                    val bytes = ByteArray(1024)

            Snackbar.make(view,"Restoring Notes is done from ${getExternalStorageDirectory()}/SPRecordings/${"NotesBackup.zip"}" +
                    " Please restart the app!",Snackbar.LENGTH_LONG).show()

                    while(zipEntry != null) {
                        //for identifying the position of the bytearray characters
                        val file = File(extractedFilesDir, zipEntry.name)
                        val outputdata = FileOutputStream(file)
                            var length: Int
                            //for iterating throughout the byte Array
                            while (zis.read(bytes).also { length = it } > 0) {
                                //outputting data read & stored in that byte array to the new zipped file
                                outputdata.write(bytes, 0, length)
                            }
                            outputdata.close()
                            zipEntry = zis.nextEntry
                        }
                        zis.closeEntry()
                        zis.close()


        }catch (E:FileNotFoundException){
            E.printStackTrace()
            Snackbar.make(view,"Backup file zip not found  ${getExternalStorageDirectory()}/SPRecordings/${"NotesBackup.zip"} not found",Snackbar.LENGTH_LONG).show()
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }



    }




}


