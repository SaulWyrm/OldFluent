package com.quchen.flashcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListItem {

    private static final int LEFT_IDX = 0;
    private static final int RIGHT_IDX = 1;

    private final String filePath;

    public String getFilePath() {
        return filePath;
    }

    // Left - слово на иностранном языке, Right - перевод/мнемоника
    public static class ItemPair {
        public final String left;
        public final String right;

        public ItemPair(String left, String right) {
            this.left = left;
            this.right = right;
        }
    }

    private ItemPair header;
    private final List<ItemPair> itemPairs = new ArrayList<>();

    public ListItem(String filePath) {
        this.filePath = filePath;
        readFile();
    }

    public List<ItemPair> getItemPairs() {
        return itemPairs;
    }

    private static List<List<String>> getFileContent(File file) throws IOException {

        List<List<String>> fileContentList = new ArrayList<>();

        if(file.exists()) {

            InputStream inputStream = new FileInputStream(file);
            Reader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            String line;

            while ((line = br.readLine()) != null) {
                if(line.contains(";")) {
                    ArrayList<String> values = new ArrayList<>(Arrays.asList(line.split(";")));
                    fileContentList.add(values);
                }
            }

            br.close();
        }

        return fileContentList;
    }

    private void readFile()
    {
        File file = new File(App.getListRootDir(), filePath);
        List<List<String>> lines = null;

        try {
            lines = getFileContent(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // проверка строк на отстуствие Null, а также наличия содержания
        if(lines != null && lines.size() > 0)
        {
            // Хранение заголовка отдельно
            List<String> headerLine = lines.get(0);
            header = new ItemPair(headerLine.get(LEFT_IDX), headerLine.get(RIGHT_IDX));
            lines.remove(0);
            for(List<String> line: lines) {
                itemPairs.add(new ItemPair(line.get(LEFT_IDX).trim(), line.get(RIGHT_IDX).trim()));
            }
        }
    }

    public int getNumberOfItems() {
        return itemPairs.size();
    }

    public String getLeftHeader() {
        if (header != null)
        {
            return header.left;
        }
        else
        {
            return null;
        }
    }

    public String getRightHeader() {
        if(header != null)
        {
            return header.right;
        }
        else
        {
            return null;
        }
    }
}
