package HomeworkGatherer;

import HomeworkGatherer.clsPresets.ClsPreset;
import Common.logging.LogPanel;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static HomeworkGatherer.utils.FilenameUtils.removeExtension;

public class DirectoryProcessor {

    // TODO Multi thread this
    public boolean processDir(Path inputDir, Path outputDir, ClsPreset clsPreset) {
        assert clsPreset != null;

        if(!Files.isDirectory(inputDir)) {
            LogPanel.logln("HIBA: A bemeneti folder nem létezik! (" + inputDir + ")");
            return false;
        }
        if(!Files.isDirectory(outputDir)) {
            LogPanel.logln("HIBA: A kimeneti folder nem létezik! (" + outputDir + ")");
            return false;
        }

        final ZipOutputStream zipOut = getZipObject(clsPreset, outputDir);

        HashMap<Path, Path> processedFilePaths = new HashMap<>(); // for "overwrite"error messages
        HashMap<Path, Path> directoryChanges = new HashMap<>(); // for "folderForEach" option

        ArrayList<Path> unprocessedFiles = new ArrayList<>(); // contains unprocessed "folderForEach" files because alphabetical order

        try {
            Files.walkFileTree(inputDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path inFilePath, BasicFileAttributes attrs) throws IOException {
                    // if extension is validated by clsPreset
                    if(clsPreset.extensionIsValid(inFilePath.toString())) {
                        Path outFilePath = getOutFilePath(clsPreset, inFilePath, outputDir);

                        // TODO Switch to "#include" - based folders
                        // separate files into different folders
                        if(clsPreset.folderForEachFile()) {
                            boolean wasRenamed = !outFilePath.getFileName().equals(inFilePath.getFileName()); // is a "main" file
                            // create directory
                            if(wasRenamed) {
                                String newDirLoc = outputDir + "\\" + removeExtension(outFilePath.getFileName().toString());
                                File newDir = new File(newDirLoc);

                                // create zip directories explicitly!
                                // this part is not necessary but is good practice
                                if(zipOut != null) {
                                    // ZipEntry --- Here file name can be created using the source file
                                    ZipEntry ze = new ZipEntry(newDir.getName() +"\\");
                                    // Putting zipEntry in zipoutputstream
                                    zipOut.putNextEntry(ze);
                                    zipOut.closeEntry();
                                }
                                else if (!newDir.exists() && !newDir.mkdirs()) { // creates the directory if it does not exist
                                    LogPanel.logln("HIBA: mappa készítés közben: " + newDirLoc); // if error
                                }

                                // save directory output changes for this parent directory
                                if(directoryChanges.containsKey(inFilePath.getParent())) {
                                    LogPanel.logln("HIBA: Két feladat file talált a" + inFilePath.getParent()
                                    + " mappában! Az app (még) nem tudja ezt feldolgozni. Kérlek válaszd külön folderbe az egyes feladatokat.");
                                    LogPanel.logln("Ez az egyik feladat file: " + inFilePath);
                                } else {
                                    directoryChanges.put(inFilePath.getParent(), Paths.get(newDirLoc));
                                }
                            }

                            // copy element in proper folder
                            Path newDir = directoryChanges.get(inFilePath.getParent());
                            if(newDir != null) { // if parent directory was found
                                outFilePath = Paths.get(newDir + "\\" + outFilePath.getFileName().toString());
                            } else {
                                unprocessedFiles.add(inFilePath); // mark for post-processing
                                return FileVisitResult.CONTINUE;
                            }
                        }

                        processFile(clsPreset, inFilePath, outFilePath, zipOut, outputDir);
                        checkForOverwriteError(processedFilePaths, inFilePath, outFilePath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    LogPanel.logln("HIBA: Nem lehet feldolgozni: " + file.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException err) {
            LogPanel.logln("HIBA: A folderrendszer bejárásakor " + Arrays.toString(err.getStackTrace()));
            return false; // breaking error
        }

        // reprocess files that happened to be proccessed before the main folder
        // in a folder for each system
        if(clsPreset.folderForEachFile()) {
            unprocessedFiles.forEach(
                inFilePath -> {
                    Path outFilePath = getOutFilePath(clsPreset, inFilePath, outputDir);
                    // copy element in proper folder
                    Path newDir = directoryChanges.get(inFilePath.getParent());
                    if(newDir != null) { // if parent directory was found
                        outFilePath = Paths.get(newDir + "\\" + outFilePath.getFileName().toString());
                    } else {
                        LogPanel.log("HIBA: nem lehet feldolgozni " + inFilePath + "mivel vagy nem része egy foldernek, ");
                        LogPanel.logln("vagy a folder amelynek része nem tartalmaz olyan file-t ami egy feladatot jelöl!");
                    }

                    processFile(clsPreset, inFilePath, outFilePath, zipOut, outputDir);
                    checkForOverwriteError(processedFilePaths, inFilePath, outFilePath);
                }
            );
        }

        try{
            if(zipOut != null) zipOut.close();
        }catch (IOException e) {
            LogPanel.logln("HIBA: Sikertelen .zip file bezárás!");
            return false; // breaking error
        }

        return true;
    }

    private Path getOutFilePath(ClsPreset clsPreset, Path filePath, Path outputPath) {
        String newFileName = clsPreset.getNewFileName(filePath);

        return Paths.get(outputPath + "\\" + newFileName); // write file with name according to class preset
    }

    private void processFile(ClsPreset clsPreset, Path inFilePath, Path outFilePath, ZipOutputStream zipOut, Path outDir) {
        try {
            // create a reader
            FileInputStream inFile = new FileInputStream(inFilePath.toFile());

            final String origName = inFilePath.getFileName().toString();

            // read one byte at a time and add it to fileContent string
            StringBuilder inFileContent = new StringBuilder();
            int ch;
            while ((ch = inFile.read()) != -1) {
                inFileContent.append((char) ch);
            }
            // process fileContent with class specific instructions
            String outFileContent = clsPreset.processContent(inFileContent.toString(), origName);

            // zipping output
            if(zipOut != null) {
                // find relative path from abolute path
                String ofp = outFilePath.toString();
                String odir = outDir.toString();
                String outLoc = ofp.substring(ofp.indexOf(odir) + odir.length() + 1);

                ZipEntry ze = new ZipEntry(outLoc);
                zipOut.putNextEntry(ze);

                ByteArrayInputStream bais = new ByteArrayInputStream(outFileContent.getBytes());

                byte[] bytes = new byte[1024];
                int length;
                while((length = bais.read(bytes)) >= 0 ) {
                    zipOut.write(bytes, 0, length);
                }

                zipOut.closeEntry(); // for zip output

            } else { // normal output
                // write file with name according to class preset
                FileWriter fileWriter = new FileWriter(outFilePath.toString());

                // write content of old file to new file
                fileWriter.write(outFileContent);

                fileWriter.close();
            }

            inFile.close();
        }
        catch (FileNotFoundException e) {
            LogPanel.log("HIBA: SIKERTELEN FILEÍRÁS! A \"" + outFilePath + "\" file nem található!");
        }
        catch (IOException e){
            LogPanel.log("HIBA: IO hiba történt." + Arrays.toString(e.getStackTrace()));
        }
    }

    // check for "internal" file rewrites and prompt errors to screen
    private void checkForOverwriteError(HashMap<Path, Path> processedFilePaths, Path inFilePath, Path outFilePath) {
        if(processedFilePaths.containsKey(outFilePath)) {
            LogPanel.logln("HIBA: FELÜlÍRÁS! a " + outFilePath + " fileon!" +
                    "\nFelülírt file származása: " + processedFilePaths.get(outFilePath) +
                    "\nAz új file származása: " + inFilePath);
        }
        processedFilePaths.put(outFilePath, inFilePath);
    }

    // return the object in which zipping has to be done
    private ZipOutputStream getZipObject(ClsPreset clsPreset, Path outputDir) {
        if(clsPreset.getParentZipName() != null) {
            String path = outputDir + "\\" + clsPreset.getParentZipName() + ".zip";
            try{
                return new ZipOutputStream(new FileOutputStream(path));
            } catch (FileNotFoundException e){
                LogPanel.logln("HIBA: Nem sikerül létrehozni a .zip file-t: " + path);
            }
        }
        return null;
    }
}
