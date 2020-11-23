#pragma once
#include <string>
#include <vector>
#include "nfd.h"

// this code converts the nfd-library to support romanian and hungarian foreign characters
// note that the conversion is unsafe, handle this sparingly

namespace wnfd {
    typedef wchar_t wnfdchar_t;

    // single file open dialog 
    /*nfdresult_t openDialog(const nfdchar_t* filterList,
        const nfdchar_t* defaultPath,
        nfdchar_t** outPath) {

    }

    // multiple file open dialog
    nfdresult_t openDialogMultiple(const nfdchar_t* filterList,
        const nfdchar_t* defaultPath,
        nfdpathset_t* outPaths);

    // save dialog
    nfdresult_t saveDialog(const nfdchar_t* filterList,
        const nfdchar_t* defaultPath,
        nfdchar_t** outPath);*/

    // select folder dialog
    // TODO: i'll never do this aight
    /*nfdresult_t pickFolder(const std::wstring& defaultPath,
        std::wstring* outPath);*/

    nfdresult_t pickFolder(const nfdchar_t* defaultPath,
        std::wstring* outPath);

    // blah - > a
    std::wstring unsafeStringToWstring(const std::string& input);

    // á -> blah
    std::string unsafeWstringToString(const std::wstring& input);

    // characters that will be converted
    static const std::vector<std::pair<std::vector<char>, wchar_t>> WNFD_CONVERTABLES = {
    { std::vector<char>{'Å', '‘'},  L'ő'},
    { std::vector<char>{'Ã', 'º'},  L'ú'},
    { std::vector<char>{'Å', '±'},  L'ű'},
    { std::vector<char>{'Ã', '¼'},  L'ü'},
    { std::vector<char>{'Ã', '³'},  L'ó'},
    { std::vector<char>{'Ã', '©'},  L'é'},
    { std::vector<char>{'Ã', '¡'},  L'á'},
    { std::vector<char>{'Ã', '¶'},  L'ö'},
    { std::vector<char>{'Ä', 'ƒ'},  L'ă'},
    { std::vector<char>{'Ã', '®'},  L'î'},
    { std::vector<char>{'Ã', '¢'},  L'â'},
    { std::vector<char>{'Å', 'Ÿ'},  L'ş'},
    { std::vector<char>{'Å', '£'},  L'ţ'}
    };
};
