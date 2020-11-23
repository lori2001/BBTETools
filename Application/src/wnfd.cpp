#include "wnfd.h"

namespace wnfd {
	/*nfdresult_t pickFolder(const std::wstring& defaultPath, std::wstring* outPath)
	{
        auto tmp = unsafeWstringToString(defaultPath);
        const nfdchar_t* safeDefaultPath = tmp.c_str();

        nfdchar_t* safeOutPath = NULL;
        const nfdresult_t result = NFD_PickFolder(safeDefaultPath, &safeOutPath);

        *outPath = unsafeStringToWstring(std::string(safeOutPath));

        return result;
	}*/
    nfdresult_t pickFolder(const nfdchar_t* defaultPath, std::wstring* outPath)
    {
        nfdchar_t* safeOutPath = NULL;
        const nfdresult_t result = NFD_PickFolder(defaultPath, &safeOutPath);

        if (result == NFD_OKAY) {
            *outPath = unsafeStringToWstring(std::string(safeOutPath));
        }

        return result;
    }
    std::wstring unsafeStringToWstring(const std::string& input)
    {
        std::wstring converted = L"";
        if (input.size() > 0) {
            converted.resize(input.size(), ' ');
            int ci = 0;
            bool lastFound = false;
            bool found = false;
            for (size_t i = 0; i < input.size() - 1; i++) {
                lastFound = found;
                found = false;
                for (auto& it : WNFD_CONVERTABLES) {
                    if (input[i] == it.first[0] && input[i + 1] == it.first[1]) {
                        converted[ci++] = it.second;
                        i++;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    converted[ci++] = input[i];
                }

                if (i == input.size() - 2 && lastFound == false) {
                    converted[ci++] = input.back();
                }
            }
            converted.resize(ci);
        }
        return converted;
    }
    std::string unsafeWstringToString(const std::wstring& input)
    {
        std::string converted;
        converted.resize(input.size(), ' ');

        int ci = 0;
        for (int i = 0; i < input.size(); i++) {
            bool found = false;
            for (int j = 0; j < WNFD_CONVERTABLES.size() && !found; j++) {
                if (input[i] == WNFD_CONVERTABLES[j].second) {
                    converted.insert(ci, 1, WNFD_CONVERTABLES[j].first[0]);
                    converted[++ci] = WNFD_CONVERTABLES[j].first[1];
                    ci++;
                    found = true;
                }
            }

            if (!found) {
                converted[ci++] = input[i];
            }
        }

        return converted;
    }
}