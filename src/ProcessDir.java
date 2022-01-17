import clsPresets.ClsPreset;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static utils.FilenameUtils.removeExtension;

public class ProcessDir {
    // TODO Multi thread this
    public ProcessDir(Path inputDir, Path outputDir, ClsPreset clsPreset) {
        assert clsPreset != null;

        final ZipOutputStream zipOut = getZipObject(clsPreset, outputDir);

        HashMap<Path, Path> processedFilePaths = new HashMap<>(); // for "overwrite"error messages
        HashMap<Path, Path> directoryChanges = new HashMap<>(); // for "folderForEach" option

        ArrayList<Path> unprocessedFiles = new ArrayList<Path>(); // contains unprocessed "folderForEach" files because alphabetical order

        try {
            Files.walkFileTree(inputDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path inFilePath, BasicFileAttributes attrs) throws IOException {
                    // if extension is validated by clsPreset
                    if(clsPreset.extensionIsValid(inFilePath.toString())) {
                        Path outFilePath = getOutFilePath(clsPreset, inFilePath, outputDir);

                        // TODO Switch to "#include" - based folders
                        // separate files into different folders
                        if(clsPreset.folderForEachFile()) {
                            boolean wasRenamed = !outFilePath.getFileName().equals(inFilePath.getFileName());
                            // create directory
                            if(wasRenamed) {
                                String newDirLoc = outputDir.toString() + "\\" + removeExtension(outFilePath.getFileName().toString());
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
                                    System.out.println("HIBÁS mappa készítés: " + newDirLoc); // if error
                                }

                                // save directory output changes for this parent directory
                                if(directoryChanges.containsKey(inFilePath.getParent())) {
                                    System.out.println("HIBA: Két feladat file talált a" + inFilePath.getParent()
                                    + " mappában! Az app (még) nem tudja ezt feldolgozni. Kérlek válaszd külön folderbe az egyes feladatokat.");
                                    System.out.println("Ez az egyik fő file: " + inFilePath);
                                } else {
                                    directoryChanges.put(inFilePath.getParent(), Paths.get(newDirLoc));
                                }
                            }

                            // copy element in proper folder
                            Path newDir = directoryChanges.get(inFilePath.getParent());
                            if(newDir != null) { // if parent directory was found
                                outFilePath = Paths.get(newDir + "\\" + outFilePath.getFileName().toString());
                            } else {
                                unprocessedFiles.add(inFilePath); // mark for reprocessing
                            }
                        }

                        processFile(clsPreset, inFilePath, outFilePath, zipOut, outputDir);
                        checkForOverwriteError(processedFilePaths, inFilePath, outFilePath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    System.out.println("HIBA: Nem lehet feldolgozni: " + file.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException err) {
            err.printStackTrace();
        }

        // reprocess files that happened to be proccessed before the main folder
        // in a folder for each system
        if(clsPreset.folderForEachFile()) {
            unprocessedFiles.forEach(
                inFilePath -> {
                    System.out.println(inFilePath);
                    Path outFilePath = getOutFilePath(clsPreset, inFilePath, outputDir);
                    // copy element in proper folder
                    Path newDir = directoryChanges.get(inFilePath.getParent());
                    if(newDir != null) { // if parent directory was found
                        outFilePath = Paths.get(newDir + "\\" + outFilePath.getFileName().toString());
                    } else {
                        System.out.print("HIBA: nem lehet feldolgozni " + inFilePath + "mivel vagy nem része egy foldernek, ");
                        System.out.println("vagy a folder amelynek része nem tartalmaz olyan file-t ami egy feladatot jelöl!");
                    }
                    processFile(clsPreset, inFilePath, outFilePath, zipOut, outputDir);
                    checkForOverwriteError(processedFilePaths, inFilePath, outFilePath);
                }
            );
        }

        try{
            if(zipOut != null) zipOut.close();
        }catch (IOException e) {
            System.out.println("HIBA: Sikertelen .zip file bezárás!");
        }
    }

    private Path getOutFilePath(ClsPreset clsPreset, Path filePath, Path outputPath) {
        Path outputFile = null;

        String newFileName = clsPreset.getNewFileName(filePath);

        // write file with name according to class preset
        outputFile = Paths.get(outputPath + "\\" + newFileName);

        return outputFile;
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
                fileWriter.write(outFileContent.toString());

                fileWriter.close();
            }

            inFile.close();
        }
        catch (FileNotFoundException e) {
            System.out.print("SIKERTELEN FILEÍRÁS: A \"" + outFilePath + "\" folder nem létezik a gépen!");
            System.out.println(" Kérlek változtasd meg a kimeneti foldert.");
        }
        catch (IOException e){
            System.out.println("Hiba történt.");
            e.printStackTrace();
        }
    }

    // check for "internal" file rewrites and prompt errors to screen
    private void checkForOverwriteError(HashMap<Path, Path> processedFilePaths, Path inFilePath, Path outFilePath) {
        if(processedFilePaths.containsKey(outFilePath)) {
            System.out.println("HIBA: FELÜlÍRÁS! a " + outFilePath + " fileon!" +
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
                System.out.println("Nem sikerül létrehozni a .zip file-t: " + path);
                e.printStackTrace();
            }
        }
        return null;
    }
}
