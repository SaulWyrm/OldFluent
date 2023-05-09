package com.quchen.flashcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Адаптер языков (групп со списками внутри)
    private static class FolderAdapter extends ArrayAdapter<String> {

        public FolderAdapter(Context context) {
            super(context, 0);
        }

        // Конвертация разметки рядовой папки во view Обьект
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String item = this.getItem(position);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.folder_item_view, parent, false);
            }

            TextView label = convertView.findViewById(R.id.label);
            label.setText(item);

            return convertView;
        }
    }

    private FolderAdapter folderAdapter;

    // Получить все папки из корневой директории
    private List<String> getFolders() {
        List<String> folders = new ArrayList<>();

        File listRoodDir = App.getListRootDir();

        for(File listFolder: listRoodDir.listFiles()) {
            folders.add(listFolder.getName());
        }

        Collections.sort(folders);

        return folders;
    }

    // Диалог создания новой папки
    private void showFolderCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        alert.setTitle(R.string.createFolderDialogTitle);
        alert.setMessage(R.string.createFolderDialogMessage);

        alert.setView(edittext);

        alert.setPositiveButton(R.string.createFolderYesOption, (dialog, id) -> {
            String folderName = edittext.getText().toString();
            File folder = new File(App.getListRootDir(), folderName);
            if(folder.mkdir()) {
                folderAdapter.add(folderName);
            } else {
                Toast.makeText(MainActivity.this, R.string.folderCreateError, Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton(R.string.createFolderNoOption, null);

        alert.show();
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English", "German", "Russian"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(R.string.chooseLang);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("en");
                    recreate();
                }
                else if (i == 1) {
                    setLocale("de");
                    recreate();
                }
                else if (i == 2) {
                    setLocale("ru");
                    recreate();
                }

                // выход из диалога после выбора языка
                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        // сохраняем данные в общие предпочтения
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    // Подгрузить язык из общих предпочтений
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    public void addFolderOnClick(View view) {
        showFolderCreateDialog();
    }

    public void goToSettingsOnClick(View view) {
        showChangeLanguageDialog();
    }

    // Подгрузка групп слов при нажатии соответствующей папки
    private final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String folder = folderAdapter.getItem(position);

            Intent intent = new Intent("com.quchen.flashcard.ListActivity");
            intent.putExtra(ListActivity.KEY_FOLDER, folder);
            startActivity(intent);
        }
    };

    // Импрорт слов из csv в отдельную папку
    private void copyFileFromResource(int id, String folderName, String fileName) {
        InputStream in = getResources().openRawResource(id);
        File folder = new File(App.getListRootDir(), folderName);
        folder.mkdirs();
        File file = new File(folder, fileName);

        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while((len = in.read(buffer, 0, buffer.length)) != -1){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    // Импорт базовых групп слов
    private void updateListFiles() {

        // Indo-European family languages
        copyFileFromResource(R.raw.oldnorse_vocabulary_en, "Old Norse","Old Norse vocabulary\nENG");
        copyFileFromResource(R.raw.oldnorse_phrases_eng, "Old Norse","Old Norse phrases\nENG");
        copyFileFromResource(R.raw.oldnorse_eng_poeticedda_voluspa, "Old Norse","Old Norse Voluspa\nENG");
        copyFileFromResource(R.raw.oldnorse_eng_sayings, "Old Norse","Old Norse sayings\nENG");

        // Niger-Congo family languages
        copyFileFromResource(R.raw.lingala_vocabulary_eng, "Lingala","Lingala vocabulary\nEng");
        copyFileFromResource(R.raw.lingala_phrases_eng, "Lingala","Lingala phrases\nEng");
        copyFileFromResource(R.raw.lingala_eng_dialogue, "Lingala","Lingala dialogue\nEng");
        copyFileFromResource(R.raw.lingala_eng_numbers, "Lingala","Lingala numbers\nEng");
        copyFileFromResource(R.raw.lingala_eng_dialogue2, "Lingala","Lingala dialogue 2\nEng");
        copyFileFromResource(R.raw.lingala_eng_weekdays, "Lingala","Lingala weekdays\nEng");
        copyFileFromResource(R.raw.lingala_eng_weekdays_transcript, "Lingala","Lingala weekdays transcript\nEng");

        // American family languages
        copyFileFromResource(R.raw.miwok_eng_vocabulary, "Miwok","Miwok Numbers\nEng");

    }

    // Долгое зажатие папки откроет диалог, и предложит удалить ее
    private final AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            final String folder = folderAdapter.getItem(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(String.format("%s %s", getResources().getString(R.string.deleteFolder), folder))
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        if(App.deleteDirectory(new File(App.getListRootDir(), folder))) {
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), null)
                    .show();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);



        updateListFiles();

        folderAdapter = new FolderAdapter(this);
        folderAdapter.addAll(getFolders());

        ListView listView = findViewById(R.id.folderList);
        listView.setAdapter(folderAdapter);

        listView.setOnItemClickListener(clickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);

        
    }
}
