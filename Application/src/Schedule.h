#pragma once
#include <fstream>

#include "NGin.h"
#include "Appointment.h"

class Schedule {
public:
	void load();
	void save();

private:
	static constexpr const char* fName = "schedule.txt";

	std::vector<Appointment> vecApps; // vector of appointments
	void setToDefaults();
};