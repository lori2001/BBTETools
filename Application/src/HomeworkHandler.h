#pragma once
#include "NGin.h"

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

	void setLabString(const std::string labString);

private:
	void ReadFiles(); // reads all files in all folders recursively

	std::wstring getOutFilePath(const std::filesystem::path& pathOfOldFile);
	void copyFile(const std::filesystem::path& from, const std::filesystem::path& to);

	// !-marks to be replaced
	const std::wstring m_GENERIC_FILE_NAME = L"saam0334_L!_!.cpp";
	const std::string m_GENERIC_COMMENT = "/* Szõke András-Loránd\n   611-es csoport\n   !.Feladat */\n\n";

	bool m_renameFailed = false;
	std::wstring m_labString = L"";

	std::filesystem::path m_outputPathWithNoEndings;
	std::filesystem::path m_outputPath;

	std::vector<std::filesystem::path> m_paths = { "" }; // hold all found files and folders (starts from element 0)
	std::vector<std::filesystem::path> m_files; // holds all found files
};
