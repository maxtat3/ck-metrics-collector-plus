package berthold.filedialog;
/**
 * Demo activity for the File Dialog Tool.
 * <p>
 * Shows how to use this tool in your own app.
 */

import androidx.appcompat.app.AppCompatActivity;

import berthold.filedialogtool.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.ObjectStreamClass;

public class MainActivity extends AppCompatActivity implements CallingActivitysInstance {

    // File Chooser
    private String currentPath;
    private static final int getPath = 123;

    // If true, fileChooser will not open last folder opened which was
    // open when last session was left.
    private boolean overrideLastPathVisitedChecked;

    // UI
    private Button loadFile;
    private Button saveFile;
    private CheckBox overrideLastPathVisited;

    /*
     * On Create....
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show selected path
        TextView t;
        t = (TextView) findViewById(R.id.path);
        t.setText(currentPath);

        // UI
        loadFile = (Button) findViewById(R.id.pick_a_file_to_load_button);
        saveFile = (Button) findViewById(R.id.pick_save_loc_button);
        overrideLastPathVisited = (CheckBox) findViewById(R.id.override_last_path_visited);

        // Buttons
        // Pick a file?
        loadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FileDialog.class);
                i.putExtra(FileDialog.MY_TASK_FOR_TODAY_IS, FileDialog.GET_FILE_NAME_AND_PATH);
                i.putExtra(FileDialog.OVERRIDE_LAST_PATH_VISITED, overrideLastPathVisitedChecked);
                startActivityForResult(i, getPath);
            }
        });

        // Save a file?
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FileDialog.class);
                i.putExtra(FileDialog.MY_TASK_FOR_TODAY_IS, FileDialog.SAVE_FILE);
                i.putExtra(FileDialog.OVERRIDE_LAST_PATH_VISITED, overrideLastPathVisitedChecked);
                startActivityForResult(i, getPath);
            }
        });

        // Checkbox. Override last path visited?
        overrideLastPathVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    overrideLastPathVisitedChecked = true;
                    Log.v("MAIN:", "checked");
                } else {
                    overrideLastPathVisitedChecked = false;
                    Log.v("MAIN:", "not checked");
                }
            }
        });
    }

    /**
     * Resume
     *
     * When the activity was paused and restarted, this method is
     * executed.
     *
     */

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("---------------Resume");
    }

    /**
     * Get result of the called activity
     *
     */

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode,resCode,data);
        if (resCode == RESULT_OK && reqCode == getPath) {
            if (data.hasExtra("path")) {
                TextView t = (TextView) findViewById(R.id.path);
                t.setText(data.getExtras().getString("path"));

                // When saving a file:
                // Check if just the folder was picked, in that case you have to take care
                // that a filename will be assigned or:
                // if a folder and an existing file was selected, in that case you may want to
                // check if the user wants the selected file to be overwritten...
                String returnStatus = data.getExtras().getString(FileDialog.RETURN_STATUS);

                if (returnStatus != null) {
                    if (returnStatus.equals(FileDialog.FOLDER_AND_FILE_PICKED)) {
                        t.append("========>>> Folder+ File Picked to save");
                    }

                    if (returnStatus.equals(FileDialog.JUST_THE_FOLDER_PICKED)) {
                        t.append("========>>> Only destination folder picked....");
                    }
                }
            }
        }
    }
}

