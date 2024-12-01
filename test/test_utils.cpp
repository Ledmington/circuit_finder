#include <iostream>
#include <bitset>

#include "testing_infra.hpp"

#include <cf.hpp>

void test_bit_string() {
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
	testing::run([]() {
		cf::input<uint8_t> x{0b10110011, 0b01111101};
		testing::assert_equals(std::string("-01100-1"), cf::utils::get_bit_string(x));
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
	test_bit_string();
	test_expression();

	testing::report();
}
