#pragma once
#include <string>
#include <fstream>
#include <filesystem>

class Settings {
public:
	static void save();
	static void load();

	inline static std::filesystem::path inputPath;
	inline static std::filesystem::path outputPath;
	inline static bool hasNewFolder;
	inline static std::string labString;

private:
	static void setToDefaults();

	static const constexpr char* m_fileName = "settings.txt";
};