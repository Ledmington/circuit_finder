#include "testing_infra.hpp"

#include <functions.hpp>

int main() {
	for (size_t i = 0; i < 100; i++) {
		testing::run([i]() {
			testing::assert_equals(i, cf::functions::uint_sqrt(i * i));
		});
	}

	testing::report();
}
