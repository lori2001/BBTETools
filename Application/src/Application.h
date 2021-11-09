#pragma once
#define MAIN_LEVEL

#ifndef _DEBUG // limits to release window
	#define NG_CONSOLE_NOPRINT // disables unnecessary printing logic
#endif

#include "NGin.h"
#include "HomeworkHandler.h"

#include "Settings.h"

#include "AppUISettings.h"

class Application : public ng::Main
{
public:
	Application();
	~Application();

	/* abstract overrides */
	virtual void handleEvents();
	virtual void update();
	virtual void draw(sf::RenderTarget& target, sf::RenderStates states) const;
private:
	const AppUISettings appUISettings;

	static constexpr unsigned MAX_LOCATION_CHARACTERS = 60;

	const std::string m_INPUT_BASE_STRING = "BEMENET: ";
	sf::Text m_inputFolderName{ appUISettings.getTextStyle() };
	ng::Button m_changeInputFolder{ appUISettings };

	const std::string m_OUTPUT_BASE_STRING = "KIMENET: ";
	sf::Text m_outputFolderName{ appUISettings.getTextStyle() };
	ng::Button m_changeOutputFolder{ appUISettings };

	ng::Switcher m_newDirectorySwitcher{ appUISettings };

	void updateOutPath(bool append, bool newDir);
	void updateOutPath();
	void updateOutPath(const std::wstring& dialogOutput);
	std::string getDirectoryName();

	std::string m_FOLDER_FOR_EACH_STRING = "Külön folder minden feladatnak:";
	sf::Text m_folderForEachText{ appUISettings.getTextStyle() };
	ng::Switcher m_folderForEachSwitcher{ appUISettings };

	sf::Text m_labNumberText{ appUISettings.getTextStyle()};
	const sf::String m_LAB_NUMBER_STRING = "Házi Sorszám:";
	ng::InputText m_labNumberInputText{ appUISettings };
	
	sf::Text m_nameText{ appUISettings.getTextStyle()};
	const sf::String m_NAME_STRING = "Név:";
	ng::InputText m_nameInputText{ appUISettings };

	sf::Text m_groupText{ appUISettings.getTextStyle() };
	const sf::String m_GROUP_STRING = "Csoport Szám:";
	ng::InputText m_groupInputText{ appUISettings };

	sf::Text m_idText{ appUISettings.getTextStyle() };
	const sf::String m_ID_STRING = "Azonosító:";
	ng::InputText m_idInputText{ appUISettings };

	ng::Button m_gatherHomework{ appUISettings };

	// returns string with a maximum character limitation
	sf::String applyLimits(const std::string& in) {
		std::string sizeChecker = in;
		if (sizeChecker.size() > MAX_LOCATION_CHARACTERS) {
			sizeChecker = sizeChecker.substr(0, MAX_LOCATION_CHARACTERS) + std::string("...");
		}
		return sizeChecker;
	}
};

ng::Main* setMainLevel() {
	return new Application;
}