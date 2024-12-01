#include <cassert>
#include <iostream>
#include <cstdint>
#include <vector>
#include <cmath>

#include <cf.hpp>
#include <functions.hpp>
#include <qmc.hpp>

int main() {
	const size_t nbits = 8;
	using data_type = uint8_t;
	std::vector<std::vector<cf::input<data_type>>> dataset(nbits);

	std::cout << "Filling dataset..." << std::endl;
	for (size_t i{0}; i < (1 << nbits); i++) {
		const auto a = static_cast<data_type>(i);
		const data_type result = cf::functions::uint_sqrt(a);
		for (size_t k{0}; k < 8 * sizeof(data_type); k++) {
			const data_type bit = 1 << (8 * sizeof(data_type) - k - 1);
			if ((result & bit) != 0) {
				dataset[k].push_back({a, static_cast<data_type>(~0)});
			}
		}
	}
	std::cout << "Dataset filled!" << std::endl << std::endl;

	std::cout << "Dataset size: " << std::endl;
	for (size_t i{0}; i < dataset.size(); i++) {
		std::cout << "  bit [" << i << "] -> " << dataset[i].size() << " entries" << std::endl;
		std::cout << std::endl;
	}

	std::cout << std::endl;
	std::cout << "Minimizing functions..." << std::endl;
	std::vector<std::vector<cf::input<data_type>>> result;
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

	return 0;
}
