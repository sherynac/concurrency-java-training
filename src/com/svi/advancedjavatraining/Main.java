package com.svi.advancedjavatraining;

import com.svi.advancedjavatraining.object.City;
import com.svi.advancedjavatraining.object.CityInfo;
import com.svi.advancedjavatraining.object.PopulationData;
import com.svi.advancedjavatraining.object.Province;
import com.svi.advancedjavatraining.utils.JSONFileReader;
import com.svi.advancedjavatraining.utils.PopulationFileWriter;
import com.svi.advancedjavatraining.utils.WebLoader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Main {

	static void main() {
		long startTime = System.nanoTime();

		//TODO START WORK AREA

		try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
			WebLoader webLoader = new WebLoader();
			double totalPopulation = 0;
			PopulationFileWriter fileWriter = new PopulationFileWriter();

			Future<List<Province>> provinceFuture = executor.submit(webLoader::getProvinces);
			Future<List<City>> cityFuture = executor.submit(webLoader :: getCities);

			List<Province> provinceList = provinceFuture.get();
			List<City> cityList = cityFuture.get();


			Map<String, String> provinceKeyToName = new HashMap<>();
			for (Province province : provinceList) {
				provinceKeyToName.put(province.getKey(), province.getName());
			}

			List<Future<PopulationData>> futures = new ArrayList<>();


			for (City city : cityList) {
				String cityName = city.getName();
				String provinceName = provinceKeyToName.get(city.getProvince());

				futures.add(executor.submit( ()-> {
					CityInfo data = new JSONFileReader(provinceName, cityName).getData();

					double population = Optional.ofNullable(data)
							.map(CityInfo::getPopulation)
							.map(Float::doubleValue)
							.orElse(0.0);

					return new PopulationData(provinceName, cityName, population);
				}));
			}

			for (Future<PopulationData> future : futures) {
				PopulationData populationData = future.get();
				fileWriter.addPopulationRecord(populationData);
				totalPopulation += populationData.getPopulation();
			}

			fileWriter.writeToFile();
			System.out.printf("Total population: %,.0f%n", totalPopulation);

		} catch (IOException io) {
			System.out.println("Error has been encountered: " + io.getMessage());
			System.exit(0);
		} catch (ExecutionException | InterruptedException e) {
			System.out.println("Error in processing data" + e.getMessage());
			System.exit(1);
        }

        // TODO END WORK AREA

		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;
		long millis = TimeUnit.NANOSECONDS.toMillis(totalTime);

		long seconds = TimeUnit.NANOSECONDS.toSeconds(totalTime);

		long minutes = TimeUnit.NANOSECONDS.toMinutes(totalTime);

		long hours = TimeUnit.NANOSECONDS.toHours(totalTime);

		String time = String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, millis);
		System.out.println(time);

	}

}
