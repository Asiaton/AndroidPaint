package tiko.tamk.fi.androidpaint;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView;
    public final static String APP_PATH_SD_CARD = "/Paint05/";

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          String permissions[],
                                          int[] grantResults) {
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
                return true;
            case R.id.color:
                createColorPicker().show();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
            case R.id.save:
                save(paintView);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void save(PaintView paintView) {

        Bitmap image = Bitmap.createBitmap(paintView.getWidth(),
                paintView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(image);
        paintView.draw(c);


        String fullPath =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + APP_PATH_SD_CARD;

        System.out.println(fullPath);
        try {
            File dir = new File(fullPath);
            System.out.println(dir.exists());
            if (!dir.exists()) {
                // TODO find out how to find phone gallery path and add here
                System.out.println(dir.mkdirs());
            }

            OutputStream fOut = null;
            Log.d("File", "1");
            File file = new File(fullPath, createUniqueFileName() + ".png");

            Log.d("File", "2");
            System.out.println(file.getAbsolutePath());
            System.out.println(file.createNewFile());

            Log.d("File", "3");
            fOut = new FileOutputStream(file);
            Log.d("File", file.getAbsolutePath());

            // 100 means no compression, the lower you go,
            // the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), file.getName());

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            e.printStackTrace();
        }
    }

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

    public AmbilWarnaDialog createColorPicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this,
                paintView.getCurrentColor(),
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.
                        paintView.setCurrentColor(color);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
        return dialog;
    }
}
