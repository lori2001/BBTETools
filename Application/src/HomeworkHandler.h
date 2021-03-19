#pragma once
#include "NGin.h"
#include "Settings.h"

#include <fstream>
#include <string>
#include <filesystem>
#include <windows.h>

class HomeworkHandler
{
public:
	HomeworkHandler(const std::filesystem::path& outputPath,
		const std::filesystem::path& inputPath = ".\\");

	~HomeworkHandler();

	void SaveFiles();
private:
	std::wstring toWString(const std::string& String);

	void ReadFiles(); // reads all files in all folders recursively

	std::wstring getOutFilePath(const std::filesystem::path& pathOfOldFile, bool* renameFailed = nullptr);
	void copyFile(const std::filesystem::path& from, const std::filesystem::path& to, bool renameFailed = false);

	bool sameDirectory(const std::filesystem::path& file1, const std::filesystem::path& file2);

	// returns std::string npos if failed
	// returns starting pos of extension if succesful (ex: .cpp)
	std::wstring getExtension(const std::filesystem::path& file, bool *hasExtension = nullptr);

	// returns the name of the file or folder
	std::wstring getFileOrFolderName(const std::filesystem::path& file, bool* isFolder = nullptr);

	// !-marks to be replaced
	const std::wstring m_GENERIC_FILE_NAME = L"!_L!_!";
	std::wstring m_labString = toWString(Settings::labString);
	std::wstring m_idString = toWString(Settings::idString);

	const std::string m_GENERIC_COMMENT = "/* !\n   !-es csoport\n   !.Feladat */\n\n";
	std::string m_nameString = Settings::nameString;
	std::string m_groupString = Settings::groupString;

	std::filesystem::path m_outputPathWithNoEndings;
	std::filesystem::path m_outputPath;

	const std::vector<std::wstring> sourceExtensions = { L".cpp", L".h", L".c", L".hpp" };
	const std::vector<std::wstring> inputExtensions = { L".in", L".txt" };

	std::vector<std::filesystem::path> m_paths = { "" }; // hold all found files and folders (starts from element 0)

	std::vector<std::filesystem::path> m_renamableOriginal; // holds files' original path that can be automatically renamed
	std::vector<std::filesystem::path> m_renamableNew; // holds files new path that can be automatically renamed

	std::vector<std::filesystem::path> m_unrenamableOriginal; // holds files' original path that can be automatically renamed
	std::vector<std::filesystem::path> m_unrenamableNew; // holds files  new path that cannot be automatically renamed
};
