package com.quchen.flashcard;

import android.app.Application;
import android.content.Context;
import com.google.android.material.color.DynamicColors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class App extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    // Получить корневую папку приложения
    public static File getListRootDir() {
        File listDirectory = new File(context.getFilesDir(), "lists");

        File oldDir = new File(context.getExternalCacheDir(), "lists");

        // Если существует старая папка с данными
        if(oldDir.exists()) {
            try {
                copyDirectoryOneLocationToAnotherLocation(oldDir, listDirectory);
                deleteDirectory(oldDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return listDirectory;
    }

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }


    public static boolean deleteDirectory(File file) {
        // First delete all content recursively
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if(!deleteDirectory(f)) {
                    return false;
                }
            }
        }

        return file.delete();
    }

    /* https://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd */
    // Копировать содержимое кэша в актуальную папку
    private static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {
        // Создаем папку, если ее не существует
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            // Иначе просто копируем содержимое кеша в папку
            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }


}
