#pragma once

#include <iostream>
#include <string>
#include <sstream>
#include <cassert>
#include <tuple>
#include <limits>

namespace cf {

// TODO: can this be a std::pair<T,T>?
template <typename T>
struct input {
	static_assert(std::is_integral_v<T>);
	static_assert(std::is_unsigned_v<T>);

	T value;
	T mask;

	bool operator==(const input<T>& other) const {
		// Give priority to the mask
		return mask == other.mask && (value & mask) == (other.value & other.mask);
	}

	bool operator!=(const input<T>& other) const {
		return !(*this == other);
	}
};

}  // namespace cf

template <typename T>
struct std::hash<cf::input<T>> {
	size_t operator()(const cf::input<T>& x) const noexcept {
		if constexpr (sizeof(size_t) >= 2 * sizeof(T)) {
			// for types up to 32 bits
			return (static_cast<size_t>(x.value) << (8 * sizeof(T))) |
				   (static_cast<size_t>(x.mask));
		} else {
			// only for uint64_t
			return (x.value << 5) ^ (x.mask);
		}
	}
};

namespace cf {
namespace utils {

uint8_t popcount(uint8_t x) {
	x = (x & 0x55u) + ((x >> 1) & 0x55u);
	x = (x & 0x33u) + ((x >> 2) & 0x33u);
	x = (x & 0x0fu) + ((x >> 4) & 0x0fu);
	assert(x <= 8);
	return x;
}

uint16_t popcount(uint16_t x) {
	x = (x & 0x5555u) + ((x >> 1) & 0x5555u);
	x = (x & 0x3333u) + ((x >> 2) & 0x3333u);
	x = (x & 0x0f0fu) + ((x >> 4) & 0x0f0fu);
	x = (x & 0x00ffu) + ((x >> 8) & 0x00ffu);
	assert(x <= 16);
	return x;
}

uint32_t popcount(uint32_t x) {
	x = (x & 0x55555555u) + ((x >> 1) & 0x55555555u);
	x = (x & 0x33333333u) + ((x >> 2) & 0x33333333u);
	x = (x & 0x0f0f0f0fu) + ((x >> 4) & 0x0f0f0f0fu);
	x = (x & 0x00ff00ffu) + ((x >> 8) & 0x00ff00ffu);
	x = (x & 0x0000ffffu) + ((x >> 16) & 0x0000ffffu);
	assert(x <= 32);
	return x;
}

uint64_t popcount(uint64_t x) {
	x = (x & 0x5555555555555555u) + ((x >> 1) & 0x5555555555555555u);
	x = (x & 0x3333333333333333u) + ((x >> 2) & 0x3333333333333333u);
	x = (x & 0x0f0f0f0f0f0f0f0fu) + ((x >> 4) & 0x0f0f0f0f0f0f0f0fu);
	x = (x & 0x00ff00ff00ff00ffu) + ((x >> 8) & 0x00ff00ff00ff00ffu);
	x = (x & 0x0000ffff0000ffffu) + ((x >> 16) & 0x0000ffff0000ffffu);
	x = (x & 0x00000000ffffffffu) + ((x >> 32) & 0x00000000ffffffffu);
	assert(x <= 64);
	return x;
}

template <typename T>
T single_bit(const size_t index) {
	assert(index < 8 * sizeof(T));
	return static_cast<T>(1u) << index;
}

template <typename T>
T get_mask(const size_t nbits) {
	static_assert(std::is_integral_v<T>);
	static_assert(std::is_unsigned_v<T>);
	assert(nbits <= 8u * sizeof(T));

	if (nbits == 8u * sizeof(T)) {
		return std::numeric_limits<T>::max();
	} else {
		return (static_cast<T>(1u) << nbits) - static_cast<T>(1u);
	}
}

template <typename T>
std::string get_bit_string(const cf::input<T>& x) {
	std::ostringstream ss;
	const size_t nbits = 8 * sizeof(T);
	for (size_t i{0}; i < nbits; i++) {
		const T bit = cf::utils::single_bit<T>(nbits - i - 1);
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
std::string get_expression(const cf::input<T>& x, const bool uppercase,
						   const std::string& and_symbol, const std::string& not_symbol) {
	std::ostringstream ss;
	const size_t nbits = 8 * sizeof(T);
	for (size_t i{0}; i < nbits; i++) {
		const T bit = cf::utils::single_bit<T>(nbits - i - 1);
		if ((x.mask & bit) == 0) {
			continue;
		}

		if (ss.tellp() > 0) {
			ss << and_symbol;
		}
		if ((x.value & bit) != 0) {
			ss << static_cast<char>((uppercase ? 'A' : 'a') + i);
		} else {
			ss << "(" << not_symbol << static_cast<char>((uppercase ? 'A' : 'a') + i) << ")";
		}
	}

	return ss.str();
}

template <typename T>
std::string get_boolean_expression(const cf::input<T>& x) {
	return get_expression(x, true, "&", "~");
}

template <typename T>
std::string get_cpp_expression(const cf::input<T>& x) {
	return get_expression(x, false, "&&", "!");
}

template <typename T>
std::string get_expression(const std::vector<cf::input<T>>& result, const bool uppercase,
						   const std::string& false_symbol, const std::string& or_symbol,
						   const std::string& and_symbol, const std::string& not_symbol) {
	std::ostringstream ss;
	if (result.size() == 0) {
		ss << false_symbol;
	} else {
		for (size_t i{0}; i < result.size(); i++) {
			ss << "(" << cf::utils::get_expression(result.at(i), uppercase, and_symbol, not_symbol)
			   << ")";
			if (i < result.size() - 1) {
				ss << " " << or_symbol << " ";
			}
		}
	}
	return ss.str();
}

template <typename T>
std::string get_boolean_expression(const std::vector<cf::input<T>>& result) {
	return get_expression(result, true, "0", "+", "&", "~");
}

template <typename T>
std::string get_cpp_expression(const std::vector<cf::input<T>>& result) {
	return get_expression(result, false, "false", "||", "&&", "!");
}

#if defined(CF_LOGGING) && CF_LOGGING == 1
template <typename T>
void debug(const T& msg) {
	std::clog << msg << std::endl;
}
#else
template <typename T>
void debug(const T&) {}
#endif	// CF_LOGGING

}  // namespace utils

}  // namespace cf
