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
		// const T result = cf::functions::uint_sqrt(a);
		const T result = cf::functions::uint_square(a);
		for (size_t k{0}; k < nbits; k++) {
			const T bit = cf::utils::single_bit<T>(nbits - k - 1);
			if ((result & bit) != 0) {
				dataset[k].push_back({a, m});
			}
		}
	}
	for (auto& v : dataset) {
		v.shrink_to_fit();
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
		std::cout << "  bit [" << i << "] -> " << dataset[i].size() << " entries out of "
				  << (1 << nbits) << " (" << std::fixed << std::setprecision(2)
				  << (static_cast<double>(dataset[i].size()) / static_cast<double>(1 << nbits) *
					  100.0)
				  << "%)" << std::endl;
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

void die(const std::string& msg) {
	std::cerr << std::endl;
	std::cerr << msg << std::endl;
	std::cerr << std::endl;
	std::exit(-1);
}

int main(const int argc, const char** argv) {
	size_t nbits = 8;
	size_t bit = 0;
	for (int i{1}; i < argc; i++) {
		const std::string arg = std::string(argv[i]);
		if (arg == "-h" || arg == "--help") {
			std::cout << std::endl;
			std::cout << "  Circuit Finder v0.1.0" << std::endl;
			std::cout << std::endl;
			std::cout << "Flags:" << std::endl;
			std::cout << " -h, --help        Displays this message and exits." << std::endl;
			std::cout << " -n, --nbits       Sets the number of bits of the operation." << std::endl
					  << "                   Must be >0. Default: " << nbits << "." << std::endl;
			std::cout << " -b, --bit         Sets the bit to minimize the expression of."
					  << std::endl
					  << "                   Must be >=0 and <nbits. Default: " << bit << "."
					  << std::endl;
			std::cout << "     --operation   Sets the bit to minimize the expression of."
					  << std::endl
					  << "                   Must be >=0 and <nbits. Default: " << bit << "."
					  << std::endl;
			std::cout << std::endl;
			return 0;
		} else if (arg == "-n" || arg == "--nbits") {
			i++;
			if (i >= argc) {
				die("'-n'/'--nbits' requires an additional argument.");
			}
			nbits = static_cast<size_t>(std::stoi(argv[i]));
			if (nbits == 0) {
				die("nbits must be >0.");
			}
		} else if (arg == "-b" || arg == "--bit") {
			i++;
			if (i >= argc) {
				die("'-b'/'--bit' requires an additional argument.");
			}
			bit = static_cast<size_t>(std::stoi(argv[i]));
			if (bit >= nbits) {
				die("bit must be >=0 and <" + std::to_string(nbits) + ".");
			}
		} else {
			die("Error: Unknown argument: '" + arg + "'.");
		}
	}

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
