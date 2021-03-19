#pragma once
#include "NGin.h"

class AppUISettings : public ng::UISettings {
public:
	AppUISettings() {
		baseColor = { 61, 207, 237 };
		selectColor = { 61, 207, 237 };
		highlightColor = { 61, 207, 237 };
		fontColor = { 255, 255, 255 };
		characterSize = 32;
		fontLoc = "arial.ttf";
	}
};
