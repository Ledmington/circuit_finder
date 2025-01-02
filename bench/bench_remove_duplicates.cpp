#include <vector>
#include <random>
#include <chrono>
#include <algorithm>
#include <unordered_set>

#include <benchmark/benchmark.h>

static void BM_RemoveDuplicates_SortUnique(benchmark::State& state) {
	std::mt19937 rnd{42};
	std::uniform_int_distribution<uint8_t> dist{0, 255};
	for (auto _ : state) {	// NOLINT
		std::vector<uint8_t> v;
		for (size_t i{0}; i < static_cast<size_t>(state.range(0)); i++) {
			v.push_back(dist(rnd));
		}
		const auto start = std::chrono::high_resolution_clock::now();
		{
			std::sort(v.begin(), v.end());
			v.erase(std::unique(v.begin(), v.end()), v.end());
		}
		const auto end = std::chrono::high_resolution_clock::now();
		benchmark::DoNotOptimize(v);
		benchmark::ClobberMemory();

		const auto elapsed_seconds =
			std::chrono::duration_cast<std::chrono::duration<double>>(end - start);
		state.SetIterationTime(elapsed_seconds.count());
	}
}
BENCHMARK(BM_RemoveDuplicates_SortUnique)->Range(1, 1048576)->UseManualTime();

static void BM_RemoveDuplicates_Manual(benchmark::State& state) {
	std::mt19937 rnd{42};
	std::uniform_int_distribution<uint8_t> dist{0, 255};
	for (auto _ : state) {	// NOLINT
		std::vector<uint8_t> v;
		for (size_t i{0}; i < static_cast<size_t>(state.range(0)); i++) {
			v.push_back(dist(rnd));
		}
		const size_t n = v.size();
		std::vector<uint8_t> w(n);
		const auto start = std::chrono::high_resolution_clock::now();
		for (size_t i{0}; i < n; i++) {
			bool found = false;
			for (size_t j{i + 1}; j < n; j++) {
				if (v[i] == v[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				w.push_back(v[i]);
			}
		}
		benchmark::DoNotOptimize(w);
		benchmark::ClobberMemory();
		const auto end = std::chrono::high_resolution_clock::now();

		const auto elapsed_seconds =
			std::chrono::duration_cast<std::chrono::duration<double>>(end - start);
		state.SetIterationTime(elapsed_seconds.count());
	}
}
BENCHMARK(BM_RemoveDuplicates_Manual)->Range(1, 1048576)->UseManualTime();

static void BM_RemoveDuplicates_Set(benchmark::State& state) {
	std::mt19937 rnd{42};
	std::uniform_int_distribution<uint8_t> dist{0, 255};
	for (auto _ : state) {	// NOLINT
		std::vector<uint8_t> v;
		for (size_t i{0}; i < static_cast<size_t>(state.range(0)); i++) {
			v.push_back(dist(rnd));
		}
		const auto start = std::chrono::high_resolution_clock::now();
		std::unordered_set<uint8_t> w;
		for (const auto& x : v) {
			w.insert(x);
		}
		const auto end = std::chrono::high_resolution_clock::now();
		benchmark::DoNotOptimize(w);
		benchmark::ClobberMemory();

		const auto elapsed_seconds =
			std::chrono::duration_cast<std::chrono::duration<double>>(end - start);
		state.SetIterationTime(elapsed_seconds.count());
	}
}
BENCHMARK(BM_RemoveDuplicates_Set)->Range(1, 1048576)->UseManualTime();

BENCHMARK_MAIN();
