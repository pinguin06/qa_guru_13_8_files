package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.monitor.FileEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class HomeWork {
    public static final String FILE = "src\\test\\resources\\output.zip";
    ClassLoader classLoader = FileParseTest.class.getClassLoader();


    @DisplayName("Запись в архив")
    @Test
    public static void main(String[] args) throws IOException {
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

    public void unZIPCheck() throws IOException, CsvException {
        String parent = "src/test/resources/unZIP";
        InputStream is = classLoader.getResourceAsStream("output.zip");
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            System.out.println(entry.getName());
            FileOutputStream fout = new FileOutputStream("src/test/resources/unZIP/" + entry.getName());
            //распаковка файлов в папку
            for (int c = zis.read(); c != -1; c = zis.read()) {
                fout.write(c);
            }
            fout.flush();
            zis.closeEntry();
            fout.close();
        }
        //проверка файлов
        File[] unFiles = new File(parent, "").listFiles();
        for (File f : unFiles) {
            FileEntry files = new FileEntry(new File(f.getName()));
            if (files.getName().contains(".csv")) {
                CSVReader csvReader = new CSVReader(new FileReader(f, UTF_8));
                List<String[]> csv = csvReader.readAll();
                assertThat(csv).contains(
                        new String[]{"name", "gender"},
                        new String[]{"Olga", "female"},
                        new String[]{"Artem", "male"});
                System.out.println("CSV OK");
            } else {
                if (files.getName().contains(".xlsx")) {
                    XLS xls = new XLS(f);
                    assertThat(
                            xls.excel.getSheetName(0));
                    System.out.println("XLSX OK");
                } else {
                    if (files.getName().contains(".pdf")) {
                        PDF pdf = new PDF(f);
                        assertThat(pdf.text).contains("e-ticket #193091612465");
                        System.out.println("PDF OK");
                    }
                }
            }
        }
    }
}