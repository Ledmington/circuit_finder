#pragma once

#include <cmath>

namespace cf {
namespace functions {

template <typename T>
T uint_sqrt(const T& a) {
	static_assert(std::is_integral_v<T>);
	static_assert(std::is_unsigned_v<T>);
	return static_cast<T>(std::sqrt(static_cast<double>(a)));
}

template <typename T>
T uint_square(const T& a) {
	static_assert(std::is_integral_v<T>);
	static_assert(std::is_unsigned_v<T>);
	return a * a;
}

}  // namespace functions
}  // namespace cf
