#include <cassert>
#include <iostream>
#include <cstdint>
#include <vector>
#include <cmath>
#include <iomanip>

#include <cf.hpp>
#include <functions.hpp>
#include <qmc.hpp>

enum class output_format : uint8_t { BOOL, CPP };

std::string get_string(const output_format& out_fmt) {
	switch (out_fmt) {
		case output_format::BOOL:
			return "BOOL";
		case output_format::CPP:
			return "CPP";
		default:
			return "UNKNOWN";
	}
}

template <typename T>
void fill_dataset(std::vector<cf::input<T>>& dataset, const size_t nbits, const size_t bit) {
	assert(dataset.size() == 0);
	assert(nbits <= 8 * sizeof(T));
	assert(bit < nbits);

	dataset.reserve(1u << nbits);

	const T m = cf::utils::get_mask<T>(nbits);
	const T bit_mask = cf::utils::single_bit<T>(nbits - bit - 1);

	for (size_t i{0}; i < (1u << nbits); i++) {
		const auto a = static_cast<T>(i);
		// const T result = cf::functions::uint_sqrt(a);
		const T result = cf::functions::uint_square(a);
		if ((result & bit_mask) != 0) {
			dataset.push_back({a, m});
		}
	}
	dataset.shrink_to_fit();
}

void die(const std::string& msg) {
	std::cerr << std::endl;
	std::cerr << msg << std::endl;
	std::cerr << std::endl;
	std::exit(-1);
}

template <typename T>
void run(const size_t nbits, const size_t bit, const output_format& out_fmt) {
	std::vector<cf::input<T>> dataset;

	std::cout << "Filling dataset..." << std::endl;
	fill_dataset(dataset, nbits, bit);
	std::cout << "Dataset filled!" << std::endl << std::endl;

	std::cout << "Dataset size: " << std::endl;
	std::cout << "  bit [" << bit << "] -> " << dataset.size() << " entries out of " << (1 << nbits)
			  << " (" << std::fixed << std::setprecision(2)
			  << (static_cast<double>(dataset.size()) / static_cast<double>(1 << nbits) * 100.0)
			  << "%)" << std::endl;
	std::cout << std::endl;

	std::cout << std::endl;
	std::cout << "Minimizing functions..." << std::endl;
	std::vector<cf::input<T>> result = cf::minimize::qmc(dataset, nbits);

	if (out_fmt == output_format::BOOL) {
		std::cout << std::endl;
		std::cout << "Minimized function for bit n." << bit << ":" << std::endl;
		std::cout << "  ";
		if (result.size() == 0) {
			std::cout << "0";
		} else {
			for (size_t i{0}; i < result.size(); i++) {
				std::cout << "(" << cf::utils::get_boolean_expression(result.at(i)) << ")";
				if (i < result.size() - 1) {
					std::cout << " + ";
				}
			}
		}
		std::cout << std::endl;
	} else if (out_fmt == output_format::CPP) {
		std::cout << std::endl;
		std::cout << "bool uint_sqrt_" << nbits << "_" << bit << "(";
		for (size_t i{0}; i < nbits; i++) {
			std::cout << "const bool " << static_cast<char>('a' + i);
			if (i < nbits - 1) {
				std::cout << ", ";
			}
		}
		std::cout << ") {" << std::endl;
		std::cout << "    return ";
		if (result.size() == 0) {
			std::cout << "false";
		} else {
			for (size_t i{0}; i < result.size(); i++) {
				std::cout << "(" << cf::utils::get_cpp_expression(result.at(i)) << ")";
				if (i < result.size() - 1) {
					std::cout << " || ";
				}
			}
		}
		std::cout << ";" << std::endl;
		std::cout << "}" << std::endl;
		std::cout << std::endl;
	} else {
		die("Unknown output_format value: " + get_string(out_fmt));
	}
}

int main(const int argc, const char** argv) {
	size_t nbits = 8;
	size_t bit = 0;
	output_format out_fmt = output_format::BOOL;
	for (int i{1}; i < argc; i++) {
		const std::string arg = std::string(argv[i]);
		if (arg == "-h" || arg == "--help") {
			std::cout << std::endl;
			std::cout << "  Circuit Finder v0.1.0" << std::endl;
			std::cout << std::endl;
			std::cout << "Flags:" << std::endl;
			std::cout << " -h, --help      Displays this message and exits." << std::endl;
			std::cout << " -n, --nbits     Sets the number of bits of the operation." << std::endl
					  << "                 Must be >0. Default: " << nbits << "." << std::endl;
			std::cout << " -b, --bit       Sets the bit to minimize the expression of." << std::endl
					  << "                 Must be >=0 and <nbits. Default: " << bit << "."
					  << std::endl;
			std::cout << " -o, --output    Sets the format of the output." << std::endl;
			std::cout << "                 Must be one of: bool, cpp. Default: "
					  << get_string(out_fmt) << "." << std::endl;
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
		} else if (arg == "-o" || arg == "--output") {
			i++;
			if (i >= argc) {
				die("'-o'/'--output' requires an additional argument.");
			}
			std::string arg_i = std::string(argv[i]);
			if (arg_i == "bool") {
				out_fmt = output_format::BOOL;
			} else if (arg_i == "cpp") {
				out_fmt = output_format::CPP;
			} else {
				die("Unknown output format '" + arg_i + "'. Must be one of: bool, cpp.");
			}
		} else {
			die("Error: Unknown argument: '" + arg + "'.");
		}
	}

	if (nbits <= 8) {
		run<uint8_t>(nbits, bit, out_fmt);
	} else if (nbits <= 16) {
		run<uint16_t>(nbits, bit, out_fmt);
	} else if (nbits <= 32) {
		run<uint32_t>(nbits, bit, out_fmt);
	} else if (nbits <= 64) {
		run<uint64_t>(nbits, bit, out_fmt);
	} else {
		die("More than 64 bits are not supported.");
	}

	return 0;
}
