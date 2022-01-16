import clsPresets.ClsPreset;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

public class ProcessDir {
    // TODO Multi thread this
    public ProcessDir(Path inputPath, Path outputPath, ClsPreset clsPreset) {
        assert clsPreset != null;

        HashMap<Path, Path> processedFilePaths = new HashMap<>();
        HashMap<Path, Path> directoryChanges = new HashMap<>();

        ArrayList<Path> unprocessedFiles = new ArrayList<Path>();

        try {
            Files.walkFileTree(inputPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path inFilePath, BasicFileAttributes attrs) {
                    // if extension is validated by clsPreset
                    if(clsPreset.extensionIsValid(inFilePath.toString())) {
                        Path outFilePath = getOutFilePath(clsPreset, inFilePath, outputPath);

                        // TODO Switch to "#include" - based folders
                        // separate files into different folders
                        if(clsPreset.folderForEachFile()) {
                            boolean wasRenamed = !outFilePath.getFileName().equals(inFilePath.getFileName());
                            // create directory
                            if(wasRenamed) {
                                String newDirLoc = outputPath.toString() + "\\" + removeExtension(outFilePath.getFileName().toString());
                                File newDir = new File(newDirLoc);

                                if (!newDir.exists() && !newDir.mkdirs()) { // creates the directory if it does not exist
                                    System.out.println("HIBÁS mappa készítés: " + newDirLoc); // if error
                                }

                                // save directory output changes for this parent directory
                                if(directoryChanges.containsKey(inFilePath.getParent())) {
                                    System.out.println("HIBA: Két feladat file talált a" + inFilePath.getParent()
                                    + " mappában! Az app (még) nem tudja ezt feldolgozni. Kérlek válaszd külön folderbe az egyes feladatokat.");
                                    System.out.println("Ez az egyik fõ file: " + inFilePath);
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

                        processFile(clsPreset, inFilePath, outFilePath);
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
                    Path outFilePath = getOutFilePath(clsPreset, inFilePath, outputPath);
                    // copy element in proper folder
                    Path newDir = directoryChanges.get(inFilePath.getParent());
                    if(newDir != null) { // if parent directory was found
                        outFilePath = Paths.get(newDir + "\\" + outFilePath.getFileName().toString());
                    } else {
                        System.out.print("HIBA: nem lehet feldolgozni " + inFilePath + "mivel vagy nem része egy foldernek, ");
                        System.out.println("vagy a folder amelynek része nem tartalmaz olyan file-t ami egy feladatot jelöl!");
                    }
                    processFile(clsPreset, inFilePath, outFilePath);
                    checkForOverwriteError(processedFilePaths, inFilePath, outFilePath);
                }
            );
        }
    }

    private String removeExtension(String str) {
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }

    private Path getOutFilePath(ClsPreset clsPreset, Path filePath, Path outputPath) {
        Path outputFile = null;

        String origName = filePath.getFileName().toString();
        String newFileName = clsPreset.getNewFileName(origName);

        // write file with name according to class preset
        outputFile = Paths.get(outputPath + "\\" + newFileName);

        return outputFile;
    }

    private void processFile(ClsPreset clsPreset, Path inFilePath, Path outFilePath) {
        try {
            // create a reader
            FileInputStream inFile = new FileInputStream(inFilePath.toFile());

            // read one byte at a time and add it to fileContent string
            StringBuilder fileContent = new StringBuilder();
            int ch;
            while ((ch = inFile.read()) != -1) {
                fileContent.append((char) ch);
            }

            final String origName = inFilePath.getFileName().toString();

            // write file with name according to class preset
            FileWriter writer = new FileWriter(outFilePath.toString());

            // process fileContent with class specific instructions
            clsPreset.processContent(fileContent.toString(), origName, writer);

            // write content of old file to new file
            writer.write(fileContent.toString());

            inFile.close();
            writer.close();
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
            System.out.println("HIBA: FELÜlÍRÁS! a " + outFilePath + " fileon!");
            System.out.println("Felülírt file származása: " + processedFilePaths.get(outFilePath));
            System.out.println("Az új file származása: " + inFilePath);
        }
        processedFilePaths.put(outFilePath, inFilePath);
    }
}
