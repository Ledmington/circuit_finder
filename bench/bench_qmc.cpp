#include <chrono>
#include <vector>

#include <benchmark/benchmark.h>

#include <cf.hpp>
#include <functions.hpp>
#include <qmc.hpp>

static void BM_QMC(benchmark::State& state) {
	const size_t nbits = static_cast<size_t>(state.range(0));
	std::vector<cf::input<uint8_t>> dataset;
	// fill dataset
	{
		const uint8_t m = cf::utils::get_mask<uint8_t>(nbits);
		for (size_t i{0}; i < (1u << nbits); i++) {
			const auto a = static_cast<uint8_t>(i);
			const uint8_t result = cf::functions::uint_sqrt(a);
			const uint8_t bit = cf::utils::single_bit<uint8_t>(0);
			if ((result & bit) != 0) {
				dataset.push_back({a, m});
			}
		}
	}

	for (auto _ : state) {	// NOLINT
		const auto start = std::chrono::high_resolution_clock::now();
		auto result = cf::minimize::qmc(dataset, nbits);
		benchmark::DoNotOptimize(result);
		benchmark::ClobberMemory();
		const auto end = std::chrono::high_resolution_clock::now();

		const auto elapsed_seconds =
			std::chrono::duration_cast<std::chrono::duration<double>>(end - start);
		state.SetIterationTime(elapsed_seconds.count());
	}
}
BENCHMARK(BM_QMC)->DenseRange(1, 8)->UseManualTime();

BENCHMARK_MAIN();
