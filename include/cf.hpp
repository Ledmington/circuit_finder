#pragma once

#include <iostream>
#include <string>
#include <sstream>
#include <cassert>
#include <tuple>

namespace cf {

// TODO: can this be a std::pair<T,T>?
template <typename T>
struct input {
	static_assert(std::is_integral_v<T>);
	static_assert(std::is_unsigned_v<T>);

	T value;
	T mask;

	bool operator==(const input<T>& other) const {
		return value == other.value && mask == other.mask;
	}

	bool operator<(const input<T>& other) const {
		return std::tie(value, mask) < std::tie(other.value, other.mask);
	}
};

namespace utils {
template <typename T>
std::string get_bit_string(const cf::input<T>& x) {
	std::ostringstream ss;
	const size_t nbits = 8 * sizeof(T);
	for (size_t i{0}; i < nbits; i++) {
		const T bit = static_cast<T>(1) << (nbits - i - 1);
		if ((x.mask & bit) == 0) {
			ss << "-";
		} else {
			if ((x.value & bit) != 0) {
				ss << "1";
			} else {
				ss << "0";
			}
		}
	}

	return ss.str();
}

template <typename T>
std::string get_expression(const cf::input<T>& x) {
	std::ostringstream ss;
	const size_t nbits = 8 * sizeof(T);
	for (size_t i{0}; i < nbits; i++) {
		const T bit = static_cast<T>(1) << (nbits - i - 1);
		if ((x.mask & bit) == 0) {
			continue;
		}

		if (ss.tellp() > 0) {
			ss << "&";
		}
		if ((x.value & bit) != 0) {
			ss << static_cast<char>('A' + i);
		} else {
			ss << "(~" << static_cast<char>('A' + i) << ")";
		}
	}

	return ss.str();
}

}  // namespace utils

}  // namespace cf
