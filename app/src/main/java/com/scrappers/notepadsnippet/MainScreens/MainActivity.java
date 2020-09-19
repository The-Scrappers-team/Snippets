package com.scrappers.notepadsnippet.MainScreens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.scrappers.notepadsnippet.AboutActivity.aboutActivity;
import com.scrappers.notepadsnippet.FingerPrint.FingerPrint;
import com.scrappers.notepadsnippet.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import io.github.douglasjunior.androidSimpleTooltip.ArrowDrawable;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

import static android.os.Environment.getExternalStorageDirectory;
import static com.scrappers.notepadsnippet.FeaturesShowUp.SliderActivity.firstStart;
import static java.lang.System.out;


public class MainActivity extends AppCompatActivity  {


    private static final int REQUEST_READ_WRITE =1 ;
    //Attributes/Fields/Vars/Objects
    MainActivityListAdapter lsAdapter;
    ArrayList<String> mainTitle = new ArrayList<>();
    ArrayList<String> subTitle = new ArrayList<>();
    GridView gridView;

    public static int isEditEntry = 0;
    public static String fileName;
    public static String recordName;
    @SuppressLint("StaticFieldLeak")
    static SearchView searchView;
    int numOfColumns = 0;
    ArrayAdapter<String> adapter;
    static String finalOutText;

    //Theme Attributes/Fields/vars
    public static String Theme = "";
    public static String fileForTheme = "";
    public static int notesNumber;

    //Theme Attributes/Fields/vars
    public static String fingerprint = "";
    public static String fileForFingerPrint = "";

    public static boolean FINGERPRINT_TRANSACTION = false;
    public static boolean DISABLE_FINGERPRINT = false;
    //Renaming note AlertDialog attribute
    AlertDialog renameDialog;
    int shift = 0;
    public static boolean NoMoreTutorials = true;

    public void showToolTipWindow(View view, int ArrowDirection, String message) {
        if ( NoMoreTutorials ){
            SimpleTooltip.Builder tooltip = new SimpleTooltip.Builder(this);
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                tooltip.anchorView(view)
                        .text(message)
                        .gravity(Gravity.END)
                        .arrowDirection(ArrowDirection)
                        .dismissOnOutsideTouch(true)
                        .animated(true)
                        .arrowColor(getResources().getColor(R.color.fab_red_color,null))
                        .transparentOverlay(false)
                        .textColor(getColor(R.color.white))
                        .backgroundColor(getResources().getColor(R.color.fab_red_color,null))
                        .onDismissListener(tooltip1 -> {
                            tooltip1.dismiss();
                            ++shift;
                            switch (shift) {
                                case 1:
                                    tooltip1.dismiss();
                                    showToolTipWindow(findViewById(R.id.settings), ArrowDrawable.RIGHT, "Dashboard");
                                    break;
                                case 2:
                                    tooltip1.dismiss();
                                    showToolTipWindow(findViewById(R.id.search_bar), ArrowDrawable.LEFT, "Search your note");
                                    break;
                                case 3:
                                    tooltip1.dismiss();
                                    showToolTipWindow(findViewById(R.id.listFiles), ArrowDrawable.LEFT, "Your Notes");
                                    break;
                                default:
                                    tooltip1.dismiss();
                                    break;
                            }
                        })
                        .build()
                        .show();
            }

        }
    }

    //Methods/Functions
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //accessing permissions write/read files from the Internal Storage
        writeExternalStoragePermission();
        //Reading themes database
        ReadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Condition containing an operand operation where
            //FINGERPRINT_TRANSACTION is to:
        //DISABLE_FINGERPRINT is to:
        if( !FINGERPRINT_TRANSACTION && !DISABLE_FINGERPRINT ){
            ReadFingerPrint();
        }
        //reading fingerprint database to see if its enabled or not
//        ReadFingerPrint();
        //check files in the Application Cache & Data Directory & fetch & Display Them onto my ArrayAdapter of my ListView/RecycleViewer
        fetch_FileNames(getApplicationContext().getFilesDir() + "/SPRecordings/Notes");
        //defining the ArrayAdapter / subtitle & MainTitle
        defineAdapter();
        //checking for columns data of gridView
        CheckColumnsViewData();
        //Running the SearchView & its Listeners + Animations
        SearchView();

//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
//            startForegroundService(new Intent(this, PushNotifications.class));
//            ComponentName receiver = new ComponentName(getApplicationContext(), PushNotifications.class);
//            PackageManager pm = this.getPackageManager();
//
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
//        }

        if ( firstStart ){
            showToolTipWindow(findViewById(R.id.addbtn),ArrowDrawable.LEFT,"Add new Note");
        }
    }


    public void Make_Expansion_Dirs() {
        boolean isDirReady;
        String[] dirs={
                getApplicationContext().getFilesDir() +  "/SPRecordings",
                getApplicationContext().getFilesDir() +  "/SPRecordings/Notes",
                getApplicationContext().getFilesDir() +  "/SPRecordings/todoLists",
                getApplicationContext().getFilesDir()+"/SPRecordings/fingerprint",
                getApplicationContext().getFilesDir()+"/SPRecordings/config",
                getExternalStorageDirectory() + "/SPRecordings",
                getExternalStorageDirectory() +  "/SPRecordings/records",
                getExternalStorageDirectory() +  "/SPRecordings/paints",
                getExternalStorageDirectory() + "/SPRecordings/Backup"
        };

        for(String dir : dirs) {
            //make new dirs
            File directory = new File(dir);
            isDirReady=directory.mkdir();
            if(isDirReady){
                Toast.makeText(getApplicationContext(),"Snippets is Ready !",Toast.LENGTH_LONG).show();
            }else{
                if(!directory.exists()){
                    Toast.makeText(getApplicationContext(), "Please Give Snippet the Regular Permissions,So you can use its full functionality !", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Snippets is Ready !",Toast.LENGTH_LONG).show();
                }
            }
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FINGERPRINT_TRANSACTION=false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FINGERPRINT_TRANSACTION=false;

    }

    //Reads the theme from the file directory && Applies it
    private void ReadTheme() {
        try {
            //themes directory & themes files w/ scrappers extension :->))
            fileForTheme= (getApplicationContext().getFilesDir()+ "/SPRecordings/config/" +"Theme.scrappers");
            //read themes in that file
            BufferedReader br = new BufferedReader(new FileReader(fileForTheme));
            if ( br.ready() ){
                //reading first line of that db file
                Theme = br.readLine();
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
                //close the BR
                br.close();
            }
            //Exception if the file isn't found --> add that bad boy :->))
        } catch (Exception error) {
            error.printStackTrace();
            //if the theme file isn't found -> add it w/ the default theme
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileForTheme));
                bw.write("R.style.AppTheme");
                bw.close();

                //catching an IllegalStateException if user haven't given the access permission to Storage Yet Read/Write especially write
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ReadFingerPrint() {
        try {
            //themes directory & themes files w/ scrappers extension :->))
            fileForFingerPrint= (getApplicationContext().getFilesDir()+ "/SPRecordings/fingerprint/" +"fingerprint.scrappers");
            //read themes in that file
            BufferedReader br = new BufferedReader(new FileReader(fileForFingerPrint));
            if ( br.ready() ){
                //reading first line of that db file
                fingerprint = br.readLine();
                //applying themes according to the content
                if(fingerprint.equals("lock")){
                    finish();
                    startActivity(new Intent(this, FingerPrint.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
                //close the BR
                br.close();
            }
            //Exception if the file isn't found --> add that bad boy :->))
        } catch (IOException error) {
            error.printStackTrace();
            //if the theme file isn't found -> add it w/ the default theme
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileForFingerPrint)));
                bw.write("unlock");
                bw.close();
                //catching an IllegalStateException if user haven't given the access permission to Storage Yet Read/Write especially write
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void SearchView() {
        //Handling SearchView And SearchView Animation & Listeners
        searchView = findViewById(R.id.search_bar);
        //SearchView open Listener
        searchView.setOnSearchClickListener(v -> {
            //use animation w/ searchBar SearchView
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_up);
            //starting animation
            searchView.startAnimation(anim);
        });


        //SearchView onClose Listener
        searchView.setOnCloseListener(() -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
            searchView.startAnimation(anim);

            //Animation Listener w/ overridable methods
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                            //refresh by restarting the Application MainActivity.class
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            return true;
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                gridView.setAdapter(adapter);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }





    //Requesting External Storage Permissions for read/write data
    public void writeExternalStoragePermission() {
        //accessing permissions from the AndroidManifest.xml
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_READ_WRITE );

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_READ_WRITE){
            Make_Expansion_Dirs();
        }
    }

    //setting up Refreshing Layout & its Style & Refresh Listener & ListView ArrayAdapter updater
    public void Refresh(){
        final PullRefreshLayout refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        refreshLayout.setOnRefreshListener(() -> new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //redefining the adapter leads to refreshing the layout view
                defineAdapter();
                //set refreshing state to false
                refreshLayout.setRefreshing(false);
                //return true for the finger print to be able to refresh the state
                // w/o fingerprint page/activity being shown
                FINGERPRINT_TRANSACTION = true;
                //cancel the timer to restart the process
                this.cancel();
            }
        }.start());

    }


    //defining the ListView ArrayAdapter Method
    //-> fetching the mainTitle ArrayList data & subTitle ArrayList Data from fetch_FileNames(...) method to the ListView/RV Adapter
    public void defineAdapter() {

        //defining ListView ArrayAdapter Custom Instance/Object
        lsAdapter = new MainActivityListAdapter(this, mainTitle,subTitle);
        //Defining a normal/Standard ArrayAdapter Instance/Object for displaying the Search Results
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,mainTitle);
        //Defining ListView Instance for that Specific id
        gridView = findViewById(R.id.listFiles);
        //setting up the CustomArrayAdapter to the list view Layout
        gridView.setAdapter(lsAdapter);
        //Running the refresh layout for displaying the Refresh Bar
        Refresh();
        //Defining ListView on item click listener for handling the 2nd Activity
        //-> Handling the open of the previously Created Notes(Your already created Notes) to edit them if you wish so...
        //Overriding an abstract method principle in java
//-> onItemClick(....); is an abstract method(void having no body,but name & SemiColon) in OnItemClickListener() Interface
// & in abstract class AdapterView & Must be Overridden to workout
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            //Starting the EditPaneActivity activity/Intent from this Intent(Context)
            startActivity(new Intent(getApplicationContext(), EditPaneActivity.class));
            //Transition Animation
            overridePendingTransition(R.anim.slide_in_activity,R.anim.slide_out_activity);
            //finishing/exiting this Activity
            finish();
            //Assigning fileName String(Static) to the Current Selected Note to use it in reading the data inside the note using readTextFiles(...) Methods
            //-> & using it in saving the note w/ the same Name using the sameSaveName Btn in
            //DialogBoxWithRecordSave(...)/DialogBoxWithoutRecordSave(...) Methods
            // in EditPaneActivity.java class :-))
            fileName = lsAdapter.getItem(position);
            try {
                //reading the data of the Currently Selected Note(fileName)
                // -> & its record File(if exists & if not -> NullPointerException -> e is handled in stack Trace)
                readTextFiles(getApplicationContext().getFilesDir() + "/SPRecordings/Notes/" + fileName);
                recordName = getExternalStorageDirectory() + "/SPRecordings/records/" + fileName
                        + ".mp3";
                //triggerEdit is an int var -> to inform the second activity if this was a new Note or an edit for a previous Note
                //triggerEdit=1 --> trigger an edit to a previous Note -> opens the second Activity(EditPaneActivity.java) on the data of this note
                // -> to be able to edit it
                //triggerEdit=0 --> trigger a new Note -> informs the secondActivity(EditPaneActivity.java) to be opened up w/ no data & no previous Name(not null)
                isEditEntry = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //defining an AlertDialogBuilder using Builder static Class(There are other ways) to build the AlertDialog on it
        //NB(Deep Knowledge) :
        // final -> is a keyword not an access Modifier -> to make this attribute immutable(unchangeable) & to access the object builder from w/in the inner class AdapterView
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        //Defining ListView on item long click listener for handling the delete Note(w/ delete all its data & record) Action
        //-> Handling the delete of the previously Created Notes(Your already created Notes)
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            //set The Message for that Builder(Hint!!!-> Revise getters & setters in java)
            //abstraction & Interfaces Applied
            builder.setMessage("Are you Sure you want to delete this item ?")
                    //setting the positive btn
                    .setPositiveButton("Yes", (dialog, id1) -> {
                        try {
                            boolean isDeleted;
                            try {
                                /*
                                 *this deletes all the todoNotes inside this particular note directory before deleting the directory
                                 */
                                File todoNoteDir = new File(getApplicationContext().getFilesDir() + "/SPRecordings/todoLists/" + lsAdapter.getItem(position));
                                File[] todoNotes = todoNoteDir.listFiles();
                                for (File todoNote : todoNotes) {
                                    isDeleted = todoNote.delete();
                                    out.println(isDeleted);
                                }
                            }catch (NullPointerException ex){
                                System.err.println(ex.getMessage());
                            }
                            /*
                             * Initialize files to delete
                             */
                            String[] files={
                                    getApplicationContext().getFilesDir() + "/SPRecordings/Notes/" + lsAdapter.getItem(position),
                                    getApplicationContext().getFilesDir() + "/SPRecordings/todoLists/" + lsAdapter.getItem(position),
                                    getExternalStorageDirectory() + "/SPRecordings/records/" + lsAdapter.getItem(position) + ".mp3",
                                    getExternalStorageDirectory() +  "/SPRecordings/paints/"+lsAdapter.getItem(position)+".png"
                            };
                            /*
                             *Delete Files one by one for this particular note
                             */
                            for(String dir : files){
                                File file=new File(dir);
                                isDeleted=file.delete();
                                if(isDeleted){
                                    Toast.makeText(getApplicationContext(),"Note & its companies are deleted Successfully",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Failed to delete the note",Toast.LENGTH_SHORT).show();
                                }
                            }

                            //restarting the activity to refresh its state(ListView adapter) after deleting that file
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            //finishing the previous activity to prevent activities overlapping & lags
                            finish();

                        } catch (NullPointerException e) {
                            //printing the stackError into the Logcat(There are many other ways to do so)
                            e.printStackTrace();
                        }

                    }).setNeutralButton("Rename", (dialog, which) -> runRenameDialog(position))

                    //setting up the -'ve btn to No & its onclick listener
                    //->NB(Deep Knowledge) -> its click Listener calls the d2.dismiss(); spontaneously/Automatically
                    .setNegativeButton("No", (dialog, which) -> {
                        //->NB(Deep Knowledge) -> -'ve btn click Listener calls the d2.dismiss(); spontaneously/Automatically
                        dialog.dismiss();

                    })
                    //setting the cancelable() function to false -> you cannot cancel it by any action(eg:-pressing on white Surrounding screen) except the -ve btn
                    .setCancelable(false);
            //creating an AlertDialog instance and adding it to the builder that we 've configured its properties
            AlertDialog d2 = builder.create();
            //showing that dialog onto the screen
            d2.show();


            //function return word
            return true;


        });


    }

    //Rename Dialog method
    public void runRenameDialog(final int position){

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View layout = inflater.inflate(R.layout.dialogboxlayout, null);
        Button okBtn = layout.findViewById(R.id.okBtn);
        Button cancelBtn = layout.findViewById(R.id.CancelBtn);
        final EditText newName=layout.findViewById(R.id.nameofsavefile);
        builder.setView(layout);

        /*
         * renaming Button
         */
        okBtn.setOnClickListener(v -> {
            boolean renameChecker;
            String[] oldNames={
                    getApplicationContext().getFilesDir() + "/SPRecordings/Notes/" + lsAdapter.getItem(position),
                    getApplicationContext().getFilesDir() + "/SPRecordings/todoLists/" + lsAdapter.getItem(position),
                    getExternalStorageDirectory() + "/SPRecordings/records/" + lsAdapter.getItem(position) + ".mp3",
                    getExternalStorageDirectory() +  "/SPRecordings/paints/"+lsAdapter.getItem(position)+".png",

            };
            String[] newNames={
                    getApplicationContext().getFilesDir() + "/SPRecordings/Notes/" + newName.getText(),
                    getApplicationContext().getFilesDir() + "/SPRecordings/todoLists/" + newName.getText(),
                    getExternalStorageDirectory() + "/SPRecordings/records/" + newName.getText() + ".mp3",
                    getExternalStorageDirectory() + "/SPRecordings/paints/" + newName.getText()+".png",

            };
            for(int feature=0; feature<oldNames.length;feature++) {
                //make new directory for the paint views
                File file = new File(oldNames[feature]);
                renameChecker=file.renameTo(new File(newNames[feature]));
                if(renameChecker){
                    Toast.makeText(getApplicationContext(),"Rename is successful !",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Renaming 's failed,Try again !",Toast.LENGTH_LONG).show();
                }
            }
            //Dismiss that dialog after data transactions are completed
            renameDialog.dismiss();
            //refresh by restarting the Application Activity
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();

        });

        //cancel btn dismiss the dialog w/o data change(w/o Rename action)
        cancelBtn.setOnClickListener(v -> renameDialog.dismiss());

        builder.setCancelable(true);
        renameDialog = builder.create();
        Objects.requireNonNull(renameDialog.getWindow()).setGravity(Gravity.CENTER);
        renameDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation2;
        renameDialog.show();


    }
    //method to read data of the selected note from the ListView adapter
    //path for the path of files -> directory + Main Name of file
    public void readTextFiles(String path) {
        //try catch to catch an I/OException if there 're no files
        try {
            //making a new BufferedReader Instance  & parsing a new FileReader instance that contains that path
            BufferedReader reader = new BufferedReader(new FileReader(path));
            //to avoid showing null results w/ attempt to open the item
            if ( !reader.ready() ){
                finalOutText = "";
                //closing the reader & don't read anything if the file isn't ready
                reader.close();
            } else {
                //reading the first Line of that file's data & giving it a separate Line
                StringBuilder outputText = new StringBuilder(reader.readLine() + "\n");
                //check if the BufferedReader be able to read more data(Lines) inside that text file
                //reading other lines(if exist) & assigning them to the same String(first Line)
                while (reader.ready()) {
                    outputText.append(reader.readLine()).append("\n");
                }
                //assigning the outputText value to the finalOutText which is a global version of the outputText
                finalOutText = outputText.toString();
                //closing the BR for saving data
                reader.close();
                //Message Display
                Toast.makeText(getApplicationContext(), "Read Successful", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //Method Declared as public(Access Modifier/Specifier)<Visible outside the activity> -> To read fileNames from the app directory
    //& stores name in an array of Files
    // -> then adding those names to the ArrayList(mainTitle) to fetch them onto the ListView/RV adapter using the defineAdapter(...) method
    public void fetch_FileNames(String path) {
        try {
            //declaring a file instance w/ a specific read/write  path
            File[] files = new File(path).listFiles();
            //using foreach/for loop to list all files(We Prefer for loop to control the iteration purpose)
            for (File file : files) {
                //adding the files to the ArrayList mainTitle to fetch them onto the lsAdapter(ArrayAdapter of listView)
                mainTitle.add(file.getName());
                //using java date/time utility library to call the last modification of these files shown onto the adapter
                Date lastDate = new Date(file.lastModified());
                //adding the last modification date to these items onto the adapter as a subTitle Text
                subTitle.add(String.valueOf(DateFormat.getInstance().format(lastDate)));

            }
            notesNumber =files.length+1;
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            //printing the stackError into the Logcat(There are many other ways to do so)
            e.printStackTrace();
            //if you open the program brand new (there's zero notes there)
            notesNumber =1;
        }
    }

    //method for adding a new note
    // -> has a view for the addNewNote Btn onClickListener -> called in the activity_main.xml in the onClick Tag of the addBtn floating button
    public void addNewNote(View view) {
            //starting the secondActivity(EditPaneActivity.java) from this Intent
            startActivity(new Intent(getApplicationContext(), EditPaneActivity.class));
            //Transition Animation
            overridePendingTransition(R.anim.slide_in_activity,R.anim.slide_out_activity);
            //finishing that activity to prevent overlapping of activities & errors in data write/read
            finish();
            //giving the triggerEdit a zero value -> trigger the second Activity(EditPaneActivity.java) to open a new note
            isEditEntry = 0;
    }

    //method for the custom AlertDialog that ve been made for the 3 dot btn to display the settings & the about
    public void settingsAlert() {
        //defining an alertDialog builder instance in this context
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this,R.style.RoundAlertDialog);
        //defining a layout inflater instance to to inflate a view that has that custom layout w/in the inside
        //Deep:- final is a keyword -> Immutable & accessing data from an inner class (View class)
        final LayoutInflater inflater = this.getLayoutInflater();
        //defining a View instance/obj & inflating the custom layout
        @SuppressLint("InflateParams")
        final View layout = inflater.inflate(R.layout.alrtbox_items_menu, null);
        //setting that badBoy(view)to the current builder
        builder.setView(layout);

        //defining a button and fetching the btn id
        Button about = layout.findViewById(R.id.about);
        //btn OnClickListener has a parameter that has the abstract method onClick(View v); in the OnClickListener() interface in the View Class
        //Deep Knowledge:-the keyword-> new -> for creating the new instance for that Specific class & using it as a reference value in calling the abstract method onClick
        about.setOnClickListener(v -> {
            //starting the aboutActivity.class from that Intent w/o finishing that one because its an info activity so no data conflict/overlapping
            startActivity(new Intent(getApplicationContext(), aboutActivity.class));

        });
        //defining a button Instance/obj and fetching the btn id
        Button settings = layout.findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            //starting settings activity
            startActivity(new Intent(getApplicationContext(), Settings.class));
            //transition
            overridePendingTransition(R.anim.slide_up_activity,R.anim.slide_down_activity);

        });
//defining a button Instance/obj and fetching the btn id
        Button privacyPolicy = layout.findViewById(R.id.privacypolicy);
        privacyPolicy.setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://cozysnippets-notepad.flycricket.io/privacy.html"));
            startActivity(intent);
        });
        //creating an alertDialog instance and adding it to the builder
        AlertDialog alert = builder.create();
        //setting the window gravity to bottom
        Objects.requireNonNull(alert.getWindow()).setGravity(Gravity.BOTTOM);
        //running the animations on the alertDialog from styles.xml
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;
        //showing that alertDialog
        alert.show();
    }

    //The action Listener for the 3 dot button in the activity_main.xml
    public void setting(View view) {
        //calling the custom AlertDialog that has the 2 buttons(aboutBtn & SettingsBtn)
        settingsAlert();
    }


    // method for the action Listener for the grid btn in the activity_main.xml
    public void ChangeGridBtn(View view) {
        //method for changing the columns of the grid view according to its current state(Value)
        ChangeColumnsViewData();

    }


    //Handling GridView & Number of Columns -> writing the antagonist number
    public void writeNumOfColumns(int data) {
        try {
            //making a new BufferedWriter Instance & parsing a new FileWriter w/ the path for the number of columns
            BufferedWriter BWriter = new BufferedWriter(new FileWriter(getApplicationContext().getFilesDir()+"/SPRecordings/config"+
                    "column"));
            //writing data to the file
            BWriter.write(String.valueOf(data));
            //close to save & Buffer/update cache
            BWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //method to read number of columns when we open the application(called w/in the ChangeColumnsViewData() method & w/in CheckColumnsViewData()
    public void readNumOfColumns() {
        try {
            //BufferedReader Instance & FileReader Parsing
            BufferedReader bufferedReader = new BufferedReader(new FileReader(getApplicationContext().getFilesDir()+"/SPRecordings/config"+
                    "column"));
            //reading the 1st Line  -> in a String
            String numString = bufferedReader.readLine();
            //Converting the String into an Integer
            // -> so we can use it in setNumColumns() when we call it in the onCreate() method via the CheckColumnsViewData() method
            numOfColumns = Integer.parseInt(numString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //method for changing the gridView Columns depending on its current State
    // ---> used in the ChangeGridBtn(View view) (gridBtn onClickListener)
    public void ChangeColumnsViewData() {
        //checking if the number of columns is 1
        // -> Then set it to 2 -> write new data -> auto update view
        if ( gridView.getNumColumns() == 1 ){
            gridView.setNumColumns(2);
            writeNumOfColumns(2);
        }

        //checking if the number of columns is 2
        // -> Then set it to 1 -> write new data -> auto update view
        else if ( gridView.getNumColumns() == 2 ){
            gridView.setNumColumns(1);
            writeNumOfColumns(1);
        }
    }

    //checking the data of the file column in SPRecordings & setting the data to that gridView
    public void CheckColumnsViewData() {
        try {
            readNumOfColumns();
            gridView.setNumColumns(numOfColumns);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}