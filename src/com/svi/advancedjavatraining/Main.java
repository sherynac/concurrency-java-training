package com.svi.advancedjavatraining;

import com.svi.advancedjavatraining.object.City;
import com.svi.advancedjavatraining.object.CityInfo;
import com.svi.advancedjavatraining.object.PopulationData;
import com.svi.advancedjavatraining.object.Province;
import com.svi.advancedjavatraining.utils.JSONFileReader;
import com.svi.advancedjavatraining.utils.PopulationFileWriter;
import com.svi.advancedjavatraining.utils.WebLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) {
		long startTime = System.nanoTime();

		//TODO START WORK AREA
		try {
			WebLoader webLoader = new WebLoader();

			List<Province> provinceList = webLoader.getProvinces();
			List<City> cityList = webLoader.getCities();



			double totalPopulation = 0;
			PopulationFileWriter fileWriter = new PopulationFileWriter();

			Map<String, String> provinceKeyToName = new HashMap<>();
			for (Province province : provinceList) {
				provinceKeyToName.put(province.getKey(), province.getName());
			}

			Map<String, String> cityToProvinceMap = new HashMap<>();

			for (City city : cityList) {
				String cityName = city.getName();
				String provinceName = provinceKeyToName.get(city.getProvince());

				cityToProvinceMap.put(cityName, provinceName);

				JSONFileReader jsonFileReader = new JSONFileReader(provinceName, cityName);
				CityInfo data = jsonFileReader.getData();

				float population = Optional.ofNullable(data)
						.map(CityInfo::getPopulation)
						.orElse(0.0f);

				PopulationData populationData = new PopulationData(provinceName, cityName, population);
				fileWriter.addPopulationRecord(populationData);

				totalPopulation += population ;
			}

			System.out.printf("Total population: %,.0f%n", totalPopulation);

		} catch (IOException io) {
			System.out.println("Error has been encountered: " + io.getMessage());
			System.exit(0);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
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
