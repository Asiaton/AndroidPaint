package tiko.tamk.fi.androidpaint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Main and only activity of the app. Contains a PaintView object on which
 * everything in the app is drawn. Also contains methods for loading and
 * saving images, opening different dialogs, and creating the menu bar on
 * top of the application.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The PaintView object that contains all the information on drawing.
     */
    private PaintView paintView;

    /**
     * The path extension to make the app save images in the correct folder.
     */
    public final static String APP_PATH_SD_CARD = "/Paint05/";

    /**
     * Method that is called upon creation of the activity. It initializes
     * the PaintView object and necessary permissions to write and read
     * to and from the external storage (SD card)
     *
     * @param savedInstanceState Bundle that passes any previously
     *                           saved information to the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        paintView.init(displayMetrics);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            String [] listOfPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(this, listOfPermissions, 1);
        }
    }

    /**
     * Opens device gallery in new activity.
     */
    public void load() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    /**
     * Returns the image chosen in the activity started in the load() method.
     *
     * @param requestCode Code used to differentiate between different requests.
     * @param resultCode Code that tells whether the request was completed.
     * @param data The data returned from the activity, in this case data of
     *             the image that was loaded.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                paintView.loadBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the given image into device gallery using path defined before.
     *
     * @param paintView The PaintView from which saved image is created.
     */
    public void save(PaintView paintView) {

        Bitmap image = Bitmap.createBitmap(paintView.getWidth(),
                paintView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(image);
        paintView.draw(c);

        // The full path for saving the image
        String fullPath =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + APP_PATH_SD_CARD;

        // Check whether the necessary directories for saving exist.
        // If not, create them.
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, createUniqueFileName() + ".png");

            fOut = new FileOutputStream(file);

            // Quality of 100 means no compression. The lower you go,
            // the stronger the compression. 100 was chosen because the file
            // size isn't that big even without compression.
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), file.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates unique file name based on date and time. Every second has
     * a different file name.
     *
     * @return The unique name created.
     */
    public String createUniqueFileName() {
        String uniqueName = "img-";
        Calendar c = Calendar.getInstance();
        uniqueName += c.get(Calendar.YEAR);
        uniqueName += String.format("%02d", c.get(Calendar.MONTH));
        uniqueName += String.format("%02d", c.get(Calendar.DAY_OF_MONTH));
        uniqueName += "-";
        uniqueName += String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
        uniqueName += String.format("%02d", c.get(Calendar.MINUTE));
        uniqueName += String.format("%02d", c.get(Calendar.SECOND));

        return uniqueName;
    }

    /**
     * Creates AlertDialog with a SeekBar, and opens it. The dialog is
     * used to determine brush size for drawing.
     */
    public void createSeekDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Choose brush size");

        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);

        final SeekBar seek = new SeekBar(this);
        linear.addView(seek);

        alert.setView(linear);

        alert.setPositiveButton("Ok", (dialog, id) -> {
            String brushWidth = String.valueOf(seek.getProgress());
            Toast.makeText(getApplicationContext(),
                    "Brush size: " + brushWidth,
                    Toast.LENGTH_LONG).show();
            paintView.setStrokeWidth(seek.getProgress());
        });

        alert.setNegativeButton("Cancel", (dialog, id) -> {
                Toast.makeText(getApplicationContext(),
                "Canceled",
                Toast.LENGTH_LONG).show();
        });

        alert.show();
    }

    /**
     * Creates a dialog for picking a color, and opens it.
     *
     * @param colorTarget Determines whether chosen color is applied to
     *                    brush or background.
     */
    public void createColorPicker(final int colorTarget) {
        new AmbilWarnaDialog(this,
                paintView.getCurrentColor(),
                true,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.
                        if (colorTarget == 0) {
                            paintView.setCurrentColor(color);
                        } else if (colorTarget == 1) {
                            paintView.setBackgroundColor(color);
                        }
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                }).show();
    }

    /**
     * Creates a dialog containing credits, and opens it.
     */
    public void openCredits() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Author: Tom Bäckman" +
                "\n\nMenu icons made by Smashicons, monkik " +
                "and Gregor Cresnar from www.flaticon.com." +
                "\n\nAmbilWarnaDialog by yukuku was used for " +
                "the color picker dialog.");

        builder.setPositiveButton("Ok, cool!", (dialog, id) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates the menu bar from xml file main.xml
     *
     * @param menu Menu that is created.
     * @return Whether the menu will be displayed or not.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Creates the functionality for the menu items.
     *
     * @param item Chosen menu item
     * @return Return false to allow normal menu processing to proceed,
     *          true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.eraser:
                paintView.setCurrentColor(paintView.getBackgroundColor());
                paintView.normal();
                return true;
            case R.id.color:
                createColorPicker(0);
                return true;
            case R.id.backgroundColor:
                createColorPicker(1);
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
            case R.id.undoLine:
                paintView.undoLine();
                return true;
            case R.id.undoShape:
                paintView.undoShape();
                return true;
            case R.id.line:
                paintView.setDrawLine(true);
                return true;
            case R.id.rectangle:
                paintView.setDrawRectangle(true);
                return true;
            case R.id.circle:
                paintView.setDrawCircle(true);
                return true;
            case R.id.oval:
                paintView.setDrawOval(true);
                return true;
            case R.id.roundRect:
                paintView.setDrawRoundedRectangle(true);
                return true;
            case R.id.dropper:
                paintView.setDropperActive(true);
                return true;
            case R.id.brushShape:
                paintView.changeStrokeShape();
                return true;
            case R.id.brushSize:
                createSeekDialog();
                return true;
            case R.id.save:
                save(paintView);
                return true;
            case R.id.load:
                paintView.clear();
                load();
                return true;
            case R.id.credits:
                openCredits();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
