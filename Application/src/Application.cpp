#include "Application.h"
#include "wnfd.h"

Application::Application()
{
	#ifndef _DEBUG // limits to release window
		ShowWindow(GetConsoleWindow(), SW_HIDE); // hides console
	#endif

	windowVideoMode = { 1366, 768 };
	windowName = "University Tools";
	windowClearColor = { 20, 20, 20 };

	Settings::load();

	// --- Button --------------------------
	m_gatherHomework.setPosition({ 50.0F, 925.0F });
	m_gatherHomework.setTexture(NG_TEXTURE("button.png"));
	m_gatherHomework.setFont(NG_FONT("arial.ttf"));
	m_gatherHomework.setString("Házi Gyüjtés");
	m_gatherHomework.setCharacterSize(40);
	m_gatherHomework.setFillColor(ITEM_COLOR);
	m_gatherHomework.setScale({ 0.85F, 1.5F });
	m_gatherHomework.setSelectColor(SELECT_COLOR);
	m_gatherHomework.setSelectThickness(2.0F);

	m_inputFolderName.setPosition({ 480, 910 });
	m_inputFolderName.setString(m_INPUT_BASE_STRING + Settings::inputPath.u8string());

	m_changeInputFolder.setPosition({ 1600.0F, 900.0F });
	m_changeInputFolder.setTexture(NG_TEXTURE("button.png"));
	m_changeInputFolder.setFont(NG_FONT("arial.ttf"));
	m_changeInputFolder.setString("Cserélj");
	m_changeInputFolder.setCharacterSize(40);
	m_changeInputFolder.setFillColor(ITEM_COLOR);
	m_changeInputFolder.setScale({ 0.5F, 1.15F });
	m_changeInputFolder.setSelectColor(SELECT_COLOR);
	m_changeInputFolder.setSelectThickness(2.5F);

	m_changeOutputFolder.setPosition({ 1600.0F, 980.0F });
	m_changeOutputFolder.setTexture(NG_TEXTURE("button.png"));
	m_changeOutputFolder.setFont(NG_FONT("arial.ttf"));
	m_changeOutputFolder.setString("Cserélj");
	m_changeOutputFolder.setCharacterSize(40);
	m_changeOutputFolder.setFillColor(ITEM_COLOR);
	m_changeOutputFolder.setScale({ 0.5F, 1.15F });
	m_changeOutputFolder.setSelectColor(SELECT_COLOR);
	m_changeOutputFolder.setSelectThickness(2.5F);

	m_newDirectorySwitcher.setTexture(NG_TEXTURE("switcher.png"));
	m_newDirectorySwitcher.setButtonColor(ITEM_COLOR);
	m_newDirectorySwitcher.setMarkColor(SELECT_COLOR);
	m_newDirectorySwitcher.setSelectColor(SELECT_COLOR);
	m_newDirectorySwitcher.setSelectThickness(2.5F);
	m_newDirectorySwitcher.setPosition({ 1820.0F, 980.0F });
	m_newDirectorySwitcher.setScale({ 0.95F, 0.95F });

	m_newDirectorySwitcher.setIsActive(Settings::hasNewFolder);

	m_outputFolderName.setPosition({ 480, 990 });
	m_outputFolderName.setString(m_OUTPUT_BASE_STRING + Settings::outputPath.u8string());

	m_LabNumberText.setPosition({ 50, 50 });
	m_LabNumberText.setString(m_LAB_NUMBER_STRING);
	m_LabNumberText.setFont(NG_FONT("arial.ttf"));
	m_LabNumberText.setCharacterSize(30);

	m_LabNumberInputText.setPosition({ 60, 100 });
	m_LabNumberInputText.setFont(NG_FONT("arial.ttf"));
	m_LabNumberInputText.setTexture(NG_TEXTURE("inputtext.png"));
	m_LabNumberInputText.setFillColor(ITEM_COLOR);
	m_LabNumberInputText.setSelectColor(SELECT_COLOR);
	m_LabNumberInputText.setSize({150, 40});
	m_LabNumberInputText.setString(Settings::labString);
}

Application::~Application()
{
	Settings::save();
}

void Application::handleEvents()
{
	m_changeInputFolder.handleEvents(event_, ng::Cursor::getPosition());
	m_changeOutputFolder.handleEvents(event_, ng::Cursor::getPosition());
	m_gatherHomework.handleEvents(event_, ng::Cursor::getPosition());
	m_newDirectorySwitcher.handleEvents(event_, ng::Cursor::getPosition());
	m_LabNumberInputText.handleEvents(event_, ng::Cursor::getPosition());

	if (m_changeInputFolder.isActive()) {
		std::wstring dialogOutput;
		nfdresult_t result = wnfd::pickFolder("", &dialogOutput);

		Settings::inputPath = dialogOutput;
		m_inputFolderName.setString(m_INPUT_BASE_STRING + Settings::inputPath.u8string());
	}
	else if (m_changeOutputFolder.isActive()) {
		std::wstring dialogOutput;
		nfdresult_t result = wnfd::pickFolder("", &dialogOutput);

		Settings::outputPath = dialogOutput;

		if (m_newDirectorySwitcher.isActive()) {
			Settings::outputPath += "\\";
			Settings::outputPath += m_DIRECTORY_NAME;
		}

		m_outputFolderName.setString(m_OUTPUT_BASE_STRING + Settings::outputPath.u8string());
	}
	else if (m_gatherHomework.isActive()) {
		HomeworkHandler homeworkHandler{
			Settings::outputPath, Settings::inputPath
		};

		homeworkHandler.setLabString(Settings::labString);

		homeworkHandler.SaveFiles();
		NG_LOG_INFO("Finished Generating Homework L", Settings::labString, " ...");
	}

	if (m_newDirectorySwitcher.hasChanged()) {
		if (m_newDirectorySwitcher.isActive()) {
			Settings::outputPath += m_DIRECTORY_NAME;
			m_outputFolderName.setString(m_OUTPUT_BASE_STRING + Settings::outputPath.u8string());
			Settings::hasNewFolder = true;
		}
		else {
			auto tmp = Settings::outputPath;
			Settings::outputPath = tmp.wstring().substr(0, tmp.wstring().size() - m_DIRECTORY_NAME.size());
			m_outputFolderName.setString(m_OUTPUT_BASE_STRING + Settings::outputPath.u8string());
			Settings::hasNewFolder = false;
		}
	}

	if (m_LabNumberInputText.hasChanged()) {
		Settings::labString = m_LabNumberInputText.getString();
	}
}

void Application::update()
{
}

void Application::draw(sf::RenderTarget& target, sf::RenderStates states) const
{
	target.draw(m_inputFolderName);
	target.draw(m_outputFolderName);

	target.draw(m_gatherHomework);
	target.draw(m_changeInputFolder);
	target.draw(m_changeOutputFolder);
	target.draw(m_newDirectorySwitcher);

	target.draw(m_LabNumberText);
	target.draw(m_LabNumberInputText);
}
