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
	m_files.clear();
}

void HomeworkHandler::SaveFiles()
{
	CreateDirectoryW(LPCWSTR(m_outputPath.c_str()), NULL);

	for (int i = 0; i < static_cast<int>(m_files.size()); i++)
	{
		auto extension = m_files[i].wstring().find_last_of('.'); // .blahblah

		// if found and is .cpp
		if (extension != std::string::npos && m_files[i].wstring().substr(extension) == L".cpp")
		{
			auto outfile = getOutFilePath(m_files[i]); // process file's name and location
			copyFile(m_files[i], outfile);
		}
	}
}
void HomeworkHandler::setLabString(const std::string labString)
{
	// copy a/t
	m_labString.resize(labString.size());
	for (int i = 0; i < labString.size(); i++) {
		m_labString[i] += labString[i];
	}
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
		m_files.push_back(m_paths.back()); //.u8string()
	}
}

// input ex.: 2.cpp
// output: saam0334_L3_2.cpp
std::wstring HomeworkHandler::getOutFilePath(const std::filesystem::path& pathOfOldFile)
{
	auto fileNameStart = pathOfOldFile.wstring().find_last_of('\\');
	auto fileNameEnd = pathOfOldFile.wstring().find_last_of('.');

	if (m_labString != L"") {
		try {
			// check if name of file can be converted to number
			int homeworkNum =
				stoi(pathOfOldFile.wstring().
					substr(fileNameStart + 1, fileNameEnd - fileNameStart - 1));

			std::wstring newFileName = m_GENERIC_FILE_NAME;

			auto finder = newFileName.find(L"!");
			newFileName.replace(finder, 1, m_labString);

			finder = newFileName.find(L"!");
			newFileName.replace(finder, 1, std::to_wstring(homeworkNum));

			m_renameFailed = false;
			return std::wstring(m_outputPath) + newFileName;
		}
		catch (...) {
			goto conversion2;
		}
	}
	else {
	conversion2:

		m_renameFailed = true;

		NG_LOG_WARN("Can't convert filename of: ", 
			pathOfOldFile.u8string().substr(fileNameStart),
			" - Original name will be kept!");

		return std::wstring(m_outputPath) + pathOfOldFile.wstring().substr(fileNameStart);
	}
}

// input ex.: 2.cpp
// output(in-file) ex.: 
// /* Szõke András-Loránd
//    611-es csoport
//    2.Feladat */
// + input
void HomeworkHandler::copyFile(const std::filesystem::path& from, const std::filesystem::path& to)
{
	std::ifstream in(from);
	std::ofstream out(to);

	/// ------ Add generic comment --------------
	if (!m_renameFailed) { // added for "backwards compatibility"
		auto finder = from.wstring().find_last_of('\\'); // should find the homework number in file's name
		finder++;
		try {
			int homeworkNum = stoi(from.wstring().substr(finder));

			auto m_genericComment = m_GENERIC_COMMENT;
			auto finder2 = m_genericComment.find('!');

			m_genericComment.replace(finder2, 1, std::to_string(homeworkNum));

			out << m_genericComment;
		}
		catch (...) {
			NG_LOG_WARN("Failed to add generic comments to: ", to);
		}
	}
	/// ------ Add generic comment --------------

	std::string tmp;
	while (getline(in, tmp)) {
		out << tmp << "\n";
	}

	in.close();
	out.close();
}

