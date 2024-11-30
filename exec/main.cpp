#include <cassert>
#include <iostream>
#include <cstdint>
#include <vector>
#include <cmath>

#include <cf.hpp>
#include <functions.hpp>
#include <qmc.hpp>

int main() {
	std::vector<std::vector<cf::input<uint8_t>>> dataset(8);

	std::cout << "Filling dataset..." << std::endl;
	for (size_t i{0}; i < 256; i++) {
		const auto a = static_cast<uint8_t>(i);
		const uint8_t result = cf::functions::uint_sqrt(a);
		for (size_t k{0}; k < 8 * sizeof(uint8_t); k++) {
			const uint8_t bit = 1 << (8 * sizeof(uint8_t) - k - 1);
			if ((result & bit) != 0) {
				dataset[k].push_back({a, static_cast<uint8_t>(0b11111111)});
			}
		}
	}
	std::cout << "Dataset filled!" << std::endl << std::endl;

	std::cout << "Dataset size: " << std::endl;
	for (size_t i{0}; i < dataset.size(); i++) {
		std::cout << "  bit [" << i << "] -> " << dataset[i].size() << " entries" << std::endl;
		cf::minimize::qmc(dataset[i], 8);
		std::cout << std::endl;
	}

	return 0;
}
