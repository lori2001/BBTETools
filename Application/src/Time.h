#pragma once
#include "NGin.h"
#include <istream>
#include <ostream>

class Time {
public:
	Time() : Time(0, 0) {}
	Time(const int in_h, const int in_m) {
		int hourRemainder = 0;
		int minRemainder = 0; // minute remainder
		int hour = in_h;
		int min = in_m;

		if (hour < 0 || min < 0) {
			throw("time cannot be negative!");
		}

		if (min > MAX_MIN) {
			minRemainder = min / MAX_MIN;
			min = min % MAX_MIN;
		}
		hour += minRemainder;
		if (hour > MAX_H) {
			hourRemainder = hour / MAX_H; // lost information
			hour = hour % MAX_H;
		}

		h = hour; m = min;
	}
	Time(const Time& in) : Time(in.h, in.m) {}

	Time operator+ (const Time& r) {
		return { m + r.m, h + r.h };
	}
	Time operator= (const Time& r) {
		return r;
	}

	// converts minutes into hours + minutes
	Time minToHour(int mins) {
		int hours = mins / 60;
		int newMins = mins % 60;

		return {hours, mins};
	}

private:
	static constexpr int MAX_H = 23;
	static constexpr int MAX_MIN = 59;

	int h = 0, m = 0;

	friend std::ostream& operator<<(std::ostream& out, const Time& T) {
		out << T.h << ":" << T.m;
		return out;
	}

	friend std::istream& operator>>(std::istream& in, Time& T) {
		std::string tmp = "00:00";
		in >> tmp;

		try {
			int tHour = stoi(tmp);

			int tMin = stoi(tmp.substr(tmp.find(":") + 1));

			T = { tHour, tMin };
		}
		catch (...) {
			NG_LOG_ERROR("Error reading time entity from file!");
			system("pause");
			exit(1);
		}

		return in;
	}

};