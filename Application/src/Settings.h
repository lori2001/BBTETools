#pragma once
#include <string>
#include <fstream>
#include <filesystem>

class Settings {
public:
	Settings() {
		setToDefaults();
	}

	static void save();
	static void load();

	inline static std::filesystem::path inputPath;
	inline static std::filesystem::path outputPath;
	inline static std::string labString;
	inline static std::string idString;
	inline static std::string nameString;
	inline static std::string groupString;
	inline static bool hasNewFolder;
	inline static bool folderForEach;

private:
	static void setToDefaults();

	static const constexpr char* m_fileName = "settings.txt";
};