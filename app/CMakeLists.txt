cmake_minimum_required(VERSION 3.4.1)

add_definitions("-DNO_CRYPTO")
include_directories(src/main/cpp)

#native-lib
file(GLOB PROJECT_SOURCES1
        "src/main/cpp/FileManager.c"
        )

find_library( # Sets the name of the path variable.
        log-lib
        log)

add_library(
        filemanager
        SHARED
        ${PROJECT_SOURCES1}
        )

target_link_libraries( # Specifies the target library.
        filemanager
        ${log-lib})

