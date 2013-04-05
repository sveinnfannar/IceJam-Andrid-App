package net.paolorovelli.IceJam;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Settings activity of the game.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class SettingsActivity extends Activity {
    private DatabaseHelper db = new DatabaseHelper(this);  // SQLite DB object
    final Context context = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        View rootView = findViewById(android.R.id.content);
        applyCustomFont((ViewGroup)rootView, Typeface.createFromAsset(getAssets(), "fonts/viking.ttf"));
    }

    public static void applyCustomFont(ViewGroup list, Typeface customTypeface) {
        for (int i = 0; i < list.getChildCount(); i++) {
            View view = list.getChildAt(i);
            if (view instanceof ViewGroup) {
                applyCustomFont((ViewGroup) view, customTypeface);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(customTypeface);
            }
        }
    }

    public void buttonClick(View view) {
        int id = view.getId();

        //Play a sound:
        view.playSoundEffect( android.view.SoundEffectConstants.CLICK );

        //Debug:
        System.out.println("[SETTINGS] Sound: " + view.isSoundEffectsEnabled());

        //case R.id.buttonClearHistory:
        /* --- BEGIN Dialog: --- */
        //Open custom dialog:
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.settingsdialog);
        dialog.setTitle("Clear history?");

        //Set the dialog number of moves:
        //TextView dgTextView = (TextView) dialog.findViewById(R.id.dialogMoves);
        //dgTextView.setText("Are you sure you want to delete all the history?");

        //Set the dialog menu button:
        Button dialogButtonNo = (Button) dialog.findViewById(R.id.dialogButtonNo);
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Close the dialog:
                dialog.dismiss();
            }
        });

        //Set the dialog menu button:
        Button dialogButtonYes = (Button) dialog.findViewById(R.id.dialogButtonYes);
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.clearRecords();

                //Close the dialog:
                //dialog.dismiss();

                //Start the new level:
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);  // come back to the main activity...
            }
        });

        //Show the dialog:
        dialog.show();
        /* --- END Dialog. --- */
    }
}