#pragma once

#include <stdexcept>
#include <functional>
#include <iostream>
#include <sstream>
#include <iomanip>
#include <unordered_map>

#include <cf.hpp>

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

template <typename T>
void assert_no_duplicates(const std::vector<cf::input<T>>& v) {
	for (size_t i{0}; i < v.size(); i++) {
		for (size_t j{i + 1}; j < v.size(); j++) {
			if (v[i] == v[j]) {
				throw std::runtime_error("Vector contains duplicate value '" +
										 cf::utils::get_bit_string(v[i]) + "' at indices " +
										 std::to_string(i) + " and " + std::to_string(j));
			}
		}
	}
}

template <typename T>
void assert_equals_without_order(const std::vector<cf::input<T>>& expected,
								 const std::vector<cf::input<T>>& actual) {
	assert_same_size(expected, actual);
	std::unordered_map<cf::input<T>, size_t> expected_map;
	for (const auto x : expected) {
		if (expected_map.count(x) == 0) {
			expected_map[x] = 1;
		} else {
			expected_map[x] = expected_map[x] + 1;
		}
	}
	std::unordered_map<cf::input<T>, size_t> actual_map;
	for (const auto x : actual) {
		if (actual_map.count(x) == 0) {
			actual_map[x] = 1;
		} else {
			actual_map[x] = actual_map[x] + 1;
		}
	}
	if (expected_map != actual_map) {
		for (const auto entry : expected_map) {
			const cf::input<T> key = entry.first;
			const size_t expected_count = entry.second;
			const size_t actual_count = actual_map[key];
			if (actual_count != expected_count) {
				throw std::runtime_error("Value '" + cf::utils::get_bit_string(key) +
										 "' appeared " + std::to_string(actual_count) +
										 " times instead of " + std::to_string(expected_count) +
										 ".");
			}
		}
		for (const auto entry : actual_map) {
			if (expected_map.count(entry.first) == 0) {
				throw std::runtime_error("Value '" + cf::utils::get_bit_string(entry.first) +
										 "' appeared " + std::to_string(entry.second) +
										 " times instead of 0.");
			}
		}
	}
}

}  // namespace testing
