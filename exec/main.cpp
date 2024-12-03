#include <cassert>
#include <iostream>
#include <cstdint>
#include <vector>
#include <cmath>

#include <iomanip>

#include <cf.hpp>
#include <functions.hpp>
#include <qmc.hpp>

template <typename T>
void fill_dataset(std::vector<std::vector<cf::input<T>>>& dataset, const size_t nbits) {
	assert(nbits <= 8 * sizeof(T));
	const T m = cf::utils::get_mask<T>(nbits);
	for (size_t i{0}; i < (1u << nbits); i++) {
		const auto a = static_cast<T>(i);
		const T result = cf::functions::uint_sqrt(a);
		for (size_t k{0}; k < nbits; k++) {
			const T bit = cf::utils::single_bit<T>(nbits - k - 1);
			if ((result & bit) != 0) {
				dataset[k].push_back({a, m});
			}
		}
	}
}

template <typename T>
void run(const size_t nbits) {
	std::vector<std::vector<cf::input<T>>> dataset(nbits);

	std::cout << "Filling dataset..." << std::endl;
	fill_dataset(dataset, nbits);
	std::cout << "Dataset filled!" << std::endl << std::endl;

	std::cout << "Dataset size: " << std::endl;
	for (size_t i{0}; i < dataset.size(); i++) {
		std::cout << "  bit [" << i << "] -> " << dataset[i].size() << " entries" << std::endl;
		std::cout << std::endl;
	}

	std::cout << std::endl;
	std::cout << "Minimizing functions..." << std::endl;
	std::vector<std::vector<cf::input<T>>> result;
	for (size_t i{0}; i < dataset.size(); i++) {
		result.push_back(cf::minimize::qmc(dataset[i], nbits));
	}

	std::cout << std::endl;
	for (size_t i{0}; i < result.size(); i++) {
		std::cout << "Minimized function for bit n." << i << ":" << std::endl;
		std::cout << "  ";
		if (result[i].size() == 0) {
			std::cout << "0";
		} else {
			for (size_t j{0}; j < result[i].size(); j++) {
				std::cout << "(" << cf::utils::get_expression(result[i][j]) << ")";
				if (j < result[i].size() - 1) {
					std::cout << " + ";
				}
			}
		}
		std::cout << std::endl;
	}
}

int main() {
	const size_t nbits = 10;
	assert(nbits > 0);

	if (nbits <= 8) {
		run<uint8_t>(nbits);
	} else if (nbits <= 16) {
		run<uint16_t>(nbits);
	} else if (nbits <= 32) {
		run<uint32_t>(nbits);
	} else if (nbits <= 64) {
		run<uint64_t>(nbits);
	} else {
		std::cerr << "More than 64 bits are not supported." << std::endl;
	}

	return 0;
}
