#include "Settings.h"
#include "wnfd.h"

void Settings::save()
{
	std::ofstream out{ m_fileName };

	if (out.good()) {
		out << wnfd::unsafeWstringToString(inputPath) << std::endl;
		out << wnfd::unsafeWstringToString(outputPath) << std::endl;
		out << labString << std::endl;
		out << hasNewFolder << std::endl;
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

		getline(in, tmp);
		labString = tmp;
		
		in >> hasNewFolder;
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
	hasNewFolder = false;
	labString = "0";
}
