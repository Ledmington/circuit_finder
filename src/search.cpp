#include <cstdint>
#include <cstdio>
#include <string>
#include <vector>

#include <cf/logic_function.hpp>

namespace cf {

static std::vector<bool> convert_to_bitarray(const uint64_t x,
											 const size_t bits) {
	std::vector<bool> v(bits);
	for (auto i{0}; i < bits; i++) {
		v[i] = (x & (1ull << i)) != 0;
	}
	return v;
}

static std::string convert_to_string(const std::vector<bool> v) {
	char arr[v.size() + 1];
	for (auto i{0}; i < v.size(); i++) {
		arr[i] = v[i] ? '1' : '0';
	}
	arr[v.size()] = '\0';
	return std::string(arr);
}

void search(const uint64_t limit, const size_t bits, logic_function fun) {
	for (uint64_t i{0u}; i < limit; i++) {
		const std::vector<bool> input = convert_to_bitarray(i, bits);
		const std::vector<bool> result = fun.apply(input);
		printf("0x%016lx -> %s\n", i, convert_to_string(result).c_str());
	}
}

}  // namespace cf