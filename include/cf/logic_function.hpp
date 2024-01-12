#pragma once

#include <vector>

namespace cf {

class logic_function {
   public:
	virtual size_t input_bits(const size_t n) = 0;
	virtual size_t output_bits(const size_t n) = 0;
	virtual std::vector<bool> apply(const std::vector<bool> v) = 0;
};

class bitwise_not : public logic_function {
   public:
	size_t input_bits(const size_t n) { return n; }
	size_t output_bits(const size_t n) { return n; }
	std::vector<bool> apply(const std::vector<bool> v) { return v; }
};

}  // namespace cf