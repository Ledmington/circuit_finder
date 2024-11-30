#pragma once

#include <stdexcept>
#include <functional>
#include <iostream>

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
	std::cout << " " << failed_tests << " failed tests out of " << total_tests << " ("
			  << (static_cast<double>(failed_tests) / static_cast<double>(total_tests)) << ")."
			  << std::endl;
	std::cout << std::endl;
	std::exit(failed_tests == 0 ? 0 : -1);
}

template <typename T>
void assert_equals(const T& expected, const T& actual) {
	if (expected != actual) {
		if constexpr (std::is_same_v<T, std::string>) {
			throw std::runtime_error("Expected '" + expected + "' but was '" + actual + "'");
		} else {
			throw std::runtime_error("Expected '" + std::to_string(expected) + "' but was '" +
									 std::to_string(actual) + "'");
		}
	}
}

}  // namespace testing
