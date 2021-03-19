#include "Schedule.h"

void Schedule::load()
{
	using namespace std;

	ifstream in(fName);

	if (in.good()) {
		Appointment app;
		vecApps.clear();
		while (in >> app) {
			vecApps.push_back(app);
		}
	}
	else {
		setToDefaults();
		save();
	}

	in.close();
}

void Schedule::save()
{
	using namespace std;
	ofstream out(fName);

	if (out.good()) {
		for (auto &it: vecApps) {
			out << it;
		}
	}
	else {
		NG_LOG_ERROR("Error creating file: ", fName);
		system("pause");
		exit(1);
	}
	
	out.close();
}

void Schedule::setToDefaults()
{
	vecApps.clear();
	vecApps.push_back({{ "ScheduleNeedsFilling" }, { 12, 10 }, 120});
}
