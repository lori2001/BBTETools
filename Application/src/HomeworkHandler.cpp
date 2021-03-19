#include "HomeworkHandler.h"

HomeworkHandler::HomeworkHandler(const std::filesystem::path& outputPath, const std::filesystem::path& inputPath)
{
	std::filesystem::path outputPathWE = outputPath;
	if (outputPathWE.wstring().back() != '\\') {
		outputPathWE += '\\';
	}

	std::filesystem::path inputPathWE = inputPath;

	if (inputPathWE.wstring().back() != '\\') {
		inputPathWE += '\\';
	}

	m_paths[0] = inputPathWE;

	m_outputPath = outputPathWE;
	m_outputPathWithNoEndings =
		outputPathWE.wstring().
		substr(0, outputPathWE.wstring().size() - 1);

	ReadFiles();
}

HomeworkHandler::~HomeworkHandler()
{
	m_paths.clear();
	m_renamableOriginal.clear();
	m_renamableNew.clear();
	m_unrenamableOriginal.clear();
	m_unrenamableNew.clear();
}

void HomeworkHandler::SaveFiles()
{
	CreateDirectoryW(LPCWSTR(m_outputPath.c_str()), NULL);
	
	if (Settings::folderForEach) {
		for (int i = 0; i < static_cast<int>(m_renamableNew.size()); i++) {
			auto pathName = m_outputPath.wstring() + getFileOrFolderName(m_renamableNew[i]);
			CreateDirectoryW(LPCWSTR(pathName.c_str()), NULL);

			auto newLoc = pathName + std::wstring(L"\\")
				+ getFileOrFolderName(m_renamableNew[i]) + getExtension(m_renamableNew[i]);

			copyFile(m_renamableOriginal[i], newLoc);
			// NG_LOG_INFO(m_renamableOriginal[i].u8string(), " - renamable file copied!");
		}

		for (int i = 0; i < static_cast<int>(m_unrenamableNew.size()); i++) {
			int fi = -1;

			for (int j = 0; j < static_cast<int>(m_renamableOriginal.size()); j++) {
				if (sameDirectory(m_renamableOriginal[j], m_unrenamableOriginal[i])) {
					fi = j;
					break;
				}
			}

			if (fi == -1) {
				copyFile(m_unrenamableOriginal[i], m_unrenamableNew[i]);

				NG_LOG_WARN(m_unrenamableOriginal[i].u8string(), " - File project not recognised!");
			}
			else {
				auto pathName = m_outputPath.wstring() + getFileOrFolderName(m_renamableNew[fi]);
				auto newLoc = pathName + std::wstring(L"\\") +
					getFileOrFolderName(m_unrenamableNew[i]) + getExtension(m_unrenamableNew[i]);

				// NG_LOG_INFO(m_unrenamableOriginal[i].u8string(), " - unrenamable file copied!");

				copyFile(m_unrenamableOriginal[i], newLoc, true);
			}
		}
	}
	else {
		for (int i = 0; i < static_cast<int>(m_renamableNew.size()); i++) {
			copyFile(m_renamableOriginal[i], m_renamableNew[i]);
			// NG_LOG_INFO(m_renamableOriginal[i].u8string(), " - renamable file copied!");
		}
		for (int i = 0; i < static_cast<int>(m_unrenamableNew.size()); i++) {
			copyFile(m_unrenamableOriginal[i], m_unrenamableNew[i], true);
			// NG_LOG_INFO(m_renamableOriginal[i].u8string(), " - unrenamable file copied!");
		}
	}
}

std::wstring HomeworkHandler::toWString(const std::string& String)
{
	std::wstring WString;
	// copy a/t
	WString.resize(String.size());
	for (int i = 0; i < String.size(); i++) {
		WString[i] += String[i];
	}

	return WString;
}

void HomeworkHandler::ReadFiles()
{
	try {
		std::filesystem::path path = m_paths.back(); // [m_paths.size() - 1]

		for (const auto& entry : std::filesystem::directory_iterator(path))
		{
			if (path != m_outputPathWithNoEndings) {
				m_paths.push_back(entry.path());
				ReadFiles();
			}
		}
	}
	catch (...) {
		// if can't open. assumes it's a file

		auto inFile = m_paths.back();
		bool hasExtension = false;
		auto extension = getExtension(inFile, &hasExtension);

		// if extension exists and is accepted
		if (hasExtension) {
			for (auto& it : sourceExtensions) {
				if (extension == it)
				{
					bool renameFailed = false;
					auto outfile = getOutFilePath(inFile, &renameFailed);

					if (renameFailed) {
						m_unrenamableOriginal.push_back(inFile);
						m_unrenamableNew.push_back(outfile);
					}
					else {
						m_renamableOriginal.push_back(inFile);
						m_renamableNew.push_back(outfile);
					}
				}
			}
			for (auto& it : inputExtensions) {
				if (extension == it) {
					auto outfile = getOutFilePath(inFile);
					m_unrenamableOriginal.push_back(inFile);
					m_unrenamableNew.push_back(outfile);
				}
			}
		}
	}
}

// input ex.: 2.cpp
// output: .../saam0334_L3_2.cpp
std::wstring HomeworkHandler::getOutFilePath(const std::filesystem::path& pathOfOldFile, bool* renameFailed)
{
	const std::wstring extension = getExtension(pathOfOldFile);
	const std::wstring oldfileName = getFileOrFolderName(pathOfOldFile);
	std::wstring newFileName;

	if (m_labString != L"") {
		try {
			stoi(oldfileName); // check if name of file starts with number (can be converted)

			newFileName = m_GENERIC_FILE_NAME;

			auto finder = newFileName.find(L"!");
			newFileName.replace(finder, 1, m_idString);
			finder = newFileName.find(L"!");
			newFileName.replace(finder, 1, m_labString);
			finder = newFileName.find(L"!");
			newFileName.replace(finder, 1, oldfileName);

			if (renameFailed != nullptr) {
				*renameFailed = false;
			}

			newFileName = std::wstring(m_outputPath) + newFileName + extension;
		}
		catch (...) {
			goto conversion2;
		}
	}
	else {
	conversion2:

		if (renameFailed != nullptr) {
			*renameFailed = true;
		}

		// NG_LOG_WARN("Can't convert filename of: ", std::to_string(oldfileName), " - Original name will be kept!");

		newFileName = std::wstring(m_outputPath) + oldfileName + extension;
	}

	return newFileName;
}

// input ex.: 2.cpp
// output(in-file) ex.: 
// /* Szõke András-Loránd
//    611-es csoport
//    2.Feladat */
// + input
void HomeworkHandler::copyFile(const std::filesystem::path& from, const std::filesystem::path& to, bool renameFailed)
{
	std::ifstream in(from);
	std::ofstream out(to);

	/// ------ Add generic comment --------------
	if (!renameFailed) {
		auto fromPos = from.u8string().find_last_of('\\'); // should find the homework's name in file's name
		auto untilPos = from.u8string().find_last_of('.');

		try {
			std::string homeworkName = from.u8string().substr(fromPos + 1, untilPos - fromPos - 1);

			auto comment = m_GENERIC_COMMENT;

			auto finder2 = comment.find('!');
			comment.replace(finder2, 1, m_nameString);

			finder2 = comment.find('!');
			comment.replace(finder2, 1, m_groupString);

			finder2 = comment.find('!');
			comment.replace(finder2, 1, homeworkName);

			out << comment;
		}
		catch (...) {
			NG_LOG_WARN("Failed to add generic comments to: ", to);
		}
	}
	/// ------ Add generic comment --------------

	// ---- Copy File ----
	std::string tmp;
	while (getline(in, tmp)) {
		out << tmp << "\n";
	}
	// --------------------

	in.close();
	out.close();
}

bool HomeworkHandler::sameDirectory(const std::filesystem::path& file1, const std::filesystem::path& file2)
{
	auto firstFinder = file1.wstring().find_last_of('\\');
	auto firstString = file1.wstring().substr(0, firstFinder);

	auto secondFinder = file2.wstring().find_last_of('\\');
	auto secondString = file2.wstring().substr(0, secondFinder);

	return firstString == secondString;
}

std::wstring HomeworkHandler::getFileOrFolderName(const std::filesystem::path& file, bool* isFolder)
{
	auto nameStart = file.wstring().find_last_of('\\');
	auto nameEnd = file.wstring().find_last_of('.');

	if (isFolder != nullptr && (nameEnd == std::string::npos || nameEnd < nameStart)) {
		*isFolder = true;
	}
	else if (isFolder != nullptr) {
		*isFolder = false;
	}

	return file.wstring().substr(nameStart + 1, nameEnd - nameStart - 1);
}

std::wstring HomeworkHandler::getExtension(const std::filesystem::path& file, bool* hasExtension)
{
	std::size_t startingIndex = file.wstring().find_last_of('.');
	if (hasExtension != nullptr) {
		if (startingIndex == std::string::npos) {
			*hasExtension = false;
		}
		else {
			*hasExtension = true;
		}
	}
	
	return file.wstring().substr(startingIndex); // (ex: ".cpp")
}

