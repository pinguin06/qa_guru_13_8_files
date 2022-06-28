package guru.qa;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HomeWorkZIP {
    public static final String FILE = "src\\test\\resources\\output.zip";
    ClassLoader classLoader = FileParseTest.class.getClassLoader();


    @DisplayName("Запись в архив")
    @Test
    public void zip() throws Exception {
        //где искать файлы, которые нужно поместить в архив
        String parent = "src/test/resources/filesForZip";

        //создаем архив
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(FILE));

        //сохраняем список файлов
        File[] files = new File(parent, "").listFiles();
        for (File f : files) {

            //создаём entry для помещения в архив
            ZipEntry file = new ZipEntry(f.getName());

            //помещаем в архив
            zout.putNextEntry(file);

            //читаем байты из файла и помещаем в буфер
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);

            //записываем байты в архив
            zout.write(buffer);

            //закрываем поток для entry
            zout.closeEntry();
        }
        //закрываем поток для архива
        zout.close();
    }
}