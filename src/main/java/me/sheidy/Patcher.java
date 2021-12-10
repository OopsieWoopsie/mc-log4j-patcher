package me.sheidy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Patcher {

    private static final String PLUGINS_FILE = "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat";

    private static boolean hasPlugins = false;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Use: java -jar patcher.jar <input.jar> <output.jar>");
            return;
        }
        String inputName = args[0];
        String outputName = args[1];

        if (!inputName.endsWith(".jar")) {
            System.out.println("Input file needs to be a Jar file");
            return;
        }
        if (!outputName.endsWith(".jar")) {
            outputName = outputName + ".jar";
        }

        File inFile = new File(inputName);
        if (!inFile.exists()) {
            System.out.println("Input file: " + inputName + " does not exists");
            return;
        }
        File outFile = new File(outputName);

        System.out.println("Input file  : " + inputName);
        System.out.println("Output file : " + outputName);

        try (
                ZipFile jarFile = new ZipFile(inFile);
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outFile))
        ) {

            zos.setLevel(9); // maximum compression

            Enumeration<? extends ZipEntry> entries = jarFile.entries();

            System.out.println("Processing original Jar file...");
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                String entryName = entry.getName();

                //#################################################
                if (entryName.startsWith("Log4j-")) {
                    continue;
                }
                if (entryName.startsWith("org/apache/logging/log4j")) {
                    continue;
                }
                if (entryName.startsWith("META-INF/versions/9/org/apache/logging/log4j/")) {
                    continue;
                }
                if (entryName.startsWith("META-INF/services/org.apache.logging.log4j.")) {
                    continue;
                }
                if (entryName.equals(PLUGINS_FILE)) {
                    hasPlugins = true;
                }
                //#################################################

                byte[] entryBytes = readAllBytes(jarFile.getInputStream(entry));

                if (entryName.equals("log4j2.xml")) {
                    System.out.print("Adding nolookups to log4j2.xml ... ");
                    entryBytes = addNolookups(entryBytes);
                    System.out.println("Done");
                }
                entry.setSize(entryBytes.length);
                entry.setCompressedSize(-1);

                zos.putNextEntry(entry);
                zos.write(entryBytes);
                zos.closeEntry();
            }

            System.out.println("Adding Log4j2 2.15.0 (core+api+iostreams)... ");
            injectNewLog4J(zos);
        }

        System.out.println("Finished");
    }

    private static void injectNewLog4J(ZipOutputStream zos) throws IOException {
        ZipInputStream zis = new ZipInputStream(Patcher.class.getResourceAsStream("/log4j-2.15.0-all.jar"));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();

            if (hasPlugins && entryName.equals(PLUGINS_FILE)) {
                continue;
            }

            try {
                zos.putNextEntry(entry);
                zos.write(readAllBytes(zis));
                zos.closeEntry();
            } catch (ZipException e) {
                if (!entryName.endsWith("/")) {
                    System.err.println("(You can likely ignore this) : " + e.getMessage());
                }
            }
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int readCount;
        while ((readCount = inputStream.read(buf)) > 0) {
            baos.write(buf, 0, readCount);
        }
        return baos.toByteArray();
    }

    private static byte[] addNolookups(byte[] entryBytes) throws IOException {
        String content = new String(entryBytes, "UTF-8");
        content = content.replace("%msg", "%msg{nolookups}");
        return content.getBytes("UTF-8");
    }
}
