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

	setWindowIcon("assets/icon.png");

	Settings::load();
	scheduleHandler.load();

	// --- Button --------------------------
	m_gatherHomework.setPosition({ 50.0F, 925.0F });
	m_gatherHomework.setString("Házi Gyüjtés");
	m_gatherHomework.setScale({ 0.85F, 1.5F });

	m_inputFolderName.setPosition({ 440, 910 });
	m_inputFolderName.setString(m_INPUT_BASE_STRING + applyLimits(Settings::inputPath.u8string()));

	m_changeInputFolder.setPosition({ 1600.0F, 900.0F });
	m_changeInputFolder.setString("Cserélj");
	m_changeInputFolder.setScale({ 0.5F, 1.15F });

	m_changeOutputFolder.setPosition({ 1600.0F, 980.0F });
	m_changeOutputFolder.setString("Cserélj");
	m_changeOutputFolder.setScale({ 0.5F, 1.15F });

	m_newDirectorySwitcher.setSelectThickness(2.5F);
	m_newDirectorySwitcher.setPosition({ 1820.0F, 980.0F });
	m_newDirectorySwitcher.setScale({ 0.95F, 0.95F });

	m_newDirectorySwitcher.setIsActive(Settings::hasNewFolder);

	m_outputFolderName.setPosition({ 440, 990 });

	m_labNumberText.setPosition({ 50, 45 });
	m_labNumberText.setString(m_LAB_NUMBER_STRING);

	m_labNumberInputText.setPosition({ 50, 100 });
	m_labNumberInputText.setSize({ 150, 40 });
	m_labNumberInputText.setString(Settings::labString);
	m_labNumberInputText.setBaseColor(sf::Color(244, 14, 56));

	m_nameText.setPosition({ 300, 45 });
	m_nameText.setString(m_NAME_STRING);
	m_nameText.setCharacterSize(30);

	m_nameInputText.setPosition({ 300, 95 });
	m_nameInputText.setString(Settings::nameString);

	m_groupInputText.setPosition({ 760, 95 });
	m_groupInputText.setSize({ 150, 40 });
	m_groupInputText.setString(Settings::groupString);
	
	m_groupText.setPosition({ 760, 45 });
	m_groupText.setString(m_GROUP_STRING);

	m_idText.setPosition({ 1030, 45 });
	m_idText.setString(m_ID_STRING);

	m_idInputText.setPosition({ 1030, 95 });
	m_idInputText.setString(Settings::idString);

	m_folderForEachText.setString(m_FOLDER_FOR_EACH_STRING);
	m_folderForEachText.setPosition(50, 830);
	m_folderForEachSwitcher.setPosition({ 520, 820 });
	m_folderForEachSwitcher.setIsActive(Settings::folderForEach);

	updateOutPath();
}

Application::~Application()
{
	Settings::save();
	scheduleHandler.save();
}

void Application::handleEvents()
{
	m_changeInputFolder.handleEvents(event_, ng::Cursor::getPosition());
	m_changeOutputFolder.handleEvents(event_, ng::Cursor::getPosition());
	m_gatherHomework.handleEvents(event_, ng::Cursor::getPosition());
	m_newDirectorySwitcher.handleEvents(event_, ng::Cursor::getPosition());
	m_labNumberInputText.handleEvents(event_, ng::Cursor::getPosition());
	m_nameInputText.handleEvents(event_, ng::Cursor::getPosition());
	m_groupInputText.handleEvents(event_, ng::Cursor::getPosition());
	m_idInputText.handleEvents(event_, ng::Cursor::getPosition());
	m_folderForEachSwitcher.handleEvents(event_, ng::Cursor::getPosition());

	if (m_folderForEachSwitcher.hasChanged()) {
		Settings::folderForEach = m_folderForEachSwitcher.isActive();
	}

	if (m_changeInputFolder.isActive()) {
		std::wstring dialogOutput;
		nfdresult_t result = wnfd::pickFolder("", &dialogOutput);

		Settings::inputPath = dialogOutput;

		// if more than 40 characters
		m_inputFolderName.setString(m_INPUT_BASE_STRING + applyLimits(Settings::inputPath.u8string()));
	}
	else if (m_changeOutputFolder.isActive()) {
		std::wstring dialogOutput;
		nfdresult_t result = wnfd::pickFolder("", &dialogOutput);
		
		updateOutPath(dialogOutput);
	}
	else if (m_gatherHomework.isActive()) {
		HomeworkHandler homeworkHandler{
			Settings::outputPath, Settings::inputPath
		};

		homeworkHandler.SaveFiles();
		NG_LOG_INFO("Finished Generating Homework L", Settings::labString, " ...");
	}

	updateOutPath(m_newDirectorySwitcher.hasChanged(), m_newDirectorySwitcher.isActive());

	if (m_labNumberInputText.hasChanged()) {
		Settings::labString = m_labNumberInputText.getString();
		updateOutPath();
	}
	else if (m_nameInputText.hasChanged()) {
		Settings::nameString = m_nameInputText.getString();
	}
	else if (m_groupInputText.hasChanged()) {
		Settings::groupString = m_groupInputText.getString();
	}
	else if (m_idInputText.hasChanged()) {
		Settings::idString = m_idInputText.getString();
		updateOutPath();
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

	target.draw(m_labNumberText);
	target.draw(m_labNumberInputText);

	target.draw(m_nameText);
	target.draw(m_nameInputText);

	target.draw(m_groupText);
	target.draw(m_groupInputText);

	target.draw(m_idText);
	target.draw(m_idInputText);

	target.draw(m_folderForEachText);
	target.draw(m_folderForEachSwitcher);
}

void Application::updateOutPath(bool append, bool newDir)
{
	if (append) { // append
		if (newDir) {
			// add
			Settings::outputPath += getDirectoryName();
			Settings::hasNewFolder = true;
		}
		else {
			// delete
			auto lastSlash = Settings::outputPath.wstring().find_last_of('\\');
			Settings::outputPath = Settings::outputPath.wstring().substr(0, lastSlash + 1);
			Settings::hasNewFolder = false;
		}

		m_outputFolderName.setString(m_OUTPUT_BASE_STRING + applyLimits(Settings::outputPath.u8string()));
	}
}

void Application::updateOutPath()
{
	if (Settings::hasNewFolder) // update
	{
		// delete
		auto lastSlash = Settings::outputPath.wstring().find_last_of('\\');
		Settings::outputPath = Settings::outputPath.wstring().substr(0, lastSlash + 1);

		// rewrite
		Settings::outputPath += getDirectoryName();
	}

	m_outputFolderName.setString(m_OUTPUT_BASE_STRING + applyLimits(Settings::outputPath.u8string()));
}

void Application::updateOutPath(const std::wstring& dialogOutput)
{
	Settings::outputPath = dialogOutput;
	Settings::outputPath += "\\";

	if (Settings::hasNewFolder) {
		Settings::outputPath += getDirectoryName();
	}

	m_outputFolderName.setString(m_OUTPUT_BASE_STRING + applyLimits(Settings::outputPath.u8string()));
}

std::string Application::getDirectoryName()
{
	return Settings::idString + std::string("_L") + Settings::labString;
}
