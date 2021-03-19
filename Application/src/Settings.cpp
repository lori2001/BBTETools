#include "Settings.h"
#include "wnfd.h"

void Settings::save()
{
	std::ofstream out{ m_fileName };

	if (out.good()) {
		out << wnfd::unsafeWstringToString(inputPath) << std::endl;
		out << wnfd::unsafeWstringToString(outputPath) << std::endl;
		out << idString << std::endl;
		out << groupString << std::endl;
		out << nameString << std::endl;
		out << labString << std::endl;
		out << hasNewFolder << std::endl;
		out << folderForEach << std::endl;
	}

	out.close();
}

void Settings::load()
{
	std::ifstream in{ m_fileName };

	if (in.good()) {
		std::string tmp;

		getline(in, tmp);
		inputPath = wnfd::unsafeStringToWstring(tmp);
		
		getline(in, tmp);
		outputPath = wnfd::unsafeStringToWstring(tmp);

		getline(in, idString);
		getline(in, groupString);
		getline(in, nameString);
		getline(in, labString);
		
		in >> hasNewFolder;
		in >> folderForEach;
	}
	else {
		setToDefaults();
	}

	in.close();
}

void Settings::setToDefaults()
{
	inputPath = L"D:\\";
	outputPath = L"C:\\Users\\Lorand\\Desktop\\";
	idString = "nnam0000";
	groupString = "611";
	nameString = "Írd ide a neved!";
	labString = "0";
	hasNewFolder = false;
	folderForEach = false;
}
