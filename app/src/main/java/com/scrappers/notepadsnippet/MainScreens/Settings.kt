@file:Suppress("DEPRECATION", "UNREACHABLE_CODE")

package com.scrappers.notepadsnippet.MainScreens

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
import com.scrappers.notepadsnippet.MainScreens.MainActivity.fileForFingerPrint
import com.scrappers.notepadsnippet.MainScreens.MainActivity.fingerprint
import com.scrappers.notepadsnippet.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess
import android.hardware.fingerprint.FingerprintManager as FingerprintManager2


class Settings : AppCompatActivity() {

    private lateinit var btnFingerPrint:Switch

    override fun onCreate(savedInstanceState: Bundle?) {
//        Defining the existence of new theme
        readTheme()
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_settings)
        checkForFingerPrint()
        btnFingerPrint=findViewById(R.id.fingerPrintBtn)
        btnFingerPrint.isChecked = (readFingerprint() == "lock")
        btnFingerPrint.setOnClickListener {
            when (btnFingerPrint.isChecked) {
                false -> {
                    changeFingerprintState("unlock")
                    Toast.makeText(applicationContext, "Unlock with Fingerprint DeActivated",Toast.LENGTH_LONG).show()
                }
                true -> {
                    changeFingerprintState("lock")
                    Toast.makeText(applicationContext,"Unlock with Fingerprint Activated",Toast.LENGTH_LONG).show()
                }
            }
        }
        //to open the activity for the 1st time only
        //changing the SharedPreferences configuration

        val prefs = getSharedPreferences("recordstate", Context.MODE_PRIVATE)
        high_quality = prefs.getBoolean("high_quality", true)

        val btnHighQuality:RadioButton=findViewById(R.id.high)
        btnHighQuality.setOnClickListener{
            //turn it to high quality
            //changing the SharedPreferences configuration
            changeAudioQuality(true)
        }

        val btnLowQuality:RadioButton=findViewById(R.id.low)
        btnLowQuality.setOnClickListener{
            //turn it to low quality
            //changing the SharedPreferences configuration
            changeAudioQuality(false)
        }

        if(high_quality){
            btnHighQuality.isChecked=true
            btnLowQuality.isChecked=false
        }else{
            btnHighQuality.isChecked=false
            btnLowQuality.isChecked=true
        }
    }

    /**
     * changes Audio Quality using SharedPrefs
     * @param condition
     */
    private fun changeAudioQuality(condition:Boolean){
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

    /**
     * changes the theme + apply & save in /data/SPRecordings/config/Theme.scrappers
     * @param view the current button view
     */
    fun changeTheme(view: View) = try {
        val bw = BufferedWriter(FileWriter(File(applicationContext.filesDir.toString() + "/SPRecordings/config/" + "Theme.scrappers")))
        bw.write(view.tag.toString())
        bw.close()
        exitProcess(0)
        startActivity(Intent(this, MainActivity::class.java))
    }
    catch (e:Exception){
        e.printStackTrace()
    }

    /**
     * changes the fingerprint state
     * @param state the new fingerprint state that you plan to save
     */
    private fun changeFingerprintState(state:String){
        val bw = BufferedWriter(FileWriter(fileForFingerPrint))
        bw.write(state)
        bw.close()
    }


    /**
     * Reads the theme from the file directory && Applies it
     *
     */
    private fun readTheme() {
        try {
            //read themes in that file
            val br = BufferedReader(FileReader(MainActivity.fileForTheme))
            if (br.ready()) { //reading first line of that db file
                MainActivity.Theme = br.readLine()
                //applying themes according to the content
                when {
                    MainActivity.Theme.contains("GreenTheme") -> {
                        setTheme(R.style.GreenTheme)
                    }
                    MainActivity.Theme.contains("AppTheme") -> {
                        setTheme(R.style.AppTheme)
                    }
                    MainActivity.Theme.contains("Darky") -> {
                        setTheme(R.style.Darky)
                    }
                    MainActivity.Theme.contains("orangeLover") -> {
                        setTheme(R.style.orangeLover)
                    }
                    MainActivity.Theme.contains("BlueDark") -> {
                        setTheme(R.style.BlueDark)
                    }
                    //close the BR
                }
                //close the BR
                br.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Reads the fingerprint state & applies it
     */
    private fun checkForFingerPrint(){
        val fingerprintManager = getSystemService(FINGERPRINT_SERVICE) as FingerprintManager2
        //Check whether the device has a fingerprint sensor//
        if (!fingerprintManager.isHardwareDetected) {
            // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
            Toast.makeText(applicationContext,"Device doesn't support fingerprint ",Toast.LENGTH_LONG).show()
            btnFingerPrint=findViewById(R.id.fingerPrintBtn)
            btnFingerPrint.isEnabled=false
        }else{
            Toast.makeText(applicationContext,"Device supports fingerprint ",Toast.LENGTH_LONG).show()
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

    /**
     * back-ups notes listener
     * @param view the current button pressed view
     */
    fun backupData(view: View){
        zipDir("${filesDir}/SPRecordings/Notes/","${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}")
        Snackbar.make(view,"Backing up is done${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}",Snackbar.LENGTH_LONG).show()
    }

    /**
     * restores notes listener
     * @param view defines button view
     */
    fun restoreData(view: View){
        unzipBackup("${getExternalStorageDirectory()}/SPRecordings/Backup/${"NotesBackup.zip"}","${filesDir}/SPRecordings/Notes/",view)
    }


    private fun zipDir(filesDir:String, outputBackupZip:String){
        try {
            val fos = FileOutputStream(outputBackupZip)
            //we will deal w/ this
            val zipOut = ZipOutputStream(fos)
            //this is our directory for listing files
            val sourceDir = File(filesDir)

            val files = sourceDir.listFiles()

            for (file in files) {

                zipOut.putNextEntry(ZipEntry(file.name))

                //InputStream to read what's inside each file & save it to the new file zip
                val fis = FileInputStream("${sourceDir}${"/"}${file.name}")

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
            e.printStackTrace()
            Toast.makeText(applicationContext,"No Notes to BackUp",Toast.LENGTH_LONG).show()
        }
    }



    private fun unzipBackup(BackupZipDir:String, extractedFilesDir:String, view: View){
        try{
                    val zis=ZipInputStream(FileInputStream(File(BackupZipDir)))
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