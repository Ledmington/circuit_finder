#pragma once

#include <vector>

namespace cf {

class logic_function {
   public:
	virtual ~logic_function(){};
	virtual size_t input_bits(const size_t n) = 0;
	virtual size_t output_bits(const size_t n) = 0;
	virtual std::vector<bool> apply(const std::vector<bool> v) = 0;
};

class bitwise_not : public logic_function {
   public:
	size_t input_bits(const size_t n) { return n; }

	size_t output_bits(const size_t n) { return n; }

	std::vector<bool> apply(const std::vector<bool> v) {
		std::vector<bool> result(v.size());
		for (auto i{0}; i < v.size(); i++) {
			result[i] = !v[i];
		}
		return result;
	}
};

}  // namespace cf