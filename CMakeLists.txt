cmake_minimum_required(VERSION 3.21 FATAL_ERROR)

project("Circuit Finder" VERSION 0.1)

set(CMAKE_POLICY_DEFAULT_CMP0135 NEW)

if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE "Release" CACHE STRING "The type of build" FORCE)
    set(CMAKE_CXX_FLAGS_RELEASE "-O3 -DNDEBUG -march=native -mtune=native -fno-exceptions -fno-rtti" CACHE STRING "Release flags" FORCE)
endif()

option(CF_TEST "Enable build of test suite" ON)
option(CF_BENCH "Enable build of benchmark suite" OFF)

enable_language(CXX)
set(CMAKE_CXX_STANDARD 17 CACHE STRING "C++ standard")
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_CURRENT_SOURCE_DIR}/cmake/modules/")

include(cmake/modules/CPM.cmake)

set(HIGHWAY_VERSION "1.0.7" CACHE STRING "Google Highway version")
CPMAddPackage(
    NAME highway
    GITHUB_REPOSITORY google/highway
    GIT_TAG "${HIGHWAY_VERSION}"
    VERSION ${HIGHWAY_VERSION}
    OPTIONS "HWY_ENABLE_TESTS Off"
)

set(WARNINGS
    -Werror
    -Wall
    -Wextra
    -pedantic
    -Wshadow
    -Wsign-conversion
    -Wfloat-conversion
    -Wfloat-equal)

set(CF_INCLUDE_DIR "${CMAKE_SOURCE_DIR}/include/" CACHE STRING "")

add_executable(cf
    "${CMAKE_SOURCE_DIR}/src/main.cpp"
    "${CMAKE_SOURCE_DIR}/src/search.cpp"
)
target_include_directories(cf PUBLIC ${CF_INCLUDE_DIR})
target_compile_options(cf INTERFACE ${WARNINGS})

# Custom target to build every target of this library
add_custom_target(circuit-finder DEPENDS cf)

if(CF_TEST)
    set(GTEST_VERSION "1.14.0" CACHE STRING "Google Test version")
    CPMAddPackage(
        NAME googletest
        GITHUB_REPOSITORY google/googletest
        VERSION ${GTEST_VERSION}
        OPTIONS "INSTALL_GTEST OFF" "gtest_force_shared_crt"
    )
    enable_testing()

    add_executable(cf-test "${CMAKE_SOURCE_DIR}/test/test_logic_not.cpp")
    target_include_directories(cf-test PUBLIC ${CF_INCLUDE_DIR})
    target_link_libraries(cf-test PRIVATE
        GTest::gtest_main
    )

    include(GoogleTest)
    gtest_discover_tests(cf-test)

    add_dependencies(circuit-finder cf)
endif()

if(CF_BENCH)
    set(GBENCHMARK_VERSION "1.8.3" CACHE STRING "Google Benchmark version")
    CPMAddPackage(
        NAME benchmark
        GITHUB_REPOSITORY google/benchmark
        VERSION ${GBENCHMARK_VERSION}
        OPTIONS "BENCHMARK_ENABLE_TESTING Off"
    )
endif()

find_package(ClangFormat QUIET)

if(CLANG_FORMAT_FOUND)
    message(STATUS "clang-format: found ${CLANG_FORMAT_EXECUTABLE} v${CLANG_FORMAT_VERSION}")
    message(STATUS "clang-format: adding custom target 'format'")
    message(STATUS "clang-format: adding custom target 'format-fix'")
    add_custom_target(format
        COMMAND
        ${CLANG_FORMAT_EXECUTABLE} -style=file --dry-run --Werror
        "${CMAKE_SOURCE_DIR}/include/*.hpp"
        "${CMAKE_SOURCE_DIR}/src/*.cpp"
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
    add_custom_target(format-fix
        COMMAND
        ${CLANG_FORMAT_EXECUTABLE} -style=file -i
        "${CMAKE_SOURCE_DIR}/include/*.hpp"
        "${CMAKE_SOURCE_DIR}/src/*.cpp"
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
endif()

message(STATUS "")
message(STATUS "Successfully configured ${PROJECT_NAME} v${PROJECT_VERSION}")
message(STATUS "CMAKE_BUILD_TYPE         : ${CMAKE_BUILD_TYPE}")

if(CMAKE_BUILD_TYPE STREQUAL "Release")
    message(STATUS "CMAKE_CXX_FLAGS_RELEASE  : ${CMAKE_CXX_FLAGS_RELEASE}")
endif()

if(CF_TEST)
    message(STATUS "Google Test version      : ${GTEST_VERSION}")
endif()

if(CF_BENCH)
    message(STATUS "Google Benchmark version : ${GBENCHMARK_VERSION}")
endif()

message(STATUS "Google Highway version   : ${HIGHWAY_VERSION}")
message(STATUS "")
