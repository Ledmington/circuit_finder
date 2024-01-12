#pragma once

#include <vector>

namespace cf {

class logic_function {
   public:
	virtual size_t input_bits(const size_t n) { return 0; }
	virtual size_t output_bits(const size_t n) { return 0; }
	virtual std::vector<bool> apply(const std::vector<bool> v) { return v; }
};

class bitwise_not : public logic_function {
   public:
	size_t input_bits(const size_t n) override { return n; }

	size_t output_bits(const size_t n) override { return n; }

	std::vector<bool> apply(const std::vector<bool> v) override {
		std::vector<bool> result(v.size());
		for (auto i{0}; i < v.size(); i++) {
			result[i] = !v[i];
		}
		return result;
	}
};

}  // namespace cf