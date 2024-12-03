#pragma once

#include <stdexcept>
#include <functional>
#include <iostream>
#include <sstream>
#include <iomanip>

namespace testing {

size_t total_tests{0};
size_t failed_tests{0};

void run(const std::string& test_name, const std::function<void(void)>& test) {
	total_tests++;
	try {
		test();
	} catch (const std::exception& e) {
		std::cerr << " " << test_name << " failed: " << e.what() << std::endl;
		failed_tests++;
	}
}

void run(const std::function<void(void)>& test) {
	run("Test_" + std::to_string(total_tests + 1), test);
}

void report() {
	std::cout << std::endl;
	std::cout << " " << failed_tests << " failed tests out of " << total_tests << " (" << std::fixed
			  << std::setprecision(2)
			  << (static_cast<double>(failed_tests) / static_cast<double>(total_tests) * 100.0)
			  << "%)." << std::endl;
	std::cout << std::endl;
	std::exit(failed_tests == 0 ? 0 : -1);
}

template <typename T>
static std::string get_string(const T& val) {
	if constexpr (std::is_same_v<T, std::string>) {
		return val;
	} else {
		return std::to_string(val);
	}
}

template <typename T>
void assert_equals(const T& expected, const T& actual) {
	if (expected != actual) {
		throw std::runtime_error("Expected '" + get_string(expected) + "' but was '" +
								 get_string(actual) + "'");
	}
}

template <typename T>
void assert_same_size(const std::vector<T>& expected, const std::vector<T>& actual) {
	if (expected.size() != actual.size()) {
		throw std::runtime_error("Expected to have same size but were " +
								 std::to_string(expected.size()) + " and " +
								 std::to_string(actual.size()) + ", respectively.");
	}
}

}  // namespace testing
