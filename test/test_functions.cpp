#include <cstdint>

#include "testing_infra.hpp"

#include <functions.hpp>

int main() {
	for (size_t i = 0; i < 256; i++) {
		testing::run([i]() {
			const uint8_t x = static_cast<uint8_t>(i);
			testing::assert_equals(static_cast<uint8_t>(std::sqrt(static_cast<double>(x))),
								   cf::functions::uint_sqrt(x));
		});
	}

	for (size_t i = 0; i < 256; i++) {
		testing::run([i]() {
			const uint8_t x = static_cast<uint8_t>(i);
			testing::assert_equals(static_cast<uint8_t>(x * x), cf::functions::uint_square(x));
		});
	}

	testing::report();
}
