cmake_minimum_required(VERSION 3.21 FATAL_ERROR)

if(CMAKE_SOURCE_DIR STREQUAL CMAKE_CURRENT_SOURCE_DIR)
    # We are building as a stand-alone project
    project("Circuit Finder" VERSION 0.1.0)

    set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
endif()

option(CF_TEST "Enables compilation of the test suite" OFF)
option(CF_BENCH "Enables compilation of the benchmark suite" OFF)

# Build in Debug mode by default
if(NOT CMAKE_BUILD_TYPE AND NOT CMAKE_CONFIGURATION_TYPES)
    set(CMAKE_BUILD_TYPE "Debug" CACHE STRING "The type of build" FORCE)
endif()

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_CURRENT_SOURCE_DIR}/cmake/modules/")

include("${CMAKE_MODULE_PATH}/CPM.cmake")

if(CF_BENCH)
    set(GBENCHMARK_VERSION "1.9.1")
    CPMAddPackage(
        NAME benchmark
        GITHUB_REPOSITORY google/benchmark
        VERSION ${GBENCHMARK_VERSION}
        OPTIONS "BENCHMARK_ENABLE_TESTING Off"
    )
endif()

# Default Release flags
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    list(APPEND CF_FLAGS -O3 -DNDEBUG -march=native -mtune=native)
endif()

# Default ReleaseWithDebugInfo flags
if(CMAKE_BUILD_TYPE STREQUAL "RelWithDebInfo")
    list(APPEND CF_FLAGS -O2 -g -fno-omit-frame-pointer -fno-optimize-sibling-calls -march=native -mtune=native)
endif()

# Default Debug flags
if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    list(APPEND CF_FLAGS -O0 -g -fno-omit-frame-pointer -fno-optimize-sibling-calls)
endif()

set(CF_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/include")
set(CF_EXEC_DIR "${CMAKE_CURRENT_SOURCE_DIR}/exec")
set(CF_TEST_DIR "${CMAKE_CURRENT_SOURCE_DIR}/test")
set(CF_BENCH_DIR "${CMAKE_CURRENT_SOURCE_DIR}/bench")

# Setup warnings
set(WARNINGS -Wall -Wextra -Wpedantic -Wshadow -Wsign-conversion -Wfloat-conversion -Wfloat-equal -Werror)

# Common target configuration
add_library(cf-config INTERFACE)
target_compile_features(cf-config INTERFACE cxx_std_17)
set_target_properties(cf-config
    PROPERTIES
    CXX_STANDARD_REQUIRED ON
    CXX_EXTENSIONS OFF)

target_compile_options(cf-config INTERFACE ${CF_FLAGS})
target_compile_options(cf-config INTERFACE ${WARNINGS})
target_link_options(cf-config INTERFACE ${CF_FLAGS})

# Define primary library target
add_library(cf INTERFACE)
target_include_directories(cf INTERFACE ${CF_INCLUDE_DIR})
target_link_libraries(cf INTERFACE cf-config)

add_executable(main "${CF_EXEC_DIR}/main.cpp")
target_link_libraries(main PRIVATE cf)
target_compile_options(main PRIVATE -fno-exceptions -fno-rtti)

if(CF_TEST)
    enable_testing()

    macro(add_cf_test_executable NAME)
        set(test_executable "test_${NAME}")
        add_executable(${test_executable} "${CF_TEST_DIR}/${test_executable}.cpp")
        target_include_directories(${test_executable} INTERFACE ${CF_INCLUDE_DIR})
        target_link_libraries(${test_executable} PRIVATE cf)
        add_test(NAME ${test_executable} COMMAND ${test_executable})
    endmacro()

    add_cf_test_executable(utils)
    add_cf_test_executable(functions)
    add_cf_test_executable(qmc)
endif()

if(CF_BENCH)

    macro(add_cf_bench_executable NAME)
        set(bench_executable "bench_${NAME}")
        add_executable(${bench_executable} "${CF_BENCH_DIR}/${bench_executable}.cpp")
        target_link_libraries(${bench_executable} PRIVATE cf benchmark)
    endmacro()

    add_cf_bench_executable(qmc)
endif()

find_package(ClangFormat QUIET)

file(GLOB_RECURSE CF_SOURCE_FILES
    "${CF_INCLUDE_DIR}/**.*pp"
    "${CF_EXEC_DIR}/**.*pp"
    "${CF_TEST_DIR}/**.*pp"
    "${CF_BENCH_DIR}/**.*pp")

if(CLANG_FORMAT_FOUND)
    message(STATUS "clang-format: found ${CLANG_FORMAT_EXECUTABLE} v${CLANG_FORMAT_VERSION}")
    message(STATUS "clang-format: adding custom target 'format'")
    message(STATUS "clang-format: adding custom target 'format-fix'")
    add_custom_target(format
        COMMAND
        ${CLANG_FORMAT_EXECUTABLE} -style=file --dry-run --Werror
        ${CF_SOURCE_FILES}
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
    add_custom_target(format-fix
        COMMAND
        ${CLANG_FORMAT_EXECUTABLE} -style=file -i
        ${CF_SOURCE_FILES}
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
endif(CLANG_FORMAT_FOUND)

find_package(ClangTidy QUIET)

if(CLANG_TIDY_FOUND)
    message(STATUS "clang-tidy: found ${CLANG_TIDY_EXECUTABLE} v${CLANG_TIDY_VERSION}")
    message(STATUS "clang-tidy: adding custom target 'tidy'")
    message(STATUS "clang-tidy: adding custom target 'tidy-fix'")
    set(tidy_cmd
        ${CLANG_TIDY_EXECUTABLE} -p ${CMAKE_CURRENT_BINARY_DIR} -warnings-as-errors=*)
    add_custom_target(tidy
        COMMAND
        ${tidy_cmd}
        ${CF_SOURCE_FILES}
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
    add_custom_target(tidy-fix
        COMMAND
        ${tidy_cmd} --fix
        ${CF_SOURCE_FILES}
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
endif(CLANG_TIDY_FOUND)

# Converting CF_FLAGS to a string
string(REPLACE ";" " " CF_FLAGS "${CF_FLAGS}")

message(STATUS "")
message(STATUS "Successfully configured ${CMAKE_PROJECT_NAME} v${CMAKE_PROJECT_VERSION}")
message(STATUS "CMAKE_CXX_COMPILER : ${CMAKE_CXX_COMPILER} (${CMAKE_CXX_COMPILER_ID}) v${CMAKE_CXX_COMPILER_VERSION}")
message(STATUS "CMAKE_BUILD_TYPE   : ${CMAKE_BUILD_TYPE}")
message(STATUS "CF_FLAGS           : ${CF_FLAGS}")
message(STATUS "CF_TEST            : ${CF_TEST}")
message(STATUS "CF_BENCH           : ${CF_BENCH}")
if(CF_BENCH)
    message(STATUS "Google Benchmark   : ${GBENCHMARK_VERSION}")
endif()
message(STATUS "")
