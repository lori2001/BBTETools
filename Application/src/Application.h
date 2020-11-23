#pragma once
#define MAIN_LEVEL

#ifndef _DEBUG // limits to release window
	#define NG_CONSOLE_NOPRINT // disables unnecessary printing logic
#endif


#include "NGin.h"
#include "HomeworkHandler.h"

#include "Settings.h"

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
	const sf::Color ITEM_COLOR{ 61, 207, 237 };
	const sf::Color SELECT_COLOR{ 61, 207, 237 };
	const sf::Color TEXT_COLOR{ 255, 255, 255 };

	const std::string m_INPUT_BASE_STRING = "BEMENET: ";
	sf::Text m_inputFolderName{ m_INPUT_BASE_STRING, NG_FONT("arial.ttf"), 30 };
	ng::Button m_changeInputFolder;

	const std::string m_OUTPUT_BASE_STRING = "KIMENET: ";
	sf::Text m_outputFolderName{ m_OUTPUT_BASE_STRING, NG_FONT("arial.ttf"), 30 };
	ng::Button m_changeOutputFolder;

	ng::Switcher m_newDirectorySwitcher;
	const std::string m_DIRECTORY_NAME = "Bekuldesre\\";

	sf::Text m_LabNumberText;
	const sf::String m_LAB_NUMBER_STRING = "Labor Szám:";
	ng::InputText m_LabNumberInputText;

	ng::Button m_gatherHomework;
};

ng::Main* setMainLevel() {
	return new Application;
}