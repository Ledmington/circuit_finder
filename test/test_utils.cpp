#include <iostream>
#include <bitset>
#include <limits>
#include <random>

#include "testing_infra.hpp"

#include <cf.hpp>

template <typename T>
void test_popcount() {
	testing::run("popcount_" + cf::utils::get_type_name<T>() + "_min", []() {
		testing::assert_equals(static_cast<T>(0), cf::utils::popcount(static_cast<T>(0)));
	});
	testing::run("popcount_" + cf::utils::get_type_name<T>() + "_max", []() {
		testing::assert_equals(static_cast<T>(8 * sizeof(T)),
							   cf::utils::popcount(std::numeric_limits<T>::max()));
	});

	std::mt19937 rnd{42};
	std::uniform_int_distribution<T> dist{0, std::numeric_limits<T>::max()};
	for (size_t i{0}; i < 100; i++) {
		testing::run("popcount_" + cf::utils::get_type_name<T>() + "_" + std::to_string(i), [&]() {
			T x{dist(rnd)};
			testing::assert_equals(static_cast<T>(std::bitset<8 * sizeof(T)>(x).count()),
								   cf::utils::popcount(x));
		});
	}
}

template <typename T>
void test_single_bit() {
	for (size_t i{0}; i < 8 * sizeof(T); i++) {
		testing::run("SingleBit_" + cf::utils::get_type_name<T>() + "_" + std::to_string(i) + "th",
					 [i]() {
			testing::assert_equals(static_cast<T>(1),
								   cf::utils::popcount(cf::utils::single_bit<T>(i)));
		});
		testing::run("SingleBit_" + cf::utils::get_type_name<T>() + "_" + std::to_string(i) + "th",
					 [i]() {
			T x = cf::utils::single_bit<T>(i);
			testing::assert_equals(static_cast<T>(0), static_cast<T>(x & (x - 1)));
		});
	}
}

template <typename T>
void test_get_mask() {
	for (size_t i{0}; i <= 8 * sizeof(T); i++) {
		testing::run("GetMask_" + cf::utils::get_type_name<T>() + "_" + std::to_string(i), [i]() {
			testing::assert_equals(static_cast<T>(i),
								   cf::utils::popcount(cf::utils::get_mask<T>(i)));
		});
	}
}

template <typename T>
void test_bit_string() {
	for (size_t i{0}; i <= 8 * sizeof(T); i++) {
		testing::run("BitString_" + cf::utils::get_type_name<T>() + "_" + std::to_string(i), [i]() {
			cf::input<T> x{0u, cf::utils::get_mask<T>(i)};
			testing::assert_equals(std::string(8 * sizeof(T) - i, '-') + std::string(i, '0'),
								   cf::utils::get_bit_string(x));
		});
	}
	testing::run([]() {
		cf::input<uint8_t> x{0, 0};
		testing::assert_equals(std::string("--------"), cf::utils::get_bit_string(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b11111111, 0};
		testing::assert_equals(std::string("--------"), cf::utils::get_bit_string(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0, 0b11111111};
		testing::assert_equals(std::string("00000000"), cf::utils::get_bit_string(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b11111111, 0b11111111};
		testing::assert_equals(std::string("11111111"), cf::utils::get_bit_string(x));
	});
}

void test_expression() {
	testing::run([]() {
		cf::input<uint8_t> x{0, 0};
		testing::assert_equals(std::string(""), cf::utils::get_expression(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b11111111, 0};
		testing::assert_equals(std::string(""), cf::utils::get_expression(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0, 0b11111111};
		testing::assert_equals(std::string("(~A)&(~B)&(~C)&(~D)&(~E)&(~F)&(~G)&(~H)"),
							   cf::utils::get_expression(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b11111111, 0b11111111};
		testing::assert_equals(std::string("A&B&C&D&E&F&G&H"), cf::utils::get_expression(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b10110011, 0b01111101};
		testing::assert_equals(std::string("(~B)&C&D&(~E)&(~F)&H"), cf::utils::get_expression(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b10000000, 0b10000000};
		testing::assert_equals(std::string("A"), cf::utils::get_expression(x));
	});
	testing::run([]() {
		cf::input<uint8_t> x{0b00000000, 0b10000000};
		testing::assert_equals(std::string("(~A)"), cf::utils::get_expression(x));
	});
}

int main() {
	test_popcount<uint8_t>();
	test_popcount<uint16_t>();
	test_popcount<uint32_t>();
	test_popcount<uint64_t>();

	test_single_bit<uint8_t>();
	test_single_bit<uint16_t>();
	test_single_bit<uint32_t>();
	test_single_bit<uint64_t>();

	test_get_mask<uint8_t>();
	test_get_mask<uint16_t>();
	test_get_mask<uint32_t>();
	test_get_mask<uint64_t>();

	test_bit_string<uint8_t>();
	test_bit_string<uint16_t>();
	test_bit_string<uint32_t>();
	test_bit_string<uint64_t>();

	test_expression();

	testing::report();
}
