#pragma once
#include "Time.h"
#include <string>
#include <istream>
#include <ostream>

class Appointment {
private:
	std::string name;
	Time start;

	int durMin = 120; // 2 hours
	int dayOfWeek = 0; // Mon = 0
public:
	Appointment() {}
	Appointment(std::string n, Time s, int dur) {
		name = n;
		start = s;
		durMin = dur;
	}

	Time getStart() const { return start; }
	std::string getName() const { return name; }
	int getDurInMin() const { return durMin; }

	friend std::ostream& operator<<(std::ostream& out, const Appointment& T) {
		out << T.name << " " << T.dayOfWeek << " " << T.start << " " << T.durMin << std::endl;
		return out;
	}

	friend std::istream& operator>>(std::istream& in, Appointment& T) {
		in >> T.name;
		in >> T.dayOfWeek;
		in >> T.start;
		in >> T.durMin;

		return in;
	}
};
